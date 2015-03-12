package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import src.com.isode.stroke.elements.CapsInfo;
import src.com.isode.stroke.serializer.payloadserializers.CapsInfoSerializer;

public class CapsInfoSerializerTest {

	@Test
	public void testSerialize() {
		CapsInfoSerializer testling = new CapsInfoSerializer();
		CapsInfo priority = new CapsInfo("http://swift.im", "myversion", "sha-1");
		String expected = "<c hash=\"sha-1\" node=\"http://swift.im\" ver=\"myversion\" xmlns=\"http://jabber.org/protocol/caps\"/>";
		assertEquals(expected, testling.serialize(priority));
	}
}