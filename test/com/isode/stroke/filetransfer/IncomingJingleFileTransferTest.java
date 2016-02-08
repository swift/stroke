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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.base.SimpleIDGenerator;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.elements.IBB;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.IQ.Type;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.jid.JID;
import com.isode.stroke.jingle.FakeJingleSession;
import com.isode.stroke.jingle.JingleContentID;
import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.network.DummyConnectionFactory;
import com.isode.stroke.network.DummyConnectionServerFactory;
import com.isode.stroke.network.DummyNetworkEnvironment;
import com.isode.stroke.network.DummyTimerFactory;
import com.isode.stroke.network.NATTraverser;
import com.isode.stroke.network.NetworkEnvironment;
import com.isode.stroke.network.NullNATTraverser;
import com.isode.stroke.network.StaticDomainNameResolver;
import com.isode.stroke.queries.IQRouter;

/**
 * Tests for {@link IncomingJingleFileTransfer}
 */
public class IncomingJingleFileTransferTest {

    private final CryptoProvider crypto = new JavaCryptoProvider();
    private final EventLoop eventLoop = new DummyEventLoop();
    private final DomainNameResolver resolver = new StaticDomainNameResolver(eventLoop);
    private final FakeJingleSession session = 
            new FakeJingleSession(new JID("foo@bar.com/baz"),"mysession");
    private final JingleContentPayload jingleContentPayload = new JingleContentPayload();
    private final DummyStanzaChannel stanzaChannel = new DummyStanzaChannel();
    private final DummyConnectionFactory connectionFactory = 
            new DummyConnectionFactory(eventLoop);
    private final DummyConnectionServerFactory serverConnectionFactory = 
            new DummyConnectionServerFactory(eventLoop);
    private final IQRouter iqRouter = new IQRouter(stanzaChannel);
    private final SOCKS5BytestreamRegistry bytestreamRegistry = new SOCKS5BytestreamRegistry();
    private final NetworkEnvironment networkEnvironment = new DummyNetworkEnvironment();
    private final NATTraverser natTraverser = new NullNATTraverser(eventLoop);
    private final SOCKS5BytestreamServerManager bytestreamServerManager = 
            new SOCKS5BytestreamServerManager(bytestreamRegistry, serverConnectionFactory, 
                    networkEnvironment, natTraverser);
    private final IDGenerator idGenerator = new SimpleIDGenerator();
    private final DummyTimerFactory timerFactory = new DummyTimerFactory();
    private final SOCKS5BytestreamProxiesManager bytestreamProxy = 
            new SOCKS5BytestreamProxiesManager(connectionFactory, timerFactory, resolver, 
                    iqRouter, new JID("bar.com"));
    private final FileTransferTransporterFactory ftTransporterFactory =
            new DefaultFileTransferTransporterFactory(bytestreamRegistry, bytestreamServerManager, 
                    bytestreamProxy, idGenerator, connectionFactory, timerFactory, crypto, iqRouter);
    
    
    @Test
    public void test_AcceptOnyIBBSendsSessionAccept() {
        // Tests whether IncomingJingleFileTransfer would accept a IBB only file transfer.
        // 1 Create your test incoming file transfer
        JingleFileTransferDescription desc = new JingleFileTransferDescription();
        desc.setFileInfo(new JingleFileTransferFileInfo("foo.tx", "", 10));
        jingleContentPayload.addDescription(desc);
        JingleIBBTransportPayload tpRef = new JingleIBBTransportPayload();
        tpRef.setSessionID("mysession");
        jingleContentPayload.addTransport(tpRef);

        IncomingJingleFileTransfer fileTransfer = createTestling();

        // 2 Do 'accept' on a dummy writebytestream (you'll have to look if there already is one)
        ByteArrayWriteBytestream byteStream = new ByteArrayWriteBytestream();
        fileTransfer.accept(byteStream);;

        // 3 Check whether accept has been called
        getCall(FakeJingleSession.AcceptCall.class,0);
    }
    
    @Test
    public void test_OnlyIBBTransferReceiveWorks() {
        // 1 Create your test incoming file transfer
        JingleFileTransferDescription desc = new JingleFileTransferDescription();
        desc.setFileInfo(new JingleFileTransferFileInfo("foo.tx", "", 10));
        jingleContentPayload.addDescription(desc);
        JingleIBBTransportPayload tpRef = new JingleIBBTransportPayload();
        tpRef.setSessionID("mysession");
        jingleContentPayload.addTransport(tpRef);

        IncomingJingleFileTransfer fileTransfer = createTestling();

        // 2 Do 'accept' on a dummy writebytestream (you'll have to look if there already is one)
        ByteArrayWriteBytestream byteStream = new ByteArrayWriteBytestream();
        fileTransfer.accept(byteStream);;

        // 3 Check whether accept has been called
        getCall(FakeJingleSession.AcceptCall.class,0);
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBOpen("myession", 10), 
                new JID("foo@bar.com/baz"), "id-open"));
        stanzaChannel.onIQReceived.emit(createIBBRequest(
                IBB.createIBBData("mysession", 0, new ByteArray("abc")), 
                new JID("foo@bar.com/baz"), "id-open"));
        assertEquals(new ByteArray("abc"),byteStream.getData());
    }
    
