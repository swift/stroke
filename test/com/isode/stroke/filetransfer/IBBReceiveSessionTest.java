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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.elements.IBB;
import com.isode.stroke.elements.IBB.Action;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.IQ.Type;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot1;

/**
 * Tests for {@link IBBReceiveSession}
 */
public class IBBReceiveSessionTest {
    
    private final DummyStanzaChannel stanzaChannel = new DummyStanzaChannel();
    private final IQRouter iqRouter = new IQRouter(stanzaChannel);
    private boolean finished = false;
    private final ByteArrayWriteBytestream bytestream = new ByteArrayWriteBytestream();
    private FileTransferError error = null;
    
    @Test
    public void testOpen() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession");
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"), "id-open"));
        assertTrue(stanzaChannel.isResultAtIndex(0, "id-open"));
        assertFalse(finished);
        testling.stop();
    }
    
    @Test
    public void testReceiveData() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession");
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"),
                "id-open"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 0, new ByteArray("abc")),
                new JID("foo@bar.com/baz"),
                "id-a"));
        
        assertTrue(stanzaChannel.isResultAtIndex(1, "id-a"));
        assertEquals(new ByteArray("abc"),bytestream.getData());
        assertFalse(finished);
        
        testling.stop();
    }
    
    @Test
    public void testReceiveMultipleData() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession");
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"),
                "id-open"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 0, new ByteArray("abc")),
                new JID("foo@bar.com/baz"),
                "id-a"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 1, new ByteArray("def")),
                new JID("foo@bar.com/baz"),
                "id-b"));
        
        assertTrue(stanzaChannel.isResultAtIndex(2, "id-b"));
        assertEquals(new ByteArray("abcdef"),bytestream.getData());
        assertFalse(finished);
        
        testling.stop();
    }
    
    @Test
    public void testReceiveDataForOtherSession() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession");
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"),
                "id-open"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("othersession", 0, new ByteArray("abc")),
                new JID("foo@bar.com/baz"),
                "id-a"));
        
        assertTrue(stanzaChannel.isErrorAtIndex(1, "id-a"));
        
        testling.stop();
    }
    
    @Test
    public void testReceiveDataOutOfOrder() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession");
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"),
                "id-open"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 0, new ByteArray("abc")),
                new JID("foo@bar.com/baz"),
                "id-a"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 0, new ByteArray("def")),
                new JID("foo@bar.com/baz"),
                "id-b"));
        
        assertTrue(stanzaChannel.isErrorAtIndex(2, "id-b"));
        assertTrue(finished);
        assertNotNull(error);
        
        testling.stop();
    }
    
    @Test
    public void testReceiveLastData() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession", 6);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"),
                "id-open"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 0, new ByteArray("abc")),
                new JID("foo@bar.com/baz"),
                "id-a"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 1, new ByteArray("def")),
                new JID("foo@bar.com/baz"),
                "id-b"));
        
        assertTrue(stanzaChannel.isResultAtIndex(2, "id-b"));
        assertEquals(new ByteArray("abcdef"),bytestream.getData());
        assertTrue(finished);
        assertNull(error);
        
        testling.stop();
    }
    
    @Test
    public void testReceiveClose() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession", 6);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"),
                "id-open"));
        
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBClose("mysession"),
                new JID("foo@bar.com/baz"),
                "id-close"));
        
        assertTrue(finished);
        assertNotNull(error);
        
        testling.stop();
    }
    
    @Test
    public void testStopWhileActive() {
        IBBReceiveSession testling = createSession("foo@bar.com/baz", "mysession", 6);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("mysession", 0x10),
                new JID("foo@bar.com/baz"),
                "id-open"));
        
        testling.stop();
        
        assertTrue(stanzaChannel.isRequestAtIndex(1, new JID("foo@bar.com/baz"),
                Type.Set, new IBB()));
        IBB ibb = stanzaChannel.sentStanzas.get(1).getPayload(new IBB());
        assertEquals(Action.Close,ibb.getAction());
        assertTrue(finished);
        assertNull(error);
    }
    
    private IQ createIBBRequest(IBB ibb,JID from,String id) {
        IQ request = IQ.createRequest(Type.Set, new JID("baz@fum.com/dum"), id, ibb);
        request.setFrom(from);
        return request;
    }
    
    private IBBReceiveSession createSession(String from,String id) {
        return createSession(from, id, 0x1000);
    }
    
    private IBBReceiveSession createSession(String from,String id,int size) {
        IBBReceiveSession session = 
                new IBBReceiveSession(id, new JID(from), new JID(), size, bytestream, iqRouter);
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
