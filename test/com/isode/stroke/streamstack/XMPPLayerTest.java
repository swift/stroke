/*
 * Copyright (c) 2010-2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.streamstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.streamstack.XMPPLayer;
import com.isode.stroke.streamstack.StreamStack;
import com.isode.stroke.streamstack.LowLayer;
import com.isode.stroke.streamstack.XMPPLayer;
import com.isode.stroke.streamstack.StreamLayer;
import com.isode.stroke.parser.PlatformXMLParserFactory;
import com.isode.stroke.parser.payloadparsers.FullPayloadParserFactoryCollection;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot;
import java.util.Vector;

public class XMPPLayerTest {

	private class XMPPLayerExposed extends XMPPLayer {

		public XMPPLayerExposed(PayloadParserFactoryCollection payloadParserFactories, PayloadSerializerCollection payloadSerializers, StreamType streamType) {
			super(payloadParserFactories, payloadSerializers, streamType);
		}

		//using XMPPLayer::handleDataRead;
		//using HighLayer::setChildLayer;
	};

	/* Multiple-inheritance workarounds */

	private StreamLayer fakeStreamLayer_ = new StreamLayer() {
		public void writeData(SafeByteArray data) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void handleDataRead(SafeByteArray data) {
			writeDataToParentLayer(data);
		}
	};

	private class DummyLowLayer implements LowLayer {

		public String writtenData = "";

		public DummyLowLayer() {

		}

		public void writeData(final SafeByteArray data) {
			writtenData += data.toString();
		}
	
		public HighLayer getParentLayer() {
			return fakeStreamLayer_.getParentLayer();
		}

		public void setParentLayer(HighLayer highLayer) {
			fakeStreamLayer_.setParentLayer(highLayer);
		}

		public void writeDataToParentLayer(SafeByteArray data) {
			fakeStreamLayer_.writeDataToParentLayer(data);
		}
	};

	private FullPayloadParserFactoryCollection parserFactories_ = new FullPayloadParserFactoryCollection();
	private FullPayloadSerializerCollection serializers_ = new FullPayloadSerializerCollection();
	private DummyLowLayer lowLayer_;
	private XMPPLayerExposed testling_;
	private PlatformXMLParserFactory xmlParserFactory_;
	private int elementsReceived_;
	private int errorReceived_;

	@Before
	public void setUp() {
		lowLayer_ = new DummyLowLayer();
		testling_ = new XMPPLayerExposed(parserFactories_, serializers_, StreamType.ClientStreamType);
		testling_.setChildLayer(lowLayer_);
		elementsReceived_ = 0;
		errorReceived_ = 0;
	}

	public void handleElement(Element element) {
		++elementsReceived_;
	}

	public void handleElementAndReset(Element element) {
		++elementsReceived_;
		testling_.resetParser();
	}

	public void handleError() {
		++errorReceived_;
	}

	@Test
	public void testParseData_Error() {
		testling_.onError.connect(new Slot() {
			@Override
			public void call() {
				handleError();
			}
		});

		testling_.handleDataRead(new SafeByteArray("<iq>"));

		assertEquals(1, errorReceived_);
	}

	@Test
	public void testResetParser() {
		testling_.onElement.connect(new Slot1<Element>() {
			@Override
			public void call(Element e1) {
				handleElement(e1);
			}
		});
		testling_.onError.connect(new Slot() {
			@Override
			public void call() {
				handleError();
			}
		});

		testling_.handleDataRead(new SafeByteArray("<stream:stream to=\"example.com\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" >"));
		testling_.resetParser();
		testling_.handleDataRead(new SafeByteArray("<stream:stream to=\"example.com\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" >"));
		testling_.handleDataRead(new SafeByteArray("<presence/>"));

		assertEquals(1, elementsReceived_);
		assertEquals(0, errorReceived_);
	}

	@Test
	public void testResetParser_FromSlot() {
		testling_.onElement.connect(new Slot1<Element>() {
			@Override
			public void call(Element e1) {
				handleElementAndReset(e1);
			}
		});
		testling_.handleDataRead(new SafeByteArray("<stream:stream to=\"example.com\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" ><presence/>"));
		testling_.handleDataRead(new SafeByteArray("<stream:stream to=\"example.com\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" ><presence/>"));

		assertEquals(2, elementsReceived_);
		assertEquals(0, errorReceived_);
	}

	@Test
	public void testWriteHeader() {
		ProtocolHeader header = new ProtocolHeader();
		header.setTo("example.com");
		testling_.writeHeader(header);

		assertEquals("<?xml version=\"1.0\"?><stream:stream xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" to=\"example.com\" version=\"1.0\">", lowLayer_.writtenData);
	}

	@Test
	public void testWriteElement() {
		testling_.writeElement(new Presence());

		assertEquals("<presence/>", lowLayer_.writtenData);
	}

	@Test
	public void testWriteFooter() {
		testling_.writeFooter();

		assertEquals("</stream:stream>", lowLayer_.writtenData);
	}
}