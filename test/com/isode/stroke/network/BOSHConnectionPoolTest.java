/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.SafeString;
import com.isode.stroke.base.URL;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.network.BOSHConnection.BOSHError;
import com.isode.stroke.parser.PlatformXMLParserFactory;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.tls.TLSOptions;

/**
 * Tests for {@link BOSHConnectionPool}
 */
public class BOSHConnectionPoolTest {

    private DummyEventLoop eventLoop = new DummyEventLoop();
    private MockConnectionFactory connectionFactory = new MockConnectionFactory(eventLoop);
    private List<String> xmppDataRead = new ArrayList<String>();
    private List<String> boshDataRead = new ArrayList<String>();
    private List<String> boshDataWritten = new ArrayList<String>();
    private PlatformXMLParserFactory parserFactory = new PlatformXMLParserFactory();
    private StaticDomainNameResolver resolver = new StaticDomainNameResolver(eventLoop);
    private TimerFactory timerFactory = new DummyTimerFactory();
    private String to = "wonderland.lit";
    private String path = "/http-bind";
    private String port = "5280";
    private String sid = "MyShinySID";
    private String initial = "<body wait='60' "
            +"inactivity='30' "
            +"polling='5' "
            +"requests='2' "
            +"hold='1' "
            +"maxpause='120' "
            +"sid='" + sid + "' "
            +"ver='1.6' "
            +"from='wonderland.lit' "
            +"xmlns='http://jabber.org/protocol/httpbind'/>";
    private URL boshURL = new URL("http", to, 5280, path);
    private long initialRID = 2349876;
    private int sessionStarted = 0;
    private int sessionTerminated = 0;
    
    @Before
    public void setUp() {
        resolver.addAddress(to, new HostAddress("127.0.0.1"));
    }
    
    @Test
    public void testConnectionCount_OneWrite() {
        BOSHConnectionPool testling = createTestling();
        assertEquals(1,connectionFactory.connections.size());
        eventLoop.processEvents();
        assertEquals(0,sessionStarted);
        readResponse(initial, connectionFactory.connections.get(0));
        assertEquals(1,sessionStarted);
        assertEquals(1,connectionFactory.connections.size());
        testling.write(new SafeByteArray("<blah/>"));
        eventLoop.processEvents();
        assertEquals(1,connectionFactory.connections.size());
        assertEquals(1,sessionStarted);
    }
    
    @Test
    public void testConnectionCount_TwoWrites() {
        BOSHConnectionPool testling = createTestling();
        assertEquals(1,connectionFactory.connections.size());
        eventLoop.processEvents();
        readResponse(initial, connectionFactory.connections.get(0));
        eventLoop.processEvents();
        testling.write(new SafeByteArray("<blah/>"));
        eventLoop.processEvents();
        assertEquals(1,connectionFactory.connections.size());
        testling.write(new SafeByteArray("<bleh/>"));
        eventLoop.processEvents();
        eventLoop.processEvents();
        assertEquals(2,connectionFactory.connections.size());
    }
    
    @Test
    public void testConnectionCount_ThreeWrites() {
        BOSHConnectionPool testling = createTestling();
        assertEquals(1,connectionFactory.connections.size());
        eventLoop.processEvents();
        readResponse(initial,connectionFactory.connections.get(0));
        testling.restartStream();
        readResponse("<body/>",connectionFactory.connections.get(0));
        testling.restartStream();
        readResponse("<body/>",connectionFactory.connections.get(0));
        testling.write(new SafeByteArray("<blah/>"));
        testling.write(new SafeByteArray("<bleh/>"));
        testling.write(new SafeByteArray("<bluh/>"));
        eventLoop.processEvents();
        assertTrue("2 < "+connectionFactory.connections.size(),
                2 >= connectionFactory.connections.size());
    }
    
