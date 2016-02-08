/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.filetransfer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.DummyTimerFactory;
import com.isode.stroke.network.HostAddress;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot1;

/**
 * Tests for {@link SOCKS5BytestreamClientSession}
 */
public class SOCKS5BytestreamClientSessionTest {

    private static final Random rng = new Random();
    
    private final HostAddressPort destinationAddressPort = new HostAddressPort(new HostAddress("127.0.0.1"), 8888);
    private final CryptoProvider crypto = new JavaCryptoProvider();
    private final String destination = "092a44d859d19c9eed676b551ee80025903351c2";
    private final DummyEventLoop eventLoop = new DummyEventLoop();
    private final DummyTimerFactory timerFactory = new DummyTimerFactory();
    private final List<HostAddressPort> failingPorts = new ArrayList<HostAddressPort>();
    private final MockeryConnection connection = 
            new MockeryConnection(failingPorts, true, eventLoop);
    
    @Before
    public void setUp() {
        rng.setSeed(System.currentTimeMillis());
    }
    
    @Test
    public void testForSessionReady() {
        final TestHelper helper = new TestHelper();
        connection.onDataSent.connect(new Slot1<SafeByteArray>() {
            
            @Override
            public void call(SafeByteArray data) {
                helper.handleConnectionDataWritten(data);
            }
            
        });
        
        SOCKS5BytestreamClientSession clientSession = new SOCKS5BytestreamClientSession(connection, destinationAddressPort, destination, timerFactory);
        clientSession.onSessionReady.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean hasError) {
                helper.handleSessionRead(hasError.booleanValue());
            }
            
        });
        
        clientSession.start();
        eventLoop.processEvents();
        assertEquals(new ByteArray(new byte[] {0x05,0x01,0x00}),helper.unprocessedInput);
        
        helper.unprocessedInput.clear();
        serverRespondHelloOK();
        eventLoop.processEvents();
        ByteArray expected = new ByteArray(new byte[] {0x05,0x01,0x00,0x03});
        expected.append((byte)destination.length());
        expected.append(destination);
        expected.append((byte)0x00);
        ByteArray results = getSubArray(helper.unprocessedInput, expected.getSize());
        assertEquals(expected,results);
        
        helper.unprocessedInput.clear();
        serverRespondRequestOK();
        eventLoop.processEvents();
        assertTrue(helper.sessionReadyCalled);
        assertFalse(helper.sessionReadyError);
    }
    
    @Test
    public void testErrorHandlingHello() {
        final TestHelper helper = new TestHelper();
        connection.onDataSent.connect(new Slot1<SafeByteArray>() {
            
            @Override
            public void call(SafeByteArray data) {
                helper.handleConnectionDataWritten(data);
            }
            
        });
        
        SOCKS5BytestreamClientSession clientSession = new SOCKS5BytestreamClientSession(connection, destinationAddressPort, destination, timerFactory);
        clientSession.onSessionReady.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean hasError) {
                helper.handleSessionRead(hasError.booleanValue());
            }
            
        });
        
        clientSession.start();
        eventLoop.processEvents();
        assertEquals(new ByteArray(new byte[] {0x05,0x01,0x00}),helper.unprocessedInput);
        
        helper.unprocessedInput.clear();
        serverRespondHelloAuthFail();
        eventLoop.processEvents();
        
        assertTrue(helper.sessionReadyCalled);
        assertTrue(helper.sessionReadyError);
        assertTrue(connection.disconnectCalled);
    }
    
    @Test
    public void testErrorHandlingRequest() {
        final TestHelper helper = new TestHelper();
        connection.onDataSent.connect(new Slot1<SafeByteArray>() {
            
            @Override
            public void call(SafeByteArray data) {
                helper.handleConnectionDataWritten(data);
            }
            
        });
        
        SOCKS5BytestreamClientSession clientSession = new SOCKS5BytestreamClientSession(connection, destinationAddressPort, destination, timerFactory);
        clientSession.onSessionReady.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean hasError) {
                helper.handleSessionRead(hasError.booleanValue());
            }
            
        });
        
        clientSession.start();
        eventLoop.processEvents();
        assertEquals(new ByteArray(new byte[] {0x05,0x01,0x00}),helper.unprocessedInput);
        
        helper.unprocessedInput.clear();
        serverRespondHelloOK();
        eventLoop.processEvents();
        ByteArray expected = new ByteArray(new byte[] {0x05,0x01,0x00,0x03});
        expected.append((byte)destination.length());
        expected.append(destination);
        expected.append((byte)0x00);
        ByteArray results = getSubArray(helper.unprocessedInput, expected.getSize());
        assertEquals(expected,results);
        
        helper.unprocessedInput.clear();
        serverRespondRequestFail();
        eventLoop.processEvents();
        assertTrue(helper.sessionReadyCalled);
        assertTrue(helper.sessionReadyError);
        assertTrue(connection.disconnectCalled);
    }
    
    @Test
    public void testWriteBytestream() {
        final TestHelper helper = new TestHelper();
        connection.onDataSent.connect(new Slot1<SafeByteArray>() {
            
            @Override
            public void call(SafeByteArray data) {
                helper.handleConnectionDataWritten(data);
            }
            
        });
        
        SOCKS5BytestreamClientSession clientSession = new SOCKS5BytestreamClientSession(connection, destinationAddressPort, destination, timerFactory);
        clientSession.onSessionReady.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean hasError) {
                helper.handleSessionRead(hasError.booleanValue());
            }
            
        });
        
        clientSession.start();
        eventLoop.processEvents();
        
        helper.unprocessedInput.clear();
        serverRespondHelloOK();
        eventLoop.processEvents();
        
        helper.unprocessedInput.clear();
        serverRespondRequestOK();
        eventLoop.processEvents();
        assertTrue(helper.sessionReadyCalled);
        assertFalse(helper.sessionReadyError);
        
        ByteArrayWriteBytestream output = new ByteArrayWriteBytestream();
        clientSession.startReceiving(output);
        
        ByteArray transferData = generateRandomByteArray(1024);
        connection.onDataRead.emit(new SafeByteArray(transferData));
        assertEquals(transferData,output.getData());
    }
    
    @Test
    public void testReadBytestream() {
        final TestHelper helper = new TestHelper();
        connection.onDataSent.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                helper.handleConnectionDataWritten(data);
            }
            
        });
        
        SOCKS5BytestreamClientSession clientSession = new SOCKS5BytestreamClientSession(connection, destinationAddressPort, destination, timerFactory);
        clientSession.onSessionReady.connect(new Slot1<Boolean>() {

            @Override
            public void call(Boolean hasError) {
                helper.handleSessionRead(hasError.booleanValue());
            }
            
        });
        
        clientSession.start();
        eventLoop.processEvents();
        
        helper.unprocessedInput.clear();
        serverRespondHelloOK();
        eventLoop.processEvents();
        
        helper.unprocessedInput.clear();
        serverRespondRequestOK();
        eventLoop.processEvents();
        assertTrue(helper.sessionReadyCalled);
        assertFalse(helper.sessionReadyError);
        
        helper.unprocessedInput.clear();
        ByteArray transferData = generateRandomByteArray(1024);
        ByteArrayReadBytestream input = new ByteArrayReadBytestream(transferData);
        clientSession.startSending(input);
        eventLoop.processEvents();
        
        assertEquals(transferData,helper.unprocessedInput);
    }
    
    private static ByteArray generateRandomByteArray(int len) {
        byte[] randomBytes = new byte[len];
        rng.nextBytes(randomBytes);
        return new ByteArray(randomBytes);
    }
    
    private void serverRespondHelloOK() {
        connection.onDataRead.emit(new SafeByteArray(new byte[] {0x05,0x00}));
    }
    
    private void serverRespondHelloAuthFail() {
        connection.onDataRead.emit(new SafeByteArray(new byte[] {0x05,(byte) 0xFF}));
    }
    
    private void serverRespondRequestOK() {
        SafeByteArray dataToSend = new SafeByteArray(new byte[] {0x05,0x00,0x00,0x03});
        dataToSend.append((byte)destination.length());
        dataToSend.append(destination);
        dataToSend.append((byte)0x00);
        connection.onDataRead.emit(dataToSend);
    }
    
    private void serverRespondRequestFail() {
        SafeByteArray correctData = new SafeByteArray(new byte[] {0x05,0x00,0x00,0x03});
        correctData.append((byte)destination.length());
        correctData.append(destination);
        correctData.append((byte)0x00);
        SafeByteArray dataToSend;
        do {
            ByteArray rndArray = generateRandomByteArray(correctData.getSize());
            dataToSend = new SafeByteArray(rndArray);
        } while (dataToSend.equals(correctData));
        connection.onDataRead.emit(dataToSend);
    }
    
    /**
     * Gets the sub {@link ByteArray} consisting of the first n bytes of
     * a given {@link ByteArray}
     * @param array A {@link ByteArray} should not be {@code null} and should
     * be at least n characters long.
     * @param n the number of bytes of the {@link ByteArray} to return as a new
     * {@link ByteArray}
     * @return The first n characters of the given {@link ByteArray} as a new
     * {@link ByteArray}.  Will not be {@code null}
     */
    private ByteArray getSubArray(ByteArray array,int n) {
        byte[] arrayData = array.getData();
        byte[] newArrayData = Arrays.copyOfRange(arrayData, 0, n);
        return new ByteArray(newArrayData);
    }

    private static final class TestHelper {
        
        private ByteArray unprocessedInput = new ByteArray();
        private boolean sessionReadyCalled = false;
        private boolean sessionReadyError = false;
        
        public TestHelper() {
            // Empty Constructor 
        }
        
        public void handleConnectionDataWritten(SafeByteArray data) {
            unprocessedInput.append(data);
        }
        
        public void handleSessionRead(boolean error) {
            sessionReadyCalled = true;
            sessionReadyError = error;
        }
        
    }
    
    
    private static final class MockeryConnection extends Connection implements EventOwner {

        private EventLoop eventLoop;
        private HostAddressPort hostAddressPort;
        private final List<HostAddressPort> failingPorts;
        private boolean isResponsive;
        private boolean disconnectCalled;
        
        private final Signal1<SafeByteArray> onDataSent = new Signal1<SafeByteArray>();

        public MockeryConnection(Collection<HostAddressPort> failingPorts,
                boolean isResponsive,EventLoop eventLoop) {
            this.eventLoop = eventLoop;
            this.failingPorts = new ArrayList<HostAddressPort>(failingPorts);
            this.isResponsive = isResponsive;
            this.disconnectCalled = false;
        }
        
        @Override
        public void listen() {
            fail();
        }

        /* (non-Javadoc)
         * @see com.isode.stroke.network.Connection#connect(com.isode.stroke.network.HostAddressPort)
         */
        @Override
        public void connect(HostAddressPort address) {
            hostAddressPort = address;
            if (isResponsive) {
                final boolean fail = failingPorts.contains(address);
                eventLoop.postEvent(new Callback() {
                    
                    @Override
                    public void run() {
                        onConnectFinished.emit(fail);
                    }
                    
                });
            }
        }

        /* (non-Javadoc)
         * @see com.isode.stroke.network.Connection#disconnect()
         */
        @Override
        public void disconnect() {
            disconnectCalled = true;
        }

        /* (non-Javadoc)
         * @see com.isode.stroke.network.Connection#write(com.isode.stroke.base.SafeByteArray)
         */
        @Override
        public void write(SafeByteArray data) {
            eventLoop.postEvent(new Callback() {
                
                @Override
                public void run() {
                    onDataWritten.emit();
                }
                
            });
            onDataSent.emit(data);
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
        
    }

}
