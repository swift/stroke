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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.network.Connection.Error;
import com.isode.stroke.network.HTTPConnectProxiedConnection.Pair;
import com.isode.stroke.signals.Slot1;

/**
 * Tests for {@link HTTPConnectProxiedConnection}
 */
public class HTTPConnectProxiedConnectionTest {
    
    private final String proxyHost = "doo.bah";
    private final int proxyPort = 1234;
    private final HostAddressPort proxyHostAddress = new HostAddressPort(new HostAddress("1.1.1.1"), proxyPort);
    private final HostAddressPort host = new HostAddressPort(new HostAddress("2.2.2.2"), 2345);
    private final DummyEventLoop eventLoop = new DummyEventLoop();
    private final StaticDomainNameResolver resolver = new StaticDomainNameResolver(eventLoop);
    private final MockConnectionFactory connectionFactory = new MockConnectionFactory(eventLoop);
    private final TimerFactory timerFactory = new DummyTimerFactory();
    private boolean connectFinished = false;
    private boolean  connectFinishedWithError = false;
    private boolean  disconnected = false;
    private Connection.Error disconnectedError = null;
    private final ByteArray dataRead = new ByteArray();
    
    private static Logger logger = 
            Logger.getLogger(HTTPConnectProxiedConnectionTest.class.getName());
    
    @Before
    public void setUp() {
        resolver.addAddress(proxyHost, proxyHostAddress.getAddress());
    }
    
    @Test
    public void testConnect_CreatesConnectionToProxy() {
        HTTPConnectProxiedConnection testling = createTestling();
        
        connect(testling, host);

        assertEquals(1,connectionFactory.connections.size());
        assertNotNull(connectionFactory.connections.get(0).hostAddressPort);
        assertEquals(proxyHostAddress,connectionFactory.connections.get(0).hostAddressPort);
        assertFalse(connectFinished);
    }
    