    @Test
    public void testConnectionCount_ThreeWrites_ManualConnect() {
        connectionFactory.autoFinishConnect = false;
        BOSHConnectionPool testling = createTestling();
        assertEquals(1,connectionFactory.connections.size());
        assertEquals(0,boshDataWritten.size()); // Connection not connected yet, can't send data
        
        connectionFactory.connections.get(0).onConnectFinished.emit(false);
        eventLoop.processEvents();
        assertEquals(1,boshDataWritten.size());
        
        readResponse(initial, connectionFactory.connections.get(0));
        eventLoop.processEvents();
        assertEquals(1,connectionFactory.connections.size());
        assertEquals(1,boshDataWritten.size()); // Don't respond to initial data with a holding call

        testling.restartStream();;
        eventLoop.processEvents();
        readResponse("<body/>", connectionFactory.connections.get(0));
        eventLoop.processEvents();
        testling.restartStream();
        eventLoop.processEvents();
        
        testling.write(new SafeByteArray("<blah/>"));
        eventLoop.processEvents();
        assertEquals(2,connectionFactory.connections.size());
        assertEquals(3,boshDataWritten.size()); // New connection isn't up yet

        connectionFactory.connections.get(1).onConnectFinished.emit(false);
        eventLoop.processEvents();
        assertEquals(4,boshDataWritten.size()); // New Connection ready
        
        testling.write(new SafeByteArray("<bleh/>"));
        eventLoop.processEvents();
        testling.write(new SafeByteArray("<bluh/>"));
        assertEquals(4,boshDataWritten.size()); // New data can't be sent, no free connections
        eventLoop.processEvents();
        assertTrue("2 < "+connectionFactory.connections.size(),
                2 >= connectionFactory.connections.size());
    }
    
    @Test
    public void testConnectionCount_ThreeWritesTwoReads() {
        MockConnection c0 = null;
        MockConnection c1 = null;
        long rid = initialRID;
        
        BOSHConnectionPool testling = createTestling();
        assertEquals(1,connectionFactory.connections.size());
        c0 = connectionFactory.connections.get(0);
        eventLoop.processEvents();
        assertEquals(1,boshDataWritten.size()); // header
        
        rid++;
        readResponse(initial, c0);
        assertEquals(1,boshDataWritten.size());
        assertEquals(1,connectionFactory.connections.size());
        assertFalse(c0.pending);
        
        rid++;
        testling.restartStream();
        eventLoop.processEvents();
        readResponse("<body/>", connectionFactory.connections.get(0));
        
        rid++;
        testling.write(new SafeByteArray("<blah/>"));
        eventLoop.processEvents();
        assertEquals(2,connectionFactory.connections.size()); // 0 was waiting for response, open and send on 1
        assertEquals(4,boshDataWritten.size()); // data
        c1 = connectionFactory.connections.get(1);
        String fullBody = "<body rid='" + rid + "' sid='" + sid + 
                "' xmlns='http://jabber.org/protocol/httpbind'><blah/></body>"; // Check empty write
        assertEquals(fullBody,lastBody());
        assertTrue(c0.pending);
        assertTrue(c1.pending);
        
        rid++;
        readResponse("<body xmlns='http://jabber.org/protocol/httpbind'><message><splatploing/></message></body>", c0); // Doesn't include necessary attributes - as the support is improved this'll start to fail
        eventLoop.processEvents();
        assertFalse(c0.pending);
        assertTrue(c1.pending);
        assertEquals(4,boshDataWritten.size()); // don't send empty in [0], still have [1] waiting 
        assertEquals(2,connectionFactory.connections.size());
        
        rid++;
        readResponse("<body xmlns='http://jabber.org/protocol/httpbind'><message><splatploing><blittlebarg/></splatploing></message></body>", c1);
        eventLoop.processEvents();
        assertFalse(c1.pending);
        assertTrue(c0.pending);
        assertEquals(5,boshDataWritten.size()); // Empty to make room
        assertEquals(2,connectionFactory.connections.size());
        
        rid++;
        testling.write(new SafeByteArray("<bleh/>"));
        eventLoop.processEvents();
        assertTrue(c0.pending);
        assertTrue(c1.pending);
        assertEquals(6,boshDataWritten.size());
        
        rid++;
        testling.write(new SafeByteArray("<blush/>"));
        assertTrue(c0.pending);
        assertTrue(c1.pending);
        assertEquals(6,boshDataWritten.size()); //Don't send data, no room
        eventLoop.processEvents();
        assertEquals(2,connectionFactory.connections.size());
    }
    
