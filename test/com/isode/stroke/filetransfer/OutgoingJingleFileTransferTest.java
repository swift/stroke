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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.elements.IBB;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.JinglePayload.Reason;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.filetransfer.FileTransfer.State;
import com.isode.stroke.filetransfer.FileTransferError.Type;
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
import com.isode.stroke.signals.Slot1;

/**
 * Tests for {@link OutgoingJingleFileTransfer}
 *
 */
public class OutgoingJingleFileTransferTest {

    private final CryptoProvider crypto = new JavaCryptoProvider();
    private final FakeJingleSession fakeJingleSession = 
            new FakeJingleSession(new JID("foo@bar.com/baz"), "mysession");
    private final JingleContentPayload jingleContentPayload = new JingleContentPayload();
    private final DummyStanzaChannel stanzaChannel = new DummyStanzaChannel();
    private final IQRouter iqRouter = new IQRouter(stanzaChannel);
    private final EventLoop eventLoop = new DummyEventLoop();
    private final DummyTimerFactory timeFactory = new DummyTimerFactory();
    private final DummyConnectionFactory connectionFactory = new DummyConnectionFactory(eventLoop);
    private final DummyConnectionServerFactory serverConnectionFactory = new DummyConnectionServerFactory(eventLoop);
    private final SOCKS5BytestreamRegistry s5bRegistry = new SOCKS5BytestreamRegistry();
    private final NetworkEnvironment networkEnviroment = new DummyNetworkEnvironment();
    private final NATTraverser natTraverser = new NullNATTraverser(eventLoop);
    private final SOCKS5BytestreamServerManager bytestreamServerManager = 
            new SOCKS5BytestreamServerManager(s5bRegistry, serverConnectionFactory, networkEnviroment, natTraverser);
    private final ByteArray data = new ByteArray();
    {
        for (int n = 0; n < (1024 * 1024); ++n) {
            data.append((byte)34);
        }
    }
    private final ByteArrayReadBytestream stream = new ByteArrayReadBytestream(data);
    private final IDGenerator idGen = new IDGenerator();
    private final DomainNameResolver resolver = new StaticDomainNameResolver(eventLoop);
    private final SOCKS5BytestreamProxiesManager s5bProxy = 
            new SOCKS5BytestreamProxiesManager(connectionFactory, timeFactory, resolver, 
                    iqRouter, new JID("bar.com"));
    private final FileTransferTransporterFactory ftTransporterFactory =
            new DummyFileTransferTransporterFactory(s5bRegistry, bytestreamServerManager, s5bProxy, idGen, connectionFactory, timeFactory, crypto, iqRouter);
    
    @Test
    public void test_SendSessionInitiateOnStart() {
        OutgoingJingleFileTransfer transfer = createTestling();
        transfer.start();
        
        FakeJingleSession.InitiateCall call = getCall(FakeJingleSession.InitiateCall.class,0);
        JingleFileTransferDescription description = null;
        if (call.description instanceof JingleFileTransferDescription) {
            description = (JingleFileTransferDescription) call.description;
        }
        assertNotNull(description);
        assertEquals(1048576,description.getFileInfo().getSize());
        
        JingleIBBTransportPayload transport = null;
        if (call.payload instanceof JingleIBBTransportPayload) {
            transport = (JingleIBBTransportPayload) call.payload;
        }
        else {
            System.out.println(call.payload.getClass().getName());
        }
        assertNotNull(transport);
    }
    
    @Test
    public void test_FallbackToIBBAfterFailingS5b() {
        OutgoingJingleFileTransfer transfer = 
                createTestling(new FileTransferOptions().withAssistedAllowed(true).withDirectAllowed(true).withProxiedAllowed(true));
        transfer.start();
        
        FakeJingleSession.InitiateCall call = getCall(FakeJingleSession.InitiateCall.class,0);
        
        assertTrue(call.payload instanceof JingleS5BTransportPayload);
        fakeJingleSession.handleSessionAcceptReceived(call.id, call.description, call.payload);
        
        // Send candidate failure
        JingleS5BTransportPayload candiateFailurePayload = new JingleS5BTransportPayload();
        candiateFailurePayload.setCandidateError(true);
        candiateFailurePayload.setSessionID(call.payload.getSessionID());
        fakeJingleSession.handleTransportInfoReceived(call.id, candiateFailurePayload);
        
        // no S5B candidates -> fall back to IBB
        // call at position 1 is the candidate our candidate error
        FakeJingleSession.ReplaceTransportCall replaceCall = 
                getCall(FakeJingleSession.ReplaceTransportCall.class,2);
        
        // accept transport replace
        fakeJingleSession.handleTransportAcceptReceived(replaceCall.id, replaceCall.payload);
        
        IQ iqOpenStanza = stanzaChannel.getStanzaAtIndex(new IQ(), 0);
        assertNotNull(iqOpenStanza);
        IBB ibbOpen = iqOpenStanza.getPayload(new IBB());
        assertNotNull(ibbOpen);
        assertEquals(IBB.Action.Open,ibbOpen.getAction());
        
    }
    
