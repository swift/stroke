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
import com.isode.stroke.streamstack.StreamStack;
import com.isode.stroke.streamstack.LowLayer;
import com.isode.stroke.streamstack.XMPPLayer;
import com.isode.stroke.streamstack.StreamLayer;
import com.isode.stroke.parser.PlatformXMLParserFactory;
import com.isode.stroke.parser.payloadparsers.FullPayloadParserFactoryCollection;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.signals.Slot1;
import java.util.Vector;

public class StreamStackTest {

	private class MyStreamLayer extends StreamLayer {

		public MyStreamLayer(final String prepend) {
			this.prepend_ = prepend;
		}

		public void writeData(final SafeByteArray data) {
			writeDataToChildLayer(new SafeByteArray(prepend_).append(data));
		}

		public void handleDataRead(final SafeByteArray data) {
			writeDataToParentLayer(new SafeByteArray(prepend_).append(data));
		}

		private String prepend_ = "";
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

	private class TestLowLayer implements LowLayer {

		public Vector<SafeByteArray> data_ = new Vector<SafeByteArray>();

		public TestLowLayer() {

		}

		public void writeData(final SafeByteArray data) {
			data_.add(data);
		}

		public void onDataRead(SafeByteArray data) {
			writeDataToParentLayer(data);
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
	private TestLowLayer physicalStream_;
	private XMPPLayer xmppStream_;
	private int elementsReceived_;
	private int dataWriteReceived_;

	@Before
	public void setUp() {
		physicalStream_ = new TestLowLayer();
		xmppStream_ = new XMPPLayer(parserFactories_, serializers_, StreamType.ClientStreamType);
		elementsReceived_ = 0;
		dataWriteReceived_ = 0;
	}

	public void handleElement(Element element) {
		++elementsReceived_;
	}

	public void handleWriteData(final SafeByteArray SafeByteArray) {
		++dataWriteReceived_;
	}

	@Test
	public void testWriteData_NoIntermediateStreamStack() {
		StreamStack testling = new StreamStack(xmppStream_, physicalStream_);
			
		xmppStream_.writeData("foo");

		assertEquals(1, physicalStream_.data_.size());
		assertEquals(new SafeByteArray("foo"), physicalStream_.data_.get(0));
	}

	@Test
	public void testWriteData_OneIntermediateStream() {
		StreamStack testling = new StreamStack(xmppStream_, physicalStream_);
		MyStreamLayer xStream = new MyStreamLayer("X");
		testling.addLayer(xStream);

		xmppStream_.writeData("foo");

		assertEquals(1, physicalStream_.data_.size());
		assertEquals(new SafeByteArray("Xfoo"), physicalStream_.data_.get(0));
	}

	@Test
	public void testWriteData_TwoIntermediateStreamStack() {
		StreamStack testling = new StreamStack(xmppStream_, physicalStream_);
		MyStreamLayer xStream = new MyStreamLayer("X");
		MyStreamLayer yStream = new MyStreamLayer("Y");
		testling.addLayer(xStream);
		testling.addLayer(yStream);

		xmppStream_.writeData("foo");

		assertEquals(1, physicalStream_.data_.size());
		assertEquals(new SafeByteArray("XYfoo"), physicalStream_.data_.get(0));
	}

	@Test
	public void testReadData_NoIntermediateStreamStack() {
		StreamStack testling = new StreamStack(xmppStream_, physicalStream_);
		xmppStream_.onElement.connect(new Slot1<Element>() {
			@Override
			public void call(Element e1) {
				handleElement(e1);
			}
		});
		physicalStream_.onDataRead(new SafeByteArray("<stream:stream xmlns:stream='http://etherx.jabber.org/streams'><presence/>"));

		assertEquals(1, elementsReceived_);
	}

	@Test
	public void testReadData_OneIntermediateStream() {
		StreamStack testling = new StreamStack(xmppStream_, physicalStream_);
		xmppStream_.onElement.connect(new Slot1<Element>() {
			@Override
			public void call(Element e1) {
				handleElement(e1);
			}
		});
		MyStreamLayer xStream = new MyStreamLayer("<");
		testling.addLayer(xStream);

		physicalStream_.onDataRead(new SafeByteArray("stream:stream xmlns:stream='http://etherx.jabber.org/streams'><presence/>"));

		assertEquals(1, elementsReceived_);
	}

	@Test
	public void testReadData_TwoIntermediateStreamStack() {
		StreamStack testling = new StreamStack(xmppStream_, physicalStream_);
		xmppStream_.onElement.connect(new Slot1<Element>() {
			@Override
			public void call(Element e1) {
				handleElement(e1);
			}
		});
		MyStreamLayer xStream = new MyStreamLayer("s");
		MyStreamLayer yStream = new MyStreamLayer("<");
		testling.addLayer(xStream);
		testling.addLayer(yStream);

		physicalStream_.onDataRead(new SafeByteArray("tream:stream xmlns:stream='http://etherx.jabber.org/streams'><presence/>"));

		assertEquals(1, elementsReceived_);
	}

	@Test
	public void testAddLayer_ExistingOnWriteDataSlot() {
		StreamStack testling = new StreamStack(xmppStream_, physicalStream_);
		xmppStream_.onWriteData.connect(new Slot1<SafeByteArray>() {
			@Override
			public void call(SafeByteArray b1) {
				handleWriteData(b1);
			}
		});
		MyStreamLayer xStream = new MyStreamLayer("X");
		testling.addLayer(xStream);

		xmppStream_.writeData("foo");

		assertEquals(1, dataWriteReceived_);
	}
}