    @Test
    public void testSession() {
        to = "prosody.doomsong.co.uk";
        resolver.addAddress("prosody.doomsong.co.uk",new HostAddress("127.0.0.1"));
        path = "/http-bind/";
        boshURL = new URL("http", to, 5280, path);

        BOSHConnectionPool testling = createTestling();
        assertEquals(1,connectionFactory.connections.size());
        eventLoop.processEvents();
        assertEquals(1,boshDataWritten.size()); // header
        assertEquals(1,connectionFactory.connections.size());

        String response = "<body authid='743da605-4c2e-4de1-afac-ac040dd4a940' xmpp:version='1.0' xmlns:stream='http://etherx.jabber.org/streams' xmlns:xmpp='urn:xmpp:xbosh' inactivity='60' wait='60' polling='5' secure='true' hold='1' from='prosody.doomsong.co.uk' ver='1.6' sid='743da605-4c2e-4de1-afac-ac040dd4a940' requests='2' xmlns='http://jabber.org/protocol/httpbind'><stream:features><auth xmlns='http://jabber.org/features/iq-auth'/><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>SCRAM-SHA-1</mechanism><mechanism>DIGEST-MD5</mechanism></mechanisms></stream:features></body>";
        readResponse(response, connectionFactory.connections.get(0));
        eventLoop.processEvents();
        assertEquals(1,boshDataWritten.size());
        assertEquals(1,connectionFactory.connections.size());

        String send = "<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"SCRAM-SHA-1\">biwsbj1hZG1pbixyPWZhOWE5ZDhiLWZmMDctNGE4Yy04N2E3LTg4YWRiNDQxZGUwYg==</auth>";
        testling.write(new SafeByteArray(send));
        eventLoop.processEvents();
        assertEquals(2,boshDataWritten.size());
        assertEquals(1,connectionFactory.connections.size());

        response = "<body xmlns='http://jabber.org/protocol/httpbind' sid='743da605-4c2e-4de1-afac-ac040dd4a940' xmlns:stream = 'http://etherx.jabber.org/streams'><challenge xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>cj1mYTlhOWQ4Yi1mZjA3LTRhOGMtODdhNy04OGFkYjQ0MWRlMGJhZmZlMWNhMy1mMDJkLTQ5NzEtYjkyNS0yM2NlNWQ2MDQyMjYscz1OVGd5WkdWaFptTXRaVE15WXkwMFpXUmhMV0ZqTURRdFpqYzRNbUppWmpGa1pqWXgsaT00MDk2</challenge></body>";
        readResponse(response, connectionFactory.connections.get(0));
        eventLoop.processEvents();
        assertEquals(2,boshDataWritten.size());
        assertEquals(1,connectionFactory.connections.size());

        send = "<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">Yz1iaXdzLHI9ZmE5YTlkOGItZmYwNy00YThjLTg3YTctODhhZGI0NDFkZTBiYWZmZTFjYTMtZjAyZC00OTcxLWI5MjUtMjNjZTVkNjA0MjI2LHA9aU11NWt3dDN2VWplU2RqL01Jb3VIRldkZjBnPQ==</response>";
        testling.write(new SafeByteArray(send));
        eventLoop.processEvents();
        assertEquals(3,boshDataWritten.size());
        assertEquals(1,connectionFactory.connections.size());

        response = "<body xmlns='http://jabber.org/protocol/httpbind' sid='743da605-4c2e-4de1-afac-ac040dd4a940' xmlns:stream = 'http://etherx.jabber.org/streams'><success xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>dj1YNmNBY3BBOWxHNjNOOXF2bVQ5S0FacERrVm89</success></body>";
        readResponse(response, connectionFactory.connections.get(0));
        eventLoop.processEvents();
        assertEquals(3,boshDataWritten.size());
        assertEquals(1,connectionFactory.connections.size());

        testling.restartStream();
        eventLoop.processEvents();
        assertEquals(4,boshDataWritten.size());
        assertEquals(1,connectionFactory.connections.size());

        response = "<body xmpp:version='1.0' xmlns:stream='http://etherx.jabber.org/streams' xmlns:xmpp='urn:xmpp:xbosh' inactivity='60' wait='60' polling='5' secure='true' hold='1' from='prosody.doomsong.co.uk' ver='1.6' sid='743da605-4c2e-4de1-afac-ac040dd4a940' requests='2' xmlns='http://jabber.org/protocol/httpbind'><stream:features><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><required/></bind><session xmlns='urn:ietf:params:xml:ns:xmpp-session'><optional/></session><sm xmlns='urn:xmpp:sm:2'><optional/></sm></stream:features></body>";
        readResponse(response, connectionFactory.connections.get(0));
        eventLoop.processEvents();
        assertEquals(5,boshDataWritten.size()); // Now we've authed (restarted) we should be keeping one query in flight so the server can reply to us at any time it wants.
        assertEquals(1,connectionFactory.connections.size());

        send = "<body rid='2821988967416214' sid='cf663f6b94279d4f' xmlns='http://jabber.org/protocol/httpbind'><iq id='session-bind' type='set'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><resource>d5a9744036cd20a0</resource></bind></iq></body>";
        testling.write(new SafeByteArray(send));
        eventLoop.processEvents();
        assertEquals(6,boshDataWritten.size());
        assertEquals(2,connectionFactory.connections.size());
    }
    
