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

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.isode.stroke.elements.Command;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Command.Action;
import com.isode.stroke.elements.Command.Note;
import com.isode.stroke.elements.Command.Status;
import com.isode.stroke.elements.Command.Note.Type;
import com.isode.stroke.eventloop.DummyEventLoop;

public class CommandParserTest {
    private static DummyEventLoop eventLoop;

    @BeforeClass
    public static void init() throws Exception {
        eventLoop = new DummyEventLoop();
    }

    private static Command parse(String xmlString) {
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

        return (Command) payload;
    }

    @Test
    public void testParse() {
        Command payload = parse("<command xmlns='http://jabber.org/protocol/commands' node='list' action='prev' sessionid='myid'/>");

        assertEquals(Action.PREV, payload.getAction());
        assertEquals("list", payload.getNode());
        assertEquals("myid", payload.getSessionID());
    }

    @Test
    public void testParse_Result() {
        Command payload = parse("<command xmlns='http://jabber.org/protocol/commands' node='config' status='completed' sessionid='myid'>"
                + "<note type='warn'>Service 'httpd' has been configured.</note>"
                + "<note type='error'>I lied.</note>"
                + "<actions execute='next'>"
                + "<prev/>"
                + "<next/>"
                + "</actions>" + "</command>");

        assertEquals(Status.COMPLETED, payload.getStatus());

        List<Note> notes = payload.getNotes();
        assertEquals(2, notes.size());
        assertEquals(Type.WARN, notes.get(0).type);
        assertEquals("Service 'httpd' has been configured.", notes.get(0).note);
        assertEquals(Type.ERROR, notes.get(1).type);
        assertEquals("I lied.", notes.get(1).note);
        List<Action> actions = payload.getAvailableActions();
        assertEquals(2, actions.size());
        assertEquals(Action.PREV, actions.get(0));
        assertEquals(Action.NEXT, actions.get(1));
        assertEquals(Action.NEXT, payload.getExecuteAction());
    }

    @Test
    public void testParse_Form() {
        Command payload = parse("<command xmlns='http://jabber.org/protocol/commands' node='config' status='completed'>"
                + "<x type=\"result\" xmlns=\"jabber:x:data\">"
                + "<title>Bot Configuration</title>"
                + "<instructions>Hello!</instructions>"
                + "<instructions>Fill out this form to configure your new bot!</instructions>"
                + "</x>" + "</command>");

        Form form = payload.getForm();
        assertEquals("Bot Configuration", form.getTitle());
        assertEquals("Hello!\nFill out this form to configure your new bot!",
                form.getInstructions());
        assertEquals(Form.Type.RESULT_TYPE, form.getType());
    }
}