    @Test
    public void testConnect_SendsConnectRequest() {
        HTTPConnectProxiedConnection testling = createTestling();
        
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));

        assertEquals(new ByteArray("CONNECT 2.2.2.2:2345 HTTP/1.1\r\n\r\n"), 
                connectionFactory.connections.get(0).dataWritten);
    }
    
    @Test
    public void testConnect_ReceiveConnectResponse() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));
        
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("HTTP/1.0 200 Connection established\r\n\r\n"));
        eventLoop.processEvents();

        assertTrue(connectFinished);
        assertFalse(connectFinishedWithError);
        assertTrue(dataRead.isEmpty());
    }
    
    @Test
    public void testConnect_ReceiveConnectChunkedResponse() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));

        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("HTTP/1.0 "));
        eventLoop.processEvents();
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("200 Connection established\r\n\r\n"));
        eventLoop.processEvents();

        assertTrue(connectFinished);
        assertFalse(connectFinishedWithError);
        assertTrue(dataRead.isEmpty());
    }
    
    @Test
    public void testConnect_ReceiveMalformedConnectResponse() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));

        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("FLOOP"));
        eventLoop.processEvents();

        assertTrue(connectFinished);
        assertTrue(connectFinishedWithError);
        assertTrue(connectionFactory.connections.get(0).disconnected);
    }
    
    @Test
    public void testConnect_ReceiveErrorConnectResponse() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));

        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("HTTP/1.0 401 Unauthorized\r\n\r\n"));
        eventLoop.processEvents();

        assertTrue(connectFinished);
        assertTrue(connectFinishedWithError);
        assertTrue(connectionFactory.connections.get(0).disconnected);
    }
    
    @Test
    public void testConnect_ReceiveDataAfterConnect() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("HTTP/1.0 200 Connection established\r\n\r\n"));
        eventLoop.processEvents();
        
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("abcdef"));
        
        assertEquals(new ByteArray("abcdef"),dataRead);
    }
    
    @Test
    public void testWrite_AfterConnect() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("HTTP/1.0 200 Connection established\r\n\r\n"));
        eventLoop.processEvents();
        connectionFactory.connections.get(0).dataWritten.clear();

        testling.write(new SafeByteArray("abcdef"));
        
        assertEquals(new ByteArray("abcdef"),connectionFactory.connections.get(0).dataWritten);
    }
    
    @Test
    public void testDisconnect_AfterConnectRequest() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));

        testling.disconnect();

        assertTrue(connectionFactory.connections.get(0).disconnected);
        assertTrue(disconnected);
        assertNull(disconnectedError);
    }
    
    @Test
    public void testDisconnect_AfterConnect() {
        HTTPConnectProxiedConnection testling = createTestling();
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("HTTP/1.0 200 Connection established\r\n\r\n"));
        eventLoop.processEvents();

        testling.disconnect();

        assertTrue(connectionFactory.connections.get(0).disconnected);
        assertTrue(disconnected);
        assertNull(disconnectedError);
    }
    
    @Test
    public void testTrafficFilter() {
        HTTPConnectProxiedConnection testling = createTestling();
        
        ExampleHTTPTrafficFilter httpTrafficFilter = new ExampleHTTPTrafficFilter();

        testling.setHTTPTrafficFilter(httpTrafficFilter);
        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));

        // set a default response so the server response is answered by the traffic filter
        httpTrafficFilter.filterResponseReturn.clear();
        httpTrafficFilter.filterResponseReturn.add(new Pair("Authorization", "Negotiate a87421000492aa874209af8bc028"));

        connectionFactory.connections.get(0).dataWritten.clear();

        // test chunked response
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("HTTP/1.0 401 Unauthorized\r\n"));
        eventLoop.processEvents();
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray("WWW-Authenticate: Negotiate\r\n\r\n"));
        eventLoop.processEvents();


        // verify that the traffic filter got called and answered with its response
        assertEquals(1,httpTrafficFilter.filterResponses.size());
        assertEquals("WWW-Authenticate",httpTrafficFilter.filterResponses.get(0).get(0).a);

        // remove the default response from the traffic filter
        httpTrafficFilter.filterResponseReturn.clear();
        eventLoop.processEvents();

        // verify that the traffic filter answer is send over the wire
        assertEquals(new ByteArray("CONNECT 2.2.2.2:2345 HTTP/1.1\r\nAuthorization: Negotiate a87421000492aa874209af8bc028\r\n\r\n"), connectionFactory.connections.get(1).dataWritten);

        // verify that after without the default response, the traffic filter is skipped, authentication proceeds and traffic goes right through
        connectionFactory.connections.get(1).dataWritten.clear();
        testling.write(new SafeByteArray("abcdef"));
        assertEquals(new ByteArray("abcdef"), connectionFactory.connections.get(1).dataWritten);
    }
    
    @Test
    public void testTrafficFilterNoConnectionReuse() {
        HTTPConnectProxiedConnection testling = createTestling();
        
        ProxyAuthenticationHTTPTrafficFilter httpTrafficFilter = new ProxyAuthenticationHTTPTrafficFilter();
        testling.setHTTPTrafficFilter(httpTrafficFilter);
        

        connect(testling, new HostAddressPort(new HostAddress("2.2.2.2"), 2345));

        // First HTTP CONNECT request assumes the proxy will work.
        assertEquals(new ByteArray("CONNECT 2.2.2.2:2345 HTTP/1.1\r\n\r\n"), 
                connectionFactory.connections.get(0).dataWritten);

         // First reply presents initiator with authentication options.
        connectionFactory.connections.get(0).onDataRead.emit(new SafeByteArray(
            "HTTP/1.0 407 ProxyAuthentication Required\r\n"
            +"proxy-Authenticate: Negotiate\r\n"
            +"Proxy-Authenticate: Kerberos\r\n"
            +"proxy-Authenticate: NTLM\r\n"
            +"\r\n"));
        eventLoop.processEvents();
        assertFalse(connectFinished);
        assertFalse(connectFinishedWithError);

        // The HTTP proxy responds with code 407, so the traffic filter should inject the authentication response on a new connection.
        assertEquals(new ByteArray("CONNECT 2.2.2.2:2345 HTTP/1.1\r\nProxy-Authorization: "
                + "NTLM TlRMTVNTUAABAAAAt7II4gkACQAxAAAACQAJACgAAAVNTUAADAAFASgKAAAAD0"
                + "xBQlNNT0tFM1dPUktHUk9VUA==\r\n\r\n"), 
                connectionFactory.connections.get(1).dataWritten);

        // The proxy responds with another authentication step.
        connectionFactory.connections.get(1).onDataRead.emit(new SafeByteArray(
            "HTTP/1.0 407 ProxyAuthentication Required\r\n"
            +"Proxy-Authenticate: NTLM TlRMTVNTUAACAAAAEAAQADgAAAA1goriluCDYHcYI/sAAAAAAAAAA"
            + "FQAVABIAAAABQLODgAAAA9TAFAASQBSAEkAVAAxAEIAAgAQAFMAUABJAFIASQBUADEAQgABABAAUw"
            + "BQAEkAUgBJAFQAMQBCAAQAEABzAHAAaQByAGkAdAAxAGIAAwAQAHMAcABpAHIAaQB0ADEAYgAAAAAA"
            + "\r\n\r\n"));
        eventLoop.processEvents();
        assertFalse(connectFinished);
        assertFalse(connectFinishedWithError);

        // Last HTTP request that should succeed. Further traffic will go over the connection of this request.
        assertEquals(new ByteArray("CONNECT 2.2.2.2:2345 HTTP/1.1\r\nProxy-Authorization: "
                + "NTLM TlRMTVNTUAADAAAAGAAYAHIAAAAYABgAigAAABIAEgBIAAAABgAGAFoAAAASABIVNT"
                + "UAADAAYAAAABAAEACiAAAANYKI4gUBKAoAAAAPTABBAEIAUwBNAE8ASwBFADMAXwBxAGEAT"
                + "ABBAEIAUwBNAE8ASwBFADMA0NKq8HYYhj8AAAAAAAAAAAAAAAAAAAAAOIiih3mR+AkyM4r99"
                + "sy1mdFonCu2ILODro1WTTrJ4b4JcXEzUBA2Ig==\r\n\r\n"),
                connectionFactory.connections.get(2).dataWritten);

        connectionFactory.connections.get(2).onDataRead.emit(new SafeByteArray("HTTP/1.0 200 OK"
                + "\r\n\r\n"));
        eventLoop.processEvents();

        // The HTTP CONNECT proxy initialization finished without error.
        assertTrue(connectFinished);
        assertFalse(connectFinishedWithError);

        // Further traffic is written directly, without interception of the filter.
        connectionFactory.connections.get(2).dataWritten.clear();
        testling.write(new SafeByteArray("This is some basic data traffic."));
        assertEquals(new ByteArray("This is some basic data traffic."),
                connectionFactory.connections.get(2).dataWritten);
    }
    
    private void connect(HTTPConnectProxiedConnection connection, HostAddressPort to) {
        connection.connect(to);
        eventLoop.processEvents();
        eventLoop.processEvents();
        eventLoop.processEvents();
    }
    
    private HTTPConnectProxiedConnection createTestling() {
        HTTPConnectProxiedConnection connection = HTTPConnectProxiedConnection.create(resolver, 
                connectionFactory, timerFactory, proxyHost, proxyPort, 
                new SafeByteArray(""), new SafeByteArray(""));
        connection.onConnectFinished.connect(new Slot1<Boolean>() {
            
            @Override
            public void call(Boolean hadError) {
                handleConnectFinished(hadError.booleanValue());
            }
            
        });
        connection.onDisconnected.connect(new Slot1<Connection.Error>() {

            @Override
            public void call(Error error) {
                handleDisconnected(error);
            }
            
        });
        connection.onDataRead.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handleDataRead(data);
            }
            
        });
        return connection;
    }

    private void handleConnectFinished(boolean hadError) {
        connectFinished = true;
        connectFinishedWithError = hadError;
    }

    private void handleDisconnected(Connection.Error error) {
        disconnected = true;
        disconnectedError = error;
    }

    private void handleDataRead(SafeByteArray data) {
        dataRead.append(data);
    }
    
    private static class ExampleHTTPTrafficFilter implements HTTPTrafficFilter {

        @Override
        public Vector<Pair> filterHTTPResponseHeader(String statusLine, Vector<Pair> response) {
            filterResponses.add(response);
            logger.fine("\n");
            return filterResponseReturn;
        }
        
        private Vector<Vector<Pair>> filterResponses = new Vector<Vector<Pair>>();
        
        private Vector<Pair> filterResponseReturn = new Vector<Pair>();
        
    }
    
    public static class ProxyAuthenticationHTTPTrafficFilter implements HTTPTrafficFilter {

        @Override
        public Vector<Pair> filterHTTPResponseHeader(String statusLine, Vector<Pair> response) {
            Vector<Pair> filterResponseReturn = new Vector<Pair>();
            String[] rawStatusLineFields = statusLine.split("\\s+");
            Vector<String> statusLineFields = new Vector(Arrays.asList(rawStatusLineFields));

            int statusCode = Integer.valueOf(statusLineFields.get(1));
            if (statusCode == 407) {
                for (Pair field : response) {
                  if ("Proxy-Authenticate".equalsIgnoreCase(field.a)) {
                      if (field.b.length() >= 6 && field.b.startsWith(" NTLM ")) {
                          filterResponseReturn.add(new Pair("Proxy-Authorization", 
                                  "NTLM TlRMTVNTUAADAAAAGAAYAHIAAAAYABgAigAAABIAEgBIAAAABgAGAFo"
                                  + "AAAASABIVNTUAADAAYAAAABAAEACiAAAANYKI4gUBKAoAAAAPTABBAEIAU"
                                  + "wBNAE8ASwBFADMAXwBxAGEATABBAEIAUwBNAE8ASwBFADMA0NKq8HYYhj"
                                  + "8AAAAAAAAAAAAAAAAAAAAAOIiih3mR+AkyM4r99sy1mdFonCu2ILODro1W"
                                  + "TTrJ4b4JcXEzUBA2Ig=="));
                          return filterResponseReturn;
                      }
                      else if (field.b.length() >= 5 && field.b.startsWith(" NTLM")) {
                          filterResponseReturn.add(new Pair("Proxy-Authorization",
                                  "NTLM TlRMTVNTUAABAAAAt7II4gkACQAxAAAACQAJACgAAAVNTUAADAAFASg"
                                  + "KAAAAD0xBQlNNT0tFM1dPUktHUk9VUA=="));
                        return filterResponseReturn;
                      }
                  }
                }

                return filterResponseReturn;
            }
            else {
                return new Vector<Pair>();
            }
        }
        
    }

    private static class MockConnection extends Connection {
        
          private final EventLoop eventLoop;
          private HostAddressPort hostAddressPort = null;
          private final List<HostAddressPort> failingPorts;
          private final ByteArray dataWritten = new ByteArray();
          private boolean disconnected = false;
          
          private MockConnection(Collection<? extends HostAddressPort> failingPorts,
                  EventLoop eventLoop) {
              this.eventLoop = eventLoop;
              this.failingPorts = new ArrayList<HostAddressPort>(failingPorts);
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
        private final List<MockConnection> connections = new ArrayList<MockConnection>();
        private final List<HostAddressPort> failingPorts = new ArrayList<HostAddressPort>();
    
        private MockConnectionFactory(EventLoop eventLoop) {
            this.eventLoop = eventLoop;
        }
        
        @Override
        public Connection createConnection() {
            MockConnection connection = new MockConnection(failingPorts, eventLoop);
            connections.add(connection);
            logger.fine("New connection created\n");
            return connection;
        }
        
    }

}