//    This test is not run in the Swiften code (If it is run it fails there too)
//
//    public void test_AcceptFailingS5BFallsBackToIBB() {
//        // 1 Create your test incoming file transfer
//        addFileTransferDescription();
//        
//        // add SOCKS5BytestreamTransportPayload
//        JingleS5BTransportPayload payload = addJingleS5BPayload();
//        
//        IncomingJingleFileTransfer fileTransfer = createTestling();
//        
//        // 2 Do 'accept' on a dummy writebytestream (you'll have to look if there already is one)
//        ByteArrayWriteBytestream byteStream = new ByteArrayWriteBytestream();
//        fileTransfer.accept(byteStream);;
//        
//        // Candidates are gathered
//        
//        // Check whether accept has been called
//        FakeJingleSession.AcceptCall acceptCall = getCall(FakeJingleSession.AcceptCall.class, 0);
//        assertEquals(payload.getSessionID(),acceptCall.payload.getSessionID());
//        
//        // Check for candiate error
//        FakeJingleSession.InfoTransportCall infoTransportCall = getCall(FakeJingleSession.InfoTransportCall.class,1);
//        JingleS5BTransportPayload s5bPayload = null;
//        if (infoTransportCall.payload instanceof JingleS5BTransportPayload) {
//            s5bPayload = (JingleS5BTransportPayload) infoTransportCall.payload;
//        }
//        assertNotNull(s5bPayload);
//        assertTrue(s5bPayload.hasCandidateError());
//        
//        // Indicate transport replace (Romeo)
//        session.handleTransportReplaceReceived(getContentID(), addJingleIBBPayload());
//        
//        FakeJingleSession.AcceptTransportCall acceptTranpsportCall = getCall(FakeJingleSession.AcceptTransportCall.class,2);
//        
//        // Send a bit of data
//        stanzaChannel.onIQReceived.emit(createIBBRequest(IBB.createIBBOpen("mysession", 10), new JID("foo@bar.com/baz"), "id-open"));
//        stanzaChannel.onIQReceived.emit(createIBBRequest(IBB.createIBBData("mysession", 0, new ByteArray("abc")), new JID("foo@bar.com/baz"), "id-a"));
//        assertEquals(new ByteArray("abc"),byteStream.getData());
//        
//    }
    
    private IncomingJingleFileTransfer createTestling() {
        JID ourJID = new JID("");
        return new IncomingJingleFileTransfer(ourJID, session, jingleContentPayload, 
                ftTransporterFactory, timerFactory, crypto);
    }
    
    private IQ createIBBRequest(IBB payload,JID from,String id) {
        IQ request = IQ.createRequest(Type.Set, new JID("foo@bar.com/baz"), id, payload);
        request.setFrom(from);
        return request;
    }
    
    private void addFileTransferDescription() {
        JingleFileTransferDescription desc = new JingleFileTransferDescription();
        desc.setFileInfo(new JingleFileTransferFileInfo("file.txt", "", 10));
        jingleContentPayload.addDescription(desc);
    }
    
    private JingleS5BTransportPayload addJingleS5BPayload() {
        JingleS5BTransportPayload payLoad = new JingleS5BTransportPayload();
        payLoad.setSessionID("mysession");
        jingleContentPayload.addTransport(payLoad);
        return payLoad;
    }
    
    private JingleIBBTransportPayload addJingleIBBPayload() {
        JingleIBBTransportPayload payLoad = new JingleIBBTransportPayload();
        payLoad.setSessionID("mysession");
        jingleContentPayload.addTransport(payLoad);
        return payLoad;
    }
    
    private JingleContentID getContentID() {
        return new JingleContentID(jingleContentPayload.getName(),
                jingleContentPayload.getCreator());
    }
    
    private <T> T getCall(Class<T> target,int i) {
        assertTrue("Index "+i+" is not less then session.calledCommands.size() = "
                    +session.calledCommands.size(),
                    i < session.calledCommands.size());
        Object rawObject = session.calledCommands.get(i);
        try {
            return target.cast(rawObject);
        }
        catch (ClassCastException e) {
            fail("Item could not be cast to type "+e.getMessage());
        }
        // Should not get here
        return null;
    }
    
    
}