    @Test
    public void test_ReceiveSessionTerminateAfterSessionInitiate() {
        OutgoingJingleFileTransfer transfer = createTestling();
        transfer.start();
        
        getCall(FakeJingleSession.InitiateCall.class,0);
        
        final FTStatusHelper helper = new FTStatusHelper();
        helper.finishedCalled = false;
        transfer.onFinished.connect(new Slot1<FileTransferError>() {
            
            @Override
            public void call(FileTransferError error) {
                helper.handleFileTransferFinished(error);
            }
            
        });
        fakeJingleSession.handleSessionTerminateReceived(new Reason(Reason.Type.Busy));
        assertTrue(helper.finishedCalled);
        assertEquals(FileTransferError.Type.PeerError,helper.errorType);
    }

    @Test
    public void test_DeclineEmitsFinishedStateCanceled() {
        OutgoingJingleFileTransfer transfer = createTestling();
        transfer.start();
        
        getCall(FakeJingleSession.InitiateCall.class,0);
        
        final FTStatusHelper helper = new FTStatusHelper();
        helper.finishedCalled = false;
        transfer.onFinished.connect(new Slot1<FileTransferError>() {

            @Override
            public void call(FileTransferError error) {
                helper.handleFileTransferFinished(error);
            }
            
        });
        transfer.onStateChanged.connect(new Slot1<FileTransfer.State>() {

            @Override
            public void call(State newState) {
                helper.handleFileTransferStatusChanged(newState);
            }
            
        });
        fakeJingleSession.handleSessionTerminateReceived(new Reason(Reason.Type.Decline));
        assertTrue(helper.finishedCalled);
        assertEquals(FileTransferError.Type.UnknownError, helper.errorType);
        assertEquals(State.Type.Canceled,helper.state.type);
    }
    
    private static class FTStatusHelper {
        
        public FTStatusHelper() {
            // Empty Constructor
        }
        
        public void handleFileTransferFinished(FileTransferError error) {
            finishedCalled = true;
            if (error != null) {
                errorType = error.getType();
            }
        }
        
        public void handleFileTransferStatusChanged(State fileTransferState) {
            state = fileTransferState;
        }
        private boolean finishedCalled = false;
        private Type errorType = Type.UnknownError;
        private State state = null;
    }
    
    private OutgoingJingleFileTransfer createTestling() {
        return createTestling(new FileTransferOptions().withAssistedAllowed(false).withDirectAllowed(false).withProxiedAllowed(false));
    }
    
    private OutgoingJingleFileTransfer createTestling(FileTransferOptions options) {
        JID to = new JID("test@foo.com/bla");
        JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
        fileInfo.setDescription("some file");
        fileInfo.setName("test.bin");
        fileInfo.addHash(new HashElement("sha-1", new ByteArray()));
        fileInfo.setSize(1024 * 1024);
        return new OutgoingJingleFileTransfer(to, fakeJingleSession, stream, 
                ftTransporterFactory, timeFactory, idGen, fileInfo, options, crypto);
    }
    
    private IQ createIBBRequest(IBB ibb,JID from,String id) {
        IQ request = IQ.createRequest(IQ.Type.Set, new JID("foo@bar.com/baz"), id, ibb);
        request.setFrom(from);
        return request;
    }
    
    private void addFileTransferDescription() {
        JingleFileTransferDescription desc = new JingleFileTransferDescription();
        desc.setFileInfo(new JingleFileTransferFileInfo());
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
        assertTrue(i < fakeJingleSession.calledCommands.size());
        Object rawObject = fakeJingleSession.calledCommands.get(i);
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
