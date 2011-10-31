/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.streamstack;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.XMPPParser;
import com.isode.stroke.parser.XMPPParserClient;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.XMPPSerializer;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;

/**
 * This uses the inner StreamLayer to work around the HighLayer not having
 * implementations because of the lack of multiple inheritance.
 * Swiften doesn't require an eventLoop, Stroke does because of
 * XML parsing being multi-threaded here.
 */
public class XMPPLayer implements HighLayer, XMPPParserClient {

    public XMPPLayer(
            PayloadParserFactoryCollection payloadParserFactories,
            PayloadSerializerCollection payloadSerializers,
            StreamType streamType,
            EventLoop eventLoop) {
        payloadParserFactories_ = payloadParserFactories;
        payloadSerializers_ = payloadSerializers;
        resetParserAfterParse_ = false;
        eventLoop_ = eventLoop;
        inParser_ = false;
        xmppParser_ = new XMPPParser(this, payloadParserFactories_, eventLoop_);
        xmppSerializer_ = new XMPPSerializer(payloadSerializers_, streamType);
    }

    public void writeHeader(ProtocolHeader header) {
        writeDataInternal(new ByteArray(xmppSerializer_.serializeHeader(header)));
    }

    public void writeFooter() {
        writeDataInternal(new ByteArray(xmppSerializer_.serializeFooter()));
    }

    public void writeElement(Element element) {
        writeDataInternal(new ByteArray(xmppSerializer_.serializeElement(element)));
    }

    public void writeData(String data) {
        writeDataInternal(new ByteArray(data));
    }

    public void resetParser() {
        if (inParser_) {
            resetParserAfterParse_ = true;
        }
        else {
            doResetParser();
        }
    }

    /**
     * Should be protected, but can't because of interface implementation.
     * @param data
     */
    public void handleDataRead(ByteArray data) {
        handleDataReadInternal(data);
    }

    protected void writeDataInternal(ByteArray data) {
        onWriteData.emit(data);
        writeDataToChildLayer(data);
    }

    public final Signal1<ProtocolHeader> onStreamStart = new Signal1<ProtocolHeader>();
    public final Signal1<Element> onElement = new Signal1<Element>();
    public final Signal1<ByteArray> onWriteData = new Signal1<ByteArray>();
    public final Signal1<ByteArray> onDataRead = new Signal1<ByteArray>();
    public final Signal onError = new Signal();

    public void handleStreamStart(ProtocolHeader header) {
        onStreamStart.emit(header);
    }

    public void handleElement(Element element) {
        onElement.emit(element);
    }

    public void handleStreamEnd() {
    }

    private void doResetParser() {
        xmppParser_ = new XMPPParser(this, payloadParserFactories_, eventLoop_);
        resetParserAfterParse_ = false;
    }
    
    private PayloadParserFactoryCollection payloadParserFactories_;
    private XMPPParser xmppParser_;
    private PayloadSerializerCollection payloadSerializers_;
    private XMPPSerializer xmppSerializer_;
    private boolean resetParserAfterParse_;
    private boolean inParser_;
    private EventLoop eventLoop_;

    /* Multiple-inheritance workarounds */

    private StreamLayer fakeStreamLayer_ = new StreamLayer() {
        public void writeData(ByteArray data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void handleDataRead(ByteArray data) {
            handleDataReadInternal(data);
        }
    };

    private void handleDataReadInternal(ByteArray data) {
        onDataRead.emit(data);
        inParser_ = true;
        if(!xmppParser_.parse(data.toString())) {
            inParser_ = false;
            onError.emit();
            return;
        }
        inParser_ = false;
        if (resetParserAfterParse_) {
            doResetParser();
        }
    }

    public LowLayer getChildLayer() {
        return fakeStreamLayer_.getChildLayer();
    }

    public void setChildLayer(LowLayer childLayer) {
        fakeStreamLayer_.setChildLayer(childLayer);
    }

    public void writeDataToChildLayer(ByteArray data) {
        fakeStreamLayer_.writeDataToChildLayer(data);
    }
}
