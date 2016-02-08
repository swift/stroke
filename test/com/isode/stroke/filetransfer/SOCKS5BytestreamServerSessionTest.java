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
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.StartStopper;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.network.DummyConnection;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;

/**
 * Tests for {@link SOCKS5BytestreamServerSession}
 */
public class SOCKS5BytestreamServerSessionTest {

    private final DummyEventLoop eventLoop = new DummyEventLoop();
    private final SOCKS5BytestreamRegistry bytestreams = new SOCKS5BytestreamRegistry();
    private final DummyConnection connection = new DummyConnection(eventLoop);
    private final ByteArray receivedData = new ByteArray();
    private int receivedDataChunks = 0;
    private final ByteArrayReadBytestream stream1 = 
            new ByteArrayReadBytestream(new ByteArray("abcdefg"));
    private boolean finished = false;
    private FileTransferError error = null;
    private SignalConnection onDataSentConnection;
    
    
    @Before
    public void setUp() {
        onDataSentConnection = connection.onDataSent.connect(new Slot1<SafeByteArray>() {
            
            @Override
            public void call(SafeByteArray data) {
                handleDataWritten(data);
            }
            
        });
    }
    
    @After
    public void tearDown() {
        onDataSentConnection.disconnect();
    }
    
    @Test
    public void testAuthenticate() {
        SOCKS5BytestreamServerSession testling = createSession();
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        receive(new SafeByteArray(new byte[] {0x05,0x02,0x01,0x02}));
        SafeByteArray expected = new SafeByteArray(new byte[] {0x05,0x00});
        assertEquals(expected,receivedData);
    }
    
    
    @Test
    public void testAuthenticate_Chunked() {
        SOCKS5BytestreamServerSession testling = createSession();
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        receive(new SafeByteArray(new byte[] {0x05,0x02,0x01}));
        assertEquals(0,receivedData.getSize());
        receive(new SafeByteArray(new byte[] {0x02}));
        SafeByteArray expected = new SafeByteArray(new byte[] {0x05,0x00});
        assertEquals(expected,receivedData);
    }
    
    @Test
    public void testRequest() {
        SOCKS5BytestreamServerSession testling = createSession();
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        bytestreams.setHasBytestream("abcdef", true);
        authenticate();
        
        ByteArray hostname = new ByteArray("abcdef");
        SafeByteArray data = new SafeByteArray();
        data.append(new byte[] {0x05,0x01,0x00,0x03});
        data.append((byte)hostname.getSize());
        data.append(hostname);
        data.append(new byte[] {0x00,0x00});
        receive(data);
        // Compare first 13 bytes of received data with what we expect
        ByteArray expectedData = 
                new ByteArray(new byte[] {0x05,0x00,0x00,0x03,0x06,0x61,0x62,
                        0x63,0x64,0x65,0x66,0x00,0x00});
        assertEquals(expectedData,receivedData);
        
    }
    
    @Test
    public void testRequest_UnknownBytestream() {
        SOCKS5BytestreamServerSession testling = createSession();
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        authenticate();
        ByteArray hostname = new ByteArray("abcdef");
        SafeByteArray data = new SafeByteArray();
        data.append(new byte[]{0x05,0x01,0x00,0x03});
        data.append((byte)hostname.getSize());
        data.append(hostname);
        data.append(new byte[] {0x00,0x00});
        receive(data);

        ByteArray expected = 
                new ByteArray(new byte[] {0x05,0x04,0x00,0x03,0x06,0x61,0x62,
                        0x63,0x64,0x65,0x66,0x00,0x00});
        
        assertEquals(expected,receivedData);
    }
    
    @Test
    public void testReceiveData() {
        SOCKS5BytestreamServerSession testling = createSession();
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        bytestreams.setHasBytestream("abcdef", true);
        authenticate();
        request("abcdef");
        eventLoop.processEvents();
        testling.startSending(stream1);
        skipHeader("abcdef");
        eventLoop.processEvents();
        assertEquals(new ByteArray("abcdefg"),receivedData);
        assertEquals(2,receivedDataChunks);
    }
    
    @Test
    public void testReceiveData_Chunked() {
        SOCKS5BytestreamServerSession testling = createSession();
        testling.setChunkSize(3);
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        bytestreams.setHasBytestream("abcdef", true);
        authenticate();
        request("abcdef");
        eventLoop.processEvents();
        testling.startSending(stream1);
        eventLoop.processEvents();
        skipHeader("abcdef");
        assertEquals(new ByteArray("abcdefg"),receivedData);
        assertEquals(4,receivedDataChunks);
    }
    
    @Test
    public void testDataStreamPauseStopsSendingData() {
        SOCKS5BytestreamServerSession testling = createSession();
        testling.setChunkSize(3);
        stream1.setDataComplete(false);
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        bytestreams.setHasBytestream("abcdef", true);
        authenticate();
        request("abcdef");
        eventLoop.processEvents();
        testling.startSending(stream1);
        eventLoop.processEvents();
        skipHeader("abcdef");
        assertEquals(new ByteArray("abcdefg"),receivedData);
        assertEquals(4,receivedDataChunks);
        assertFalse(finished);
        assertNull(error);
    }
    
    @Test
    public void testDataStreamResumeAfterPauseSendsData() {
        SOCKS5BytestreamServerSession testling = createSession();
        testling.setChunkSize(3);
        stream1.setDataComplete(false);
        StartStopper<SOCKS5BytestreamServerSession> stopper = 
                new StartStopper<SOCKS5BytestreamServerSession>(testling);
        bytestreams.setHasBytestream("abcdef", true);
        authenticate();
        request("abcdef");
        eventLoop.processEvents();
        testling.startSending(stream1);
        eventLoop.processEvents();
        skipHeader("abcdef");
        stream1.addData(new ByteArray("xyz"));
        eventLoop.processEvents();
        assertEquals(new ByteArray("abcdefgxyz"),receivedData);
        assertFalse(finished);
        assertNull(error);
    }
    
    private void receive(SafeByteArray data) {
        connection.receive(data);
        eventLoop.processEvents();
    }
    
    private void authenticate() {
        receive(new SafeByteArray(new byte[] {0x05,0x02,0x01,0x02}));
        receivedData.clear();
        receivedDataChunks = 0;
    }
    
    private void request(String hostname) {
        SafeByteArray results = new SafeByteArray();
        results.append(new byte[] {0x05,0x01,0x00,0x03});
        results.append((byte) hostname.length());
        results.append(hostname);
        results.append(new byte[] {0x00,0x00});
        receive(results);
    }
    
    private void skipHeader(String hostname) {
        int headerSize = 7 + hostname.length();
        byte[] currentReceivedData = receivedData.getData();
        byte[] newContents = Arrays.copyOfRange(currentReceivedData, headerSize, currentReceivedData.length);
        receivedData.clear();
        receivedData.append(newContents);
    }
    
    private void handleDataWritten(SafeByteArray data) {
        receivedData.append(data);
        receivedDataChunks++;
    }

    private SOCKS5BytestreamServerSession createSession() {
        SOCKS5BytestreamServerSession session = new SOCKS5BytestreamServerSession(connection, bytestreams);
        session.onFinished.connect(new Slot1<FileTransferError>() {
    
            @Override
            public void call(FileTransferError error) {
                handleFinished(error);
            }
            
        });
        return session;
    }

    private void handleFinished(FileTransferError error) {
        finished = true;
        this.error = error;
    }
    
}
