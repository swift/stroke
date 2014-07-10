/*
 * Copyright (c) 2010-2014 Remko Tronçon
 * All rights reserved. */
/*
 * Copyright (c) 2010-2014, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.session;

import java.util.List;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.streamstack.CompressionLayer;
import com.isode.stroke.streamstack.ConnectionLayer;
import com.isode.stroke.streamstack.StreamStack;
import com.isode.stroke.streamstack.TLSLayer;
import com.isode.stroke.streamstack.WhitespacePingLayer;
import com.isode.stroke.streamstack.XMPPLayer;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.TLSContextFactory;

public class BasicSessionStream extends SessionStream {

    public BasicSessionStream(
            StreamType streamType,
            Connection connection,
            PayloadParserFactoryCollection payloadParserFactories,
            PayloadSerializerCollection payloadSerializers,
            TLSContextFactory tlsContextFactory,
            TimerFactory timerFactory) {
        available = false;
        this.connection = connection;
        this.payloadParserFactories = payloadParserFactories;
        this.payloadSerializers = payloadSerializers;
        this.tlsContextFactory = tlsContextFactory;
        this.timerFactory = timerFactory;
        if (timerFactory == null) {
            throw new IllegalStateException(); //FIXME: remove conditional, debugging only.
        }
        this.streamType = streamType;
        this.compressionLayer = null;
        this.tlsLayer = null;
        this.whitespacePingLayer = null;

        xmppLayer = new XMPPLayer(payloadParserFactories, payloadSerializers, streamType);
        xmppLayer.onStreamStart.connect(new Slot1<ProtocolHeader>() {

            public void call(ProtocolHeader p1) {
                handleStreamStartReceived(p1);
            }
        });
        xmppLayer.onElement.connect(new Slot1<Element>() {

            public void call(Element p1) {
                handleElementReceived(p1);
            }
        });
        xmppLayer.onError.connect(new Slot() {

            public void call() {
                handleXMPPError();
            }
        });
        xmppLayer.onDataRead.connect(new Slot1<ByteArray>() {

            public void call(ByteArray p1) {
                handleDataRead(p1);
            }
        });
        xmppLayer.onWriteData.connect(new Slot1<ByteArray>() {

            public void call(ByteArray p1) {
                handleDataWritten(p1);
            }
        });

        connection.onDisconnected.connect(new Slot1<Connection.Error>() {

            public void call(Connection.Error p1) {
                handleConnectionFinished(p1);
            }
        });
        connectionLayer = new ConnectionLayer(connection);

        streamStack = new StreamStack(xmppLayer, connectionLayer);

        available = true;

    }

    public void writeHeader(ProtocolHeader header) {
        assert available;
        xmppLayer.writeHeader(header);
    }

    public void writeElement(Element element) {
        assert available;
        xmppLayer.writeElement(element);
    }

    public void writeFooter() {
        assert available;
        xmppLayer.writeFooter();
    }

    public void writeData(String data) {
        assert available;
        xmppLayer.writeData(data);
    }

    public void close() {
        connection.disconnect();
    }

    public boolean isOpen() {
        return available;
    }

    public boolean supportsTLSEncryption() {
        return tlsContextFactory != null && tlsContextFactory.canCreate();
    }

    public void addTLSEncryption() {
        assert available;
        tlsLayer = new TLSLayer(tlsContextFactory);
        if (hasTLSCertificate() && !tlsLayer.setClientCertificate(getTLSCertificate())) {
            onClosed.emit(new Error(Error.Type.InvalidTLSCertificateError));
        } else {
            streamStack.addLayer(tlsLayer);
            tlsLayer.onError.connect(new Slot() {

                public void call() {
                    handleTLSError();
                }
            });
            tlsLayer.onConnected.connect(new Slot() {

                public void call() {
                    handleTLSConnected();
                }
            });
            tlsLayer.connect();
        }
    }

    public boolean isTLSEncrypted() {
        return tlsLayer != null;
    }

    @Override
    public List<Certificate> getPeerCertificateChain() {
        return tlsLayer.getPeerCertificateChain();
    }
    @Override
    public Certificate getPeerCertificate() {
        return tlsLayer.getPeerCertificate();
    }

    public CertificateVerificationError getPeerCertificateVerificationError() {
        return tlsLayer.getPeerCertificateVerificationError();
    }

    public ByteArray getTLSFinishMessage() {
        return tlsLayer.getContext().getFinishMessage();
    }

    public void addZLibCompression() {
        compressionLayer = new CompressionLayer();
        streamStack.addLayer(compressionLayer);
    }

    public void setWhitespacePingEnabled(boolean enabled) {
        if (enabled) {
            if (whitespacePingLayer == null) {
                whitespacePingLayer = new WhitespacePingLayer(timerFactory);
                streamStack.addLayer(whitespacePingLayer);
            }
            whitespacePingLayer.setActive();
        }
        else if (whitespacePingLayer != null) {
            whitespacePingLayer.setInactive();
        }
    }

    public void resetXMPPParser() {
        xmppLayer.resetParser();
    }

    private void handleStreamStartReceived(ProtocolHeader header) {
        onStreamStartReceived.emit(header);
    }

    private void handleElementReceived(Element element) {
        onElementReceived.emit(element);
    }

    private void handleXMPPError() {
        available = false;
        onClosed.emit(new Error(Error.Type.ParseError));
    }

    private void handleTLSConnected() {
        onTLSEncrypted.emit();
    }

    private void handleTLSError() {
        available = false;
        onClosed.emit(new Error(Error.Type.TLSError));
    }

    private void handleConnectionFinished(Connection.Error error) {
        available = false;
        if (Connection.Error.ReadError.equals(error)) {
            onClosed.emit(new Error(Error.Type.ConnectionReadError));
        }
        else if (error != null) {
            onClosed.emit(new Error(Error.Type.ConnectionWriteError));
        }
        else {
            onClosed.emit(null);
        }
    }

    private void handleDataRead(ByteArray data) {
        onDataRead.emit(data.toString());
    }

    private void handleDataWritten(ByteArray data) {
        onDataWritten.emit(data.toString());
    }
    @Override
    public String toString() {
            return super.toString() + 
            "; available=" + available;
    }

    private boolean available;
    private Connection connection;
    private PayloadParserFactoryCollection payloadParserFactories;
    private PayloadSerializerCollection payloadSerializers;
    private TLSContextFactory tlsContextFactory;
    private TimerFactory timerFactory;
    private StreamType streamType;
    private XMPPLayer xmppLayer;
    private ConnectionLayer connectionLayer;
    private CompressionLayer compressionLayer;
    private TLSLayer tlsLayer;
    private WhitespacePingLayer whitespacePingLayer;
    private StreamStack streamStack;
    
}
