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

import org.junit.After;
import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.URL;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.network.BOSHConnection.Pair;
import com.isode.stroke.parser.PlatformXMLParserFactory;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.tls.TLSOptions;

/**
 * Test for {@link BoshConnection}
 */
public class BoshConnectionTest {

    private final DummyEventLoop eventLoop = new DummyEventLoop();
    private final MockConnectionFactory connectionFactory = new MockConnectionFactory(eventLoop);
    private boolean connectFinished = false;
    private boolean connectFinishedWithError = false;
    private boolean disconnected = false;
    private boolean disconnectedError = false;
    private final ByteArray dataRead = new ByteArray();
    private final PlatformXMLParserFactory parserFactory = new PlatformXMLParserFactory();
    private final StaticDomainNameResolver resolver = new StaticDomainNameResolver(eventLoop);
    private final TimerFactory timerFactory = new DummyTimerFactory();
    private final TLSContextFactory tlsContextFactory = null;
    private String sid;
    
    @After
    public void tearDown() {
        eventLoop.processEvents();
    }
    
    @Test
    public void testHeader() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        testling.startStream("wonderland.lit",1);
        String initial = "<body wait='60' "
                            +"inactivity='30' "
                            +"polling='5' "
                            +"requests='2' "
                            +"hold='1' "
                            +"maxpause='120' "
                            +"sid='MyShinySID' "
                            +"ver='1.6' "
                            +"from='wonderland.lit' "
                            +"xmlns='http://jabber.org/protocol/httpbind'/>";
        readResponse(initial, connectionFactory.connections.get(0));
        assertEquals("MyShinySID",sid);
        assertTrue(testling.isReadyToSend());
    }
    
    @Test
    public void testReadiness_ok() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        testling.setSID("blahhhh");
        assertTrue(testling.isReadyToSend());
    }
    
    @Test
    public void testReadiness_pending() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        testling.setSID("mySID");
        assertTrue(testling.isReadyToSend());
        testling.write(new SafeByteArray("<mypayload/>"));
        assertFalse(testling.isReadyToSend());
        readResponse("<body><blah/></body>", connectionFactory.connections.get(0));
        assertTrue(testling.isReadyToSend());
    }
    
    @Test
    public void testReadiness_disconnect() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        testling.setSID("mySID");
        assertTrue(testling.isReadyToSend());
        connectionFactory.connections.get(0).onDisconnected.emit(null);
        assertFalse(testling.isReadyToSend());
    }
    
    @Test
    public void testReadiness_noSID() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        assertFalse(testling.isReadyToSend());
    }
    
    @Test
    public void testWrite_Receive() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        testling.setSID("mySID");
        testling.write(new SafeByteArray("<mypayload/>"));
        readResponse("<body><blah/></body>", connectionFactory.connections.get(0));
        assertEquals("<blah/>",dataRead.toString());
    }
    
    @Test
    public void testWrite_ReceiveTwice() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        testling.setSID("mySID");
        testling.write(new SafeByteArray("<mypayload/>"));
        readResponse("<body><blah/></body>", connectionFactory.connections.get(0));
        assertEquals("<blah/>",dataRead.toString());
        dataRead.clear();
        testling.write(new SafeByteArray("<mypayload2/>"));
        readResponse("<body><bleh/></body>", connectionFactory.connections.get(0));
        assertEquals("<bleh/>",dataRead.toString());
    }
    
    @Test
    public void testRead_Fragment() {
        BOSHConnection testling = createTestling();
        testling.connect();
        eventLoop.processEvents();
        assertEquals(1, connectionFactory.connections.size());
        MockConnection connection = connectionFactory.connections.get(0);
        SafeByteArray data1 = new SafeByteArray(
            "HTTP/1.1 200 OK\r\n"+
            "Content-Type: text/xml; charset=utf-8\r\n"+
            "Access-Control-Allow-Origin: *\r\n"+
            "Access-Control-Allow-Headers: Content-Type\r\n"+
            "Content-Length: 64\r\n");
        SafeByteArray data2 = new SafeByteArray(
            "\r\n<body xmlns='http://jabber.org/protocol/httpbind'>"+
            "<bl");
        SafeByteArray data3 = new SafeByteArray(
            "ah/>"+
            "</body>");
        connection.onDataRead.emit(data1);
        connection.onDataRead.emit(data2);
        assertTrue(dataRead.isEmpty());
        connection.onDataRead.emit(data3);
        assertEquals("<blah/>",dataRead.toString());
    }
    
    @Test
    public void testHTTPRequest() {
        String data = "<blah/>";
        String sid = "wigglebloom";
        String fullBody = "<body xmlns='http://jabber.org/protocol/httpbind' sid='" + sid + "' rid='20'>" + data + "</body>";
        Pair<SafeByteArray, Integer> http = 
                BOSHConnection.createHTTPRequest(new SafeByteArray(data), false, false, 
                        20, sid, new URL());
        assertEquals(fullBody.length(),http.second.intValue());
    }
    
    @Test
    public void testHTTPRequest_Empty() {
        String data = "";
        String sid = "wigglebloomsickle";
        String fullBody = "<body rid='42' sid='" + sid + "' xmlns='http://jabber.org/protocol/httpbind'>" + data + "</body>";
        Pair<SafeByteArray, Integer> http = 
                BOSHConnection.createHTTPRequest(new SafeByteArray(data), false, false, 
                        42, sid, new URL());
        assertEquals(fullBody.length(),http.second.intValue());
        String response = http.first.toString();
        int bodyPosition = response.indexOf("\r\n\r\n");
        assertFalse("bodyPosition is equal to -1",-1 == bodyPosition);
        assertEquals(fullBody,response.substring(bodyPosition+4));
    }

    private BOSHConnection createTestling() {
        resolver.addAddress("wonderland.lit", new HostAddress("127.0.0.1"));
        Connector connector = Connector.create("wonderland.lit", 5280, null, resolver, connectionFactory, timerFactory);
        BOSHConnection connection = BOSHConnection.create(new URL(), connector, parserFactory, 
                tlsContextFactory, new TLSOptions());
        connection.onConnectionFinished.connect(new Slot1<Boolean>() {
            
            @Override
            public void call(Boolean hadError) {
                handleConnectFinished(hadError.booleanValue());
            }
            
        });
        connection.onDisconnected.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean hadError) {
                handleDisconnected(hadError.booleanValue());
            }
            
        });
        connection.onXMPPDataRead.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray p1) {
                handleDataRead(p1);
            }
            
        });
        connection.onSessionStarted.connect(new Slot2<String, Integer>() {
            
            @Override
            public void call(String sid, Integer requests) {
                handleSID(sid);
            }
            
        });
        connection.setRID(42);
        return connection;
    }

    private void handleConnectFinished(boolean hadError) {
        connectFinished = true;
        connectFinishedWithError = hadError;
    }

    private void handleDisconnected(boolean hadError) {
        disconnected = true;
        disconnectedError = hadError;
    }

    private void handleDataRead(SafeByteArray data) {
        dataRead.append(data);
    }

    private void handleSID(String s) {
        sid = s;
    }
    
    private void readResponse(String response,MockConnection connection) {
        SafeByteArray data1 = new SafeByteArray(
                "HTTP/1.1 200 OK\r\n"+
              "Content-Type: text/xml; charset=utf-8\r\n"+
              "Access-Control-Allow-Origin: *\r\n"+
              "Access-Control-Allow-Headers: Content-Type\r\n"+
              "Content-Length: "
                );
        connection.onDataRead.emit(data1);
        SafeByteArray data2 = new SafeByteArray(Integer.toString(response.length()));
        connection.onDataRead.emit(data2);
        SafeByteArray data3 = new SafeByteArray("\r\n\r\n");
        connection.onDataRead.emit(data3);
        SafeByteArray data4 = new SafeByteArray(response);
        connection.onDataRead.emit(data4);
    }
    
    private static class MockConnection extends Connection {
        
        public MockConnection(Collection<HostAddressPort> failingPorts,
                EventLoop eventLoop) {
            this.failingPorts = new ArrayList<HostAddressPort>(failingPorts);
            this.eventLoop = eventLoop;
        }
        
        @Override
        public void listen() {
            fail();
        }
        
        @Override
        public void connect(HostAddressPort address) {
            hostAddressPort = address;
            final boolean fail = failingPorts.contains(address);
            eventLoop.postEvent(new Callback() {
                
                @Override
                public void run() {
                    onConnectFinished.emit(fail);
                }
                
            });
        }
        
        @Override
        public void disconnect() {
            disconnected = true;
            onDisconnected.emit(null);
        }
        
        @Override
        public void write(SafeByteArray data) {
            dataWritten.append(data);
        }

        /* (non-Javadoc)
         * @see com.isode.stroke.network.Connection#getLocalAddress()
         */
        @Override
        public HostAddressPort getLocalAddress() {
            return new HostAddressPort();
        }
        
        public HostAddressPort getRemoteAddress() {
            return new HostAddressPort();
        }
        
        private final EventLoop eventLoop;
        private HostAddressPort hostAddressPort;
        private final List<HostAddressPort> failingPorts;
        private final ByteArray dataWritten = new ByteArray();
        private boolean disconnected;

    }
    
    private static class MockConnectionFactory implements ConnectionFactory {
        
        public MockConnectionFactory(EventLoop eventLoop) {
            this.eventLoop = eventLoop;
        }
        
        @Override
        public Connection createConnection() {
            MockConnection connection = new MockConnection(failingPorts, eventLoop);
            connections.add(connection);
            return connection;
        }
        
        private final EventLoop eventLoop;
        private List<MockConnection> connections = new ArrayList<MockConnection>();
        private List<HostAddressPort> failingPorts = new ArrayList<HostAddressPort>();
    }
 
}
