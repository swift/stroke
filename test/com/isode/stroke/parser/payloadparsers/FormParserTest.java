/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Form.Type;
import com.isode.stroke.elements.FormField.BooleanFormField;
import com.isode.stroke.elements.FormField.FixedFormField;
import com.isode.stroke.elements.FormField.HiddenFormField;
import com.isode.stroke.elements.FormField.JIDMultiFormField;
import com.isode.stroke.elements.FormField.ListMultiFormField;
import com.isode.stroke.elements.FormField.ListSingleFormField;
import com.isode.stroke.elements.FormField.TextMultiFormField;
import com.isode.stroke.elements.FormField.TextSingleFormField;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

public class FormParserTest {
    @BeforeClass
    public static void init() throws Exception {
    }

    private static Form parse(String xmlString) {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(xmlString));

        Payload payload = null;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            eventLoop.processEvents();
            payload = parser.getPayload();
        } while (payload == null);

        return (Form) payload;
    }

    @Test
    public void testParse_FormInformation() throws Exception {
        Form payload = parse("<x type=\"submit\" xmlns=\"jabber:x:data\">"
                + "<title>Bot Configuration</title>"
                + "<instructions>Hello!</instructions>"
                + "<instructions>Fill out this form to configure your new bot!</instructions>"
                + "</x>");
        assertEquals("Bot Configuration", payload.getTitle());
        assertEquals("Hello!\nFill out this form to configure your new bot!",
                payload.getInstructions());
        assertEquals(Type.SUBMIT_TYPE, payload.getType());
    }

    @Test
    public void testParse() {
        Form payload = parse("<x type=\"form\" xmlns=\"jabber:x:data\">"
                + "<field type=\"hidden\" var=\"FORM_TYPE\">"
                + "<value>jabber:bot</value>"
                + "</field>"
                + "<field type=\"fixed\"><value>Section 1: Bot Info</value></field>"
                + "<field label=\"The name of your bot\" type=\"text-single\" var=\"botname\"/>"
                + "<field label=\"Helpful description of your bot\" type=\"text-multi\" var=\"description\"><value>This is a bot.</value><value>A quite good one actually</value></field>"
                + "<field label=\"Public bot?\" type=\"boolean\" var=\"public\">"
                + "<required/>"
                + "<value>1</value>"
                + "</field>"
                + "<field label=\"Password for special access\" type=\"text-private\" var=\"password\"/>"
                + "<field label=\"What features will the bot support?\" type=\"list-multi\" var=\"features\">"
                + "<value>news</value>"
                + "<value>search</value>"
                + "<option label=\"Contests\"><value>contests</value></option>"
                + "<option label=\"News\"><value>news</value></option>"
                + "<option label=\"Polls\"><value>polls</value></option>"
                + "<option label=\"Reminders\"><value>reminders</value></option>"
                + "<option label=\"Search\"><value>search</value></option>"
                + "</field>"
                + "<field label=\"Maximum number of subscribers\" type=\"list-single\" var=\"maxsubs\">"
                + "<value>20</value>"
                + "<option label=\"10\"><value>10</value></option>"
                + "<option label=\"20\"><value>20</value></option>"
                + "<option label=\"30\"><value>30</value></option>"
                + "<option label=\"50\"><value>50</value></option>"
                + "<option label=\"100\"><value>100</value></option>"
                + "<option label=\"None\"><value>none</value></option>"
                + "</field>"
                + "<field label=\"People to invite\" type=\"jid-multi\" var=\"invitelist\">"
                + "<desc>Tell all your friends about your new bot!</desc>"
                + "<value>foo@bar.com</value>" + "<value>baz@fum.org</value>"
                + "</field>" + "<field var=\"untyped\">" + "<value>foo</value>"
                + "</field>" + "</x>");

        assertEquals(10, payload.getFields().size());
        assertEquals("jabber:bot", ((HiddenFormField) (payload.getFields()
                .get(0))).getValue());
        assertEquals("FORM_TYPE", payload.getFields().get(0).getName());
        assertTrue(!payload.getFields().get(0).getRequired());

        assertEquals("Section 1: Bot Info", ((FixedFormField) (payload
                .getFields().get(1))).getValue());

        assertEquals("The name of your bot", payload.getFields().get(2)
                .getLabel());

        assertEquals("This is a bot.\nA quite good one actually",
                ((TextMultiFormField) (payload.getFields().get(3))).getValue());

        assertEquals(Boolean.TRUE, ((BooleanFormField) (payload.getFields()
                .get(4))).getValue());
        assertTrue(payload.getFields().get(4).getRequired());
        assertEquals("1", ((BooleanFormField) (payload.getFields().get(4)))
                .getRawValues().get(0));

        assertEquals("news",
                ((ListMultiFormField) (payload.getFields().get(6))).getValue()
                        .get(0));
        assertEquals("news", payload.getFields().get(6).getRawValues().get(0));
        assertEquals("search", ((ListMultiFormField) (payload.getFields()
                .get(6))).getValue().get(1));
        assertEquals("search", payload.getFields().get(6).getRawValues().get(1));
        assertEquals(5, payload.getFields().get(6).getOptions().size());
        assertEquals("Contests",
                payload.getFields().get(6).getOptions().get(0).label);
        assertEquals("contests",
                payload.getFields().get(6).getOptions().get(0).value);
        assertEquals("News",
                payload.getFields().get(6).getOptions().get(1).label);
        assertEquals("news",
                payload.getFields().get(6).getOptions().get(1).value);

        assertEquals("20", ((ListSingleFormField) (payload.getFields().get(7)))
                .getValue());

        assertEquals(new JID("foo@bar.com"), ((JIDMultiFormField) (payload
                .getFields().get(8))).getValue().get(0));
        assertEquals(new JID("baz@fum.org"), ((JIDMultiFormField) (payload
                .getFields().get(8))).getValue().get(1));
        assertEquals("Tell all your friends about your new bot!", payload
                .getFields().get(8).getDescription());

        assertEquals("foo",
                ((TextSingleFormField) (payload.getFields().get(9))).getValue());
    }
}