/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.eventloop.SimpleEventLoop;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class XMLParserTest {

    @Before
    public void setUp() {
        client_ = new Client();
    }

    private XMLParser parser() {
        return PlatformXMLParserFactory.createXMLParser(client_);
    }

    private void join(XMLParser parser) {
       
    }

    @Test
    public void testParse_characters() {
        XMLParser testling = parser();

        String data = "ABCZ\u0041\u00DF\u00F7\u0410\u0498";
        assertTrue(testling.parse("<body>" + data + "</body>"));
        join(testling);
        assertEquals(3, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("body", client_.events.get(0).data);

        assertEquals(Client.Type.CharacterData, client_.events.get(1).type);
        assertEquals(data, client_.events.get(1).data);

        assertEquals(Client.Type.EndElement, client_.events.get(2).type);
        assertEquals("body", client_.events.get(2).data);

   }

    @Test
    public void testParse_NestedElements() {
        XMLParser testling = parser();

        assertTrue(testling.parse(
                "<iq type=\"get\">"
                + "<query xmlns='jabber:iq:version'/>"
                + "</iq>"));

        join(testling);

        assertEquals(4, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("iq", client_.events.get(0).data);
        assertEquals(1, client_.events.get(0).attributes.getEntries().size());
        assertEquals("get", client_.events.get(0).attributes.getAttribute("type"));
        assertEquals("", client_.events.get(0).ns);

        assertEquals(Client.Type.StartElement, client_.events.get(1).type);
        assertEquals("query", client_.events.get(1).data);
        assertEquals(0, client_.events.get(1).attributes.getEntries().size());
        assertEquals("jabber:iq:version", client_.events.get(1).ns);

        assertEquals(Client.Type.EndElement, client_.events.get(2).type);
        assertEquals("query", client_.events.get(2).data);
        assertEquals("jabber:iq:version", client_.events.get(2).ns);

        assertEquals(Client.Type.EndElement, client_.events.get(3).type);
        assertEquals("iq", client_.events.get(3).data);
        assertEquals("", client_.events.get(3).ns);
    }

    @Test
    public void testParse_ElementInNamespacedElement() {
        XMLParser testling = parser();

        assertTrue(testling.parse(
                "<query xmlns='jabber:iq:version'>"
                + "<name>Swift</name>"
                + "</query>"));
        join(testling);
        assertEquals(5, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("query", client_.events.get(0).data);
        assertEquals(0, client_.events.get(0).attributes.getEntries().size());
        assertEquals("jabber:iq:version", client_.events.get(0).ns);

        assertEquals(Client.Type.StartElement, client_.events.get(1).type);
        assertEquals("name", client_.events.get(1).data);
        assertEquals("jabber:iq:version", client_.events.get(1).ns);

        assertEquals(Client.Type.CharacterData, client_.events.get(2).type);
        assertEquals("Swift", client_.events.get(2).data);

        assertEquals(Client.Type.EndElement, client_.events.get(3).type);
        assertEquals("name", client_.events.get(3).data);
        assertEquals("jabber:iq:version", client_.events.get(3).ns);

        assertEquals(Client.Type.EndElement, client_.events.get(4).type);
        assertEquals("query", client_.events.get(4).data);
        assertEquals("jabber:iq:version", client_.events.get(4).ns);
    }

    @Test
    public void testParse_CharacterData() {
        XMLParser testling = parser();

        assertTrue(testling.parse("<html>bla<i>bli</i>blo</html>"));
        join(testling);
        assertEquals(7, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("html", client_.events.get(0).data);

        assertEquals(Client.Type.CharacterData, client_.events.get(1).type);
        assertEquals("bla", client_.events.get(1).data);

        assertEquals(Client.Type.StartElement, client_.events.get(2).type);
        assertEquals("i", client_.events.get(2).data);

        assertEquals(Client.Type.CharacterData, client_.events.get(3).type);
        assertEquals("bli", client_.events.get(3).data);

        assertEquals(Client.Type.EndElement, client_.events.get(4).type);
        assertEquals("i", client_.events.get(4).data);

        assertEquals(Client.Type.CharacterData, client_.events.get(5).type);
        assertEquals("blo", client_.events.get(5).data);

        assertEquals(Client.Type.EndElement, client_.events.get(6).type);
        assertEquals("html", client_.events.get(6).data);
    }

    @Test
    public void testParse_NamespacePrefix() {
        XMLParser testling = parser();

        assertTrue(testling.parse("<p:x xmlns:p='bla'><p:y/></p:x>"));
        join(testling);
        assertEquals(4, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("x", client_.events.get(0).data);
        assertEquals("bla", client_.events.get(0).ns);

        assertEquals(Client.Type.StartElement, client_.events.get(1).type);
        assertEquals("y", client_.events.get(1).data);
        assertEquals("bla", client_.events.get(1).ns);

        assertEquals(Client.Type.EndElement, client_.events.get(2).type);
        assertEquals("y", client_.events.get(2).data);
        assertEquals("bla", client_.events.get(2).ns);

        assertEquals(Client.Type.EndElement, client_.events.get(3).type);
        assertEquals("x", client_.events.get(3).data);
        assertEquals("bla", client_.events.get(3).ns);
    }

    @Test
    public void testParse_UnhandledXML() {
        XMLParser testling = parser();

        assertTrue(testling.parse("<iq><!-- Testing --></iq>"));
        join(testling);
        assertEquals(2, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("iq", client_.events.get(0).data);

        assertEquals(Client.Type.EndElement, client_.events.get(1).type);
        assertEquals("iq", client_.events.get(1).data);
    }

    //@Test /*TODO: uncomment if we ever get a sane incremental parser */
    public void testParse_InvalidXML() {
        XMLParser testling = parser();

        assertTrue(!testling.parse("<iq><bla></iq>"));
    }

    //@Test /*TODO: uncomment if we ever get a sane incremental parser */
    public void testParse_InErrorState() {
        XMLParser testling = parser();

        assertTrue(!testling.parse("<iq><bla></iq>"));
        assertTrue(!testling.parse("<iq/>"));
    }

    @Test
    public void testParse_Incremental() {
        XMLParser testling = parser();

        assertTrue(testling.parse("<iq"));
        assertTrue(testling.parse("></iq>"));
        join(testling);
        assertEquals(2, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("iq", client_.events.get(0).data);

        assertEquals(Client.Type.EndElement, client_.events.get(1).type);
        assertEquals("iq", client_.events.get(1).data);
    }

    @Test
    public void testParse_IncrementalWithCloses() {
        XMLParser testling = parser();

        assertTrue(testling.parse("<iq"));
        assertTrue(testling.parse(">&lt;></iq>"));
        join(testling);
        assertEquals(3, client_.events.size());

        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("iq", client_.events.get(0).data);

        assertEquals(Client.Type.CharacterData, client_.events.get(1).type);
        assertEquals("<>", client_.events.get(1).data);

        assertEquals(Client.Type.EndElement, client_.events.get(2).type);
        assertEquals("iq", client_.events.get(2).data);
    }

    @Test
    public void testParse_WhitespaceInAttribute() {
        XMLParser testling = parser();

        assertTrue(testling.parse(
                "<query xmlns='http://www.xmpp.org/extensions/xep-0084.html#ns-data '>"));
        assertTrue(testling.parse(
                "<presence/>"));
        join(testling);
        assertEquals(3, client_.events.size());
        assertEquals(Client.Type.StartElement, client_.events.get(0).type);
        assertEquals("query", client_.events.get(0).data);
        assertEquals(Client.Type.StartElement, client_.events.get(1).type);
        assertEquals("presence", client_.events.get(1).data);
        assertEquals(Client.Type.EndElement, client_.events.get(2).type);
        assertEquals("presence", client_.events.get(2).data);
    }

    private static class Client implements XMLParserClient {

        public enum Type {

            StartElement, EndElement, CharacterData
        };

        private class Event {

            Event(
                    Type type,
                    String data,
                    String ns,
                    AttributeMap attributes) {
                this.type = type;
                this.data = data;
                this.ns = ns;
                this.attributes = attributes;
            }

            Event(Type type, String data, String ns) {
                this.type = type;
                this.data = data;
                this.ns = ns;
            }

            Event(Type type, String data) {
                this(type, data, "");
            }
            Type type;
            String data;
            String ns;
            AttributeMap attributes;
        };

        Client() {
        }

        public void handleStartElement(String element, String ns, AttributeMap attributes) {
            events.add(new Event(Type.StartElement, element, ns, attributes));
        }

        public void handleEndElement(String element, String ns) {
            events.add(new Event(Type.EndElement, element, ns));
        }

        public void handleCharacterData(String data) {
            events.add(new Event(Type.CharacterData, data));
        }
        List<Event> events = new ArrayList<Event>();
    };
    private Client client_;
}
