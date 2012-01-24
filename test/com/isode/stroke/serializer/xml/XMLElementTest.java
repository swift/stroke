/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.serializer.xml;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class XMLElementTest {

    @Test
    public void testSerialize() {
        XMLElement testling = new XMLElement("foo", "http://example.com");
        testling.setAttribute("myatt", "myval");
        XMLElement barElement = new XMLElement("bar");
        barElement.addNode(new XMLTextNode("Blo"));
        testling.addNode(barElement);
        XMLElement bazElement = new XMLElement("baz");
        bazElement.addNode(new XMLTextNode("Bli&</stream>"));
        testling.addNode(bazElement);

        String result = testling.serialize();
        String expectedResult =
                "<foo myatt=\"myval\" xmlns=\"http://example.com\">"
                + "<bar>Blo</bar>"
                + "<baz>Bli&amp;&lt;/stream&gt;</baz>"
                + "</foo>";

        assertEquals(expectedResult, result);
    }

    @Test
    public void testSerialize_NoChildren() {
        XMLElement testling = new XMLElement("foo", "http://example.com");

        assertEquals("<foo xmlns=\"http://example.com\"/>", testling.serialize());
    }

    @Test
    public void testSerialize_SpecialAttributeCharacters() {
        XMLElement testling = new XMLElement("foo");
        testling.setAttribute("myatt", "<\"'&>");

        assertEquals("<foo myatt=\"&lt;&quot;&apos;&amp;&gt;\"/>", testling.serialize());
    }

    @Test
    public void testSerialize_EmptyAttributeValue() {
        XMLElement testling = new XMLElement("foo");
        testling.setAttribute("myatt", "");

        assertEquals("<foo myatt=\"\"/>", testling.serialize());
    }
}


