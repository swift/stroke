/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.session;

import java.util.List;
import java.util.Random;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.SafeString;
import com.isode.stroke.base.URL;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.elements.TopLevelElement;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.network.BOSHConnection.BOSHError;
import com.isode.stroke.network.BOSHConnectionPool;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.network.HTTPTrafficFilter;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.XMLParserFactory;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.session.SessionStream.SessionStreamError.Type;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.streamstack.XMPPLayer;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.TLSContextFactory;
import com.isode.stroke.tls.TLSOptions;

public class BOSHSessionStream extends SessionStream implements EventOwner {

    private final BOSHConnectionPool connectionPool_;
    private boolean available_ = false;
    private final XMPPLayer xmppLayer_;
    private ProtocolHeader streamHeader_ = new ProtocolHeader();
    private final EventLoop eventLoop_;
    private boolean firstHeader_ = true;
    
    /**
     * Max value for generated RID
     */
    private final static long MAXRID = (1 << 53);
    
    /**
     * {@link Random} for generating the RID
     */
    private final static Random rng = new Random();
    private final SignalConnection poolSessionTerminatedConnection;
    private final SignalConnection poolSessionStartedConnection;
    private final SignalConnection poolXMPPDataReadConnection;
    private final SignalConnection poolBOSHDataReadConnection;
    private SignalConnection poolBOSHDataWrittenConnection;
    private final SignalConnection xMPPLayerDataWrittenConnection;
    private final SignalConnection xMPPErrorConnection;
    private final SignalConnection elementReceivedConnection;
    private final SignalConnection streamStartReceivedConnection;
    
