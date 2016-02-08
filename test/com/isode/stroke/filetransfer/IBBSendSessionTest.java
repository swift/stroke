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
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot1;

/**
 * Tests for {@link IBBSendSession}
 */
public class IBBSendSessionTest {

    private final DummyStanzaChannel stanzaChannel = new DummyStanzaChannel();
    private final IQRouter iqRouter = new IQRouter(stanzaChannel);
    private boolean finished;
    private FileTransferError error;
    private final ByteArrayReadBytestream bytestream = new ByteArrayReadBytestream(new ByteArray("abcdefg"));
    
    @Test
    public void testStart() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        testling.setBlockSize(1234);
        testling.start();
        assertEquals(1,stanzaChannel.sentStanzas.size());
        assertTrue(stanzaChannel.isRequestAtIndex(0, new JID("foo@bar.com/baz"), Type.Set, new IBB()));
        IBB ibb = stanzaChannel.sentStanzas.get(0).getPayload(new IBB());
        assertEquals(Action.Open,ibb.getAction());
        assertEquals(1234,ibb.getBlockSize());
        assertEquals("myid",ibb.getStreamID());
    }
    
    @Test
    public void testStart_ResponseStartsSending() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        testling.setBlockSize(3);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        assertEquals(2, stanzaChannel.sentStanzas.size());
        assertTrue(stanzaChannel.isRequestAtIndex(1, new JID("foo@bar.com/baz"), Type.Set, new IBB()));
        IBB ibb = stanzaChannel.sentStanzas.get(1).getPayload(new IBB());
        assertEquals(Action.Data,ibb.getAction());
        assertEquals(new ByteArray("abc"),ibb.getData());
        assertEquals(0,ibb.getSequenceNumber());
        assertEquals("myid",ibb.getStreamID());
    }
    
    @Test
    public void testResponseContinuesSending() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        testling.setBlockSize(3);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        assertEquals(3,stanzaChannel.sentStanzas.size());
        assertTrue(stanzaChannel.isRequestAtIndex(2, new JID("foo@bar.com/baz"), 
                Type.Set, new IBB()));
        IBB ibb = stanzaChannel.sentStanzas.get(2).getPayload(new IBB());
        assertEquals(Action.Data,ibb.getAction());
        assertEquals(new ByteArray("def"),ibb.getData());
        assertEquals(1,ibb.getSequenceNumber());
        assertEquals("myid",ibb.getStreamID());
    }
    
    @Test
    public void testResponsdToAllFinishes() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        testling.setBlockSize(3);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        assertTrue(finished);
        assertNull(error);
    }
    
    @Test
    public void testErrorResponseFinishesWithError() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        testling.setBlockSize(3);
        testling.start();
        Stanza sentStanza = stanzaChannel.sentStanzas.get(0);
        IQ errorIQ = 
                IQ.createError(new JID("baz@fum.com/foo"), sentStanza.getTo(), sentStanza.getID());
        stanzaChannel.onIQReceived.emit(errorIQ);
        assertTrue(finished);
        assertNotNull(error);
    }
    
    @Test
    public void testStopDuringSessionCloses() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        testling.setBlockSize(3);
        testling.start();
        testling.stop();
        
        assertEquals(2,stanzaChannel.sentStanzas.size());
        assertTrue(stanzaChannel.isRequestAtIndex(1, new JID("foo@bar.com/baz"), 
                Type.Set, new IBB()));
        IBB ibb = stanzaChannel.sentStanzas.get(1).getPayload(new IBB());
        assertEquals(Action.Close,ibb.getAction());
        assertEquals("myid",ibb.getStreamID());
        assertTrue(finished);
        assertNull(error);
    }
    
    @Test
    public void testStopAfterFinishedDoesNotClose() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        testling.setBlockSize(16);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        assertTrue(finished);
        testling.stop();
        assertEquals(2,stanzaChannel.sentStanzas.size());
    }
    
    @Test
    public void testDataStreamPauseStopsSendingData() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        bytestream.setDataComplete(false);
        testling.setBlockSize(3);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        assertFalse(finished);
        assertNull(error);
    }
    
    @Test
    public void testDataStreamResumeAfterPauseSendsData() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        bytestream.setDataComplete(false);
        testling.setBlockSize(3);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        bytestream.addData(new ByteArray("xyz"));
        assertEquals(5,stanzaChannel.sentStanzas.size());
    }
    
    @Test
    public void testDataStreamResumeBeforePauseDoesNotSendData() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        bytestream.setDataComplete(false);
        testling.setBlockSize(3);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        bytestream.addData(new ByteArray("xyz"));
        assertEquals(2,stanzaChannel.sentStanzas.size());
    }
    
    @Test
    public void testDataStreamResumeAfterResumeDoesNotSendData() {
        IBBSendSession testling = createSession("foo@bar.com/baz");
        bytestream.setDataComplete(false);
        testling.setBlockSize(3);
        testling.start();
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        stanzaChannel.onIQReceived.emit(createIBResult());
        bytestream.addData(new ByteArray("xyz"));
        bytestream.addData(new ByteArray("xuv"));
        assertEquals(5,stanzaChannel.sentStanzas.size());
    }
    
    private IQ createIBResult() {
        Stanza lastStanza = stanzaChannel.sentStanzas.lastElement();
        return IQ.createResult(new JID("baz@fum.com/dum"), lastStanza.getTo(), 
                lastStanza.getID(), new IBB());
    }
    
    private IBBSendSession createSession(String to) {
        IBBSendSession session = 
                new IBBSendSession("myid", new JID(), new JID(to), bytestream, iqRouter);
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