    @Test
    public void testWrite_Empty() {
        MockConnection c0 = null;

        BOSHConnectionPool testling = createTestling();
        assertEquals(1,connectionFactory.connections.size());
        c0 = connectionFactory.connections.get(0);

        readResponse(initial, c0);
        eventLoop.processEvents();
        assertEquals(1,boshDataWritten.size()); // Shouldn't have sent anything extra
        eventLoop.processEvents();
        testling.restartStream();
        eventLoop.processEvents();
        assertEquals(2,boshDataWritten.size());
        readResponse("<body></body>",c0);
        eventLoop.processEvents();
        assertEquals(3,boshDataWritten.size());
        String fullBody = "<body rid='" + (initialRID + 2) + "' sid='" + sid + "' xmlns='http://jabber.org/protocol/httpbind'></body>";
        String response = boshDataWritten.get(2);
        int bodyPosition = response.indexOf("\r\n\r\n");
        assertEquals(fullBody,response.substring(bodyPosition+4));
    }
    

    private static class MockConnection extends Connection {

        private final EventLoop eventLoop;
        private HostAddressPort hostAddressPort;
        private final List<HostAddressPort> failingPorts;
        private final ByteArray dataWritten = new ByteArray();
        private boolean disconnected;
        private boolean pending;
        private boolean autoFinishConnect;
        
        private MockConnection(Collection<? extends HostAddressPort> failingPorts,
                EventLoop eventLoop,boolean autoFinishConnect) {
            this.eventLoop = eventLoop;
            this.failingPorts = new ArrayList<HostAddressPort>(failingPorts);
            disconnected = false;
            pending = false;
            this.autoFinishConnect = autoFinishConnect;
        }
        
        @Override
        public void listen() {
            fail();
        }

        @Override
        public void connect(HostAddressPort address) {
            hostAddressPort = address;
            final boolean fail = failingPorts.contains(address);
            if (autoFinishConnect) {
                eventLoop.postEvent(new Callback() {
                    
                    @Override
                    public void run() {
                        onConnectFinished.emit(fail);
                    }
                });
            }
        }

        @Override
        public void disconnect() {
            disconnected = true;
            onDisconnected.emit(null);
        }

        @Override
        public void write(SafeByteArray data) {
            dataWritten.append(data);
            pending = true;
        }