    /**
     * 
     */
    public BOSHSessionStream(URL boshURL,
            PayloadParserFactoryCollection payloadParserFactories,
            PayloadSerializerCollection payloadSerializers,
            ConnectionFactory connectionFactory,
            TLSContextFactory tlsContextFactory,
            TimerFactory timerFactory,
            XMLParserFactory xmlParserFactory,
            EventLoop eventLoop,
            DomainNameResolver resolver,
            String to,
            URL boshHTTPConnectProxyURL,
            SafeString boshHTTPConnectProxyAuthID,
            SafeString boshHTTPConnectProxyAuthPassword,
            TLSOptions tlsOptions,
            HTTPTrafficFilter trafficFilter) {
        
        eventLoop_ = eventLoop;
        long initialRID = generateRandomRID();
        
        connectionPool_ = new BOSHConnectionPool(boshURL, resolver, connectionFactory, 
                xmlParserFactory, tlsContextFactory, timerFactory, eventLoop, to, 
                initialRID, boshHTTPConnectProxyURL, boshHTTPConnectProxyAuthID, 
                boshHTTPConnectProxyAuthPassword, tlsOptions, trafficFilter);
        poolSessionTerminatedConnection = connectionPool_.onSessionTerminated.connect(new Slot1<BOSHError>() {
            
            @Override
            public void call(BOSHError error) {
                handlePoolSessionTerminated(error);
            }
            
        });
        poolSessionStartedConnection = connectionPool_.onSessionStarted.connect(new Slot() {
            
            @Override
            public void call() {
                handlePoolSessionStarted();
            }
            
        });
        poolXMPPDataReadConnection = connectionPool_.onXMPPDataRead.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handlePoolXMPPDataRead(data);
            }
            
        });
        poolBOSHDataReadConnection = connectionPool_.onBOSHDataRead.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handlePoolBOSHDataRead(data);
            }
            
        });
        poolBOSHDataWrittenConnection = connectionPool_.onBOSHDataWritten.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handlePoolBOSHDataWritten(data);
            }
            
        });
        
        
        xmppLayer_ = new XMPPLayer(payloadParserFactories, payloadSerializers, StreamType.ClientStreamType, true);
        streamStartReceivedConnection = xmppLayer_.onStreamStart.connect(new Slot1<ProtocolHeader>() {

            @Override
            public void call(ProtocolHeader header) {
                handleStreamStartReceived(header);
            }
            
        });
        elementReceivedConnection = xmppLayer_.onElement.connect(new Slot1<Element>() {

            @Override
            public void call(Element element) {
                handleElementReceived(element);
            }
            
        });
        xMPPErrorConnection = xmppLayer_.onError.connect(new Slot() {
            
            @Override
            public void call() {
                handleXMPPError();
            }
            
        });
        xMPPLayerDataWrittenConnection = xmppLayer_.onWriteData.connect(new Slot1<SafeByteArray>() {

            @Override
            public void call(SafeByteArray data) {
                handleXMPPLayerDataWritten(data);
            }
            
        });
        
        
        available_ = true;
    }
    
    private static long generateRandomRID() {
        long random = -1;
        do {
            random = rng.nextLong();
        } while (random < 0 && random > MAXRID);
        return random;
    }

    public void open() {
        connectionPool_.setTLSCertificate(getTLSCertificate());
        connectionPool_.open();
    }
    
    @Override
    public void close() {
        connectionPool_.close();
    }

    @Override
    public boolean isOpen() {
        return available_;
    }
    
    @Override
    public void writeHeader(ProtocolHeader header) {
        streamHeader_ = header;
        // First time we're told to do this, don't (the sending of the initial header is handled on connect)
        //  On subsequent requests we should restart the stream the BOSH way.

        if (!firstHeader_) {
            eventLoop_.postEvent(new Callback() {
                
                @Override
                public void run() {
                    fakeStreamHeaderReceipt();
                }
                
            });
            eventLoop_.postEvent(new Callback() {
                
                @Override
                public void run() {
                    connectionPool_.restartStream();
                }
                
            });
        }
        firstHeader_ = false;

    }
    
    @Override
    public void writeFooter() {
        connectionPool_.writeFooter();
    }

    @Override
    public void writeElement(Element element) {
        assert(available_);
        xmppLayer_.writeElement(element);
    }

    @Override
    public void writeData(String data) {
        assert(available_);
        xmppLayer_.writeData(data);
    }

    @Override
    public boolean supportsZLibCompression() {
        return false;
    }

    @Override
    public void addZLibCompression() {
        // Empty Method
    }

    @Override
    public boolean supportsTLSEncryption() {
        return false;
    }

    @Override
    public void addTLSEncryption() {
        assert(available_);
    }

    @Override
    public boolean isTLSEncrypted() {
        return connectionPool_.isTLSEncrypted();
    }

    @Override
    public void setWhitespacePingEnabled(boolean enabled) {
        // Empty Method
    }

    @Override
    public void resetXMPPParser() {
        xmppLayer_.resetParser();
    }

    @Override
    public void disconnect() {
        poolSessionTerminatedConnection.disconnect();
        poolSessionStartedConnection.disconnect();
        poolXMPPDataReadConnection.disconnect();
        poolBOSHDataReadConnection.disconnect();
        poolBOSHDataWrittenConnection.disconnect();
        xMPPLayerDataWrittenConnection.disconnect();
        xMPPErrorConnection.disconnect();
        elementReceivedConnection.disconnect();
        streamStartReceivedConnection.disconnect();
    }

    @Override
    public List<Certificate> getPeerCertificateChain() {
        return connectionPool_.getPeerCertificateChain();
    }

    @Override
    public Certificate getPeerCertificate() {
        return connectionPool_.getPeerCertificate();
    }

    @Override
    public CertificateVerificationError getPeerCertificateVerificationError() {
        return connectionPool_.getPeerCertificateVerificationError();
    }

    @Override
    public ByteArray getTLSFinishMessage() {
        return new ByteArray();
    }
    
    private void handleXMPPError() {
        available_ = false;
        onClosed.emit(new SessionStreamError(Type.ParseError));
    }
    
    private void handleStreamStartReceived(ProtocolHeader header) {
        onStreamStartReceived.emit(header);
    }
    
    private void handleElementReceived(Element element) {
        onElementReceived.emit(element);
    }
    
    private void handlePoolXMPPDataRead(SafeByteArray data) {
        xmppLayer_.handleDataRead(data);
    }
    
    private void handleXMPPLayerDataWritten(final SafeByteArray data) {
        eventLoop_.postEvent(new Callback() {
            
            @Override
            public void run() {
                connectionPool_.write(data);
            }
            
        });
    }
    
    private void handlePoolSessionStarted() {
        fakeStreamHeaderReceipt();
    }
    
    private void handlePoolBOSHDataRead(SafeByteArray data) {
        onDataRead.emit(data);
    }
    
    private void handlePoolBOSHDataWritten(SafeByteArray data) {
        onDataWritten.emit(data);
    }
    
    private void handlePoolSessionTerminated(final BOSHError error) {
        eventLoop_.postEvent(new Callback() {
            
            @Override
            public void run() {
                fakeStreamFooterReceipt(error);
            }
            
        });
    }
    
    private void fakeStreamHeaderReceipt() {
        StringBuilder header = new StringBuilder();
        header.append("<stream:stream xmlns='jabber:client' "
                + "xmlns:stream='http://etherx.jabber.org/streams' from='");
        header.append(streamHeader_.getTo());
        header.append("' id='dummy' version='1.0'>");
        
        xmppLayer_.handleDataRead(new SafeByteArray(header.toString()));
    }
    
    private void fakeStreamFooterReceipt(BOSHError error) {
        String footer = "</stream:stream>";
        xmppLayer_.handleDataRead(new SafeByteArray(footer));
        onClosed.emit(error);
    }

}