        @Override
        public HostAddressPort getLocalAddress() {
            return new HostAddressPort();
        }
        
        public HostAddressPort getRemoteAddress() {
            return new HostAddressPort();
        }

    }
    
    private static class MockConnectionFactory implements ConnectionFactory {

        private final EventLoop eventLoop;
        private List<MockConnection> connections = new ArrayList<MockConnection>();
        private List<HostAddressPort> failingPorts = new ArrayList<HostAddressPort>();
        private boolean autoFinishConnect;
        
        private MockConnectionFactory(EventLoop eventLoop) {
            this(eventLoop,true);
        }
        
        private MockConnectionFactory(EventLoop eventLoop,boolean autoFinishConnect) {
            this.eventLoop = eventLoop;
            this.autoFinishConnect = autoFinishConnect;
        }
        
        @Override
        public Connection createConnection() {
            MockConnection connection = 
                    new MockConnection(failingPorts, eventLoop, autoFinishConnect);
            connections.add(connection);
            return connection;
        }
        
    }
    
    private BOSHConnectionPool createTestling() {
        // make_shared is limited to 9 arguments; instead new is used here.
        BOSHConnectionPool pool = new BOSHConnectionPool(boshURL, resolver, connectionFactory, parserFactory, 
                null, timerFactory, eventLoop, to, initialRID, 
                new URL(), new SafeString(""), 
                new SafeString(""), new TLSOptions());
        pool.open();
        pool.onXMPPDataRead.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handleXMPPDataRead(data);
            }
            
        });
        pool.onBOSHDataRead.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handleBOSHDataRead(data);
            }
        });
        pool.onBOSHDataWritten.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handleBOSHDataWritten(data);
            }
            
        
        });
        pool.onSessionStarted.connect(new Slot() {
            
            @Override
            public void call() {
                handleSessionStarted();
            }
            
        });
        pool.onSessionTerminated.connect(new Slot1<BOSHConnection.BOSHError>() {

            @Override
            public void call(BOSHError error) {
                handleSessionTerminated();
            }
            
        });
        eventLoop.processEvents();
        eventLoop.processEvents();
        return pool;
    }

    private String lastBody() {
        String response = boshDataWritten.get(boshDataWritten.size() - 1);
        int bodyPosition = response.indexOf("\r\n\r\n");
        return response.substring(bodyPosition+4);
    }

    private void handleXMPPDataRead(SafeByteArray d) {
        xmppDataRead.add(d.toString());
    }

    private void handleBOSHDataRead(SafeByteArray d) {
        boshDataRead.add(d.toString());
    }

    private void handleBOSHDataWritten(SafeByteArray d) {
        boshDataWritten.add(d.toString());
    }

    private void handleSessionStarted() {
        sessionStarted++;
    }

    private void handleSessionTerminated() {
        sessionTerminated++;
    }
    
    private void readResponse(String response, MockConnection connection) {
        connection.pending = false;
        SafeByteArray data1 = new SafeByteArray(
            "HTTP/1.1 200 OK\r\n"
            +"Content-Type: text/xml; charset=utf-8\r\n"
            +"Access-Control-Allow-Origin: *\r\n"
            +"Access-Control-Allow-Headers: Content-Type\r\n"
            +"Content-Length: ");
        connection.onDataRead.emit(data1);
        SafeByteArray data2 = new SafeByteArray(String.valueOf(response.length()));
        connection.onDataRead.emit(data2);
        SafeByteArray data3 = new SafeByteArray("\r\n\r\n");
        connection.onDataRead.emit(data3);
        SafeByteArray data4 = new SafeByteArray(response);
        connection.onDataRead.emit(data4);
    }

    private String fullRequestFor(String data) {
        String result = "POST /" + path + " HTTP/1.1\r\n"
                    + "Host: " + to + ":" + port + "\r\n"
                    + "Content-Type: text/xml; charset=utf-8\r\n"
                    + "Content-Length: " + data.length() + "\r\n\r\n"
                    + data;
        return result;
    }

}
