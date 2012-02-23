/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.Form.Type;
import com.isode.stroke.elements.FormField.BooleanFormField;
import com.isode.stroke.elements.FormField.FixedFormField;
import com.isode.stroke.elements.FormField.HiddenFormField;
import com.isode.stroke.elements.FormField.JIDMultiFormField;
import com.isode.stroke.elements.FormField.ListMultiFormField;
import com.isode.stroke.elements.FormField.ListSingleFormField;
import com.isode.stroke.elements.FormField.Option;
import com.isode.stroke.elements.FormField.TextMultiFormField;
import com.isode.stroke.elements.FormField.TextPrivateFormField;
import com.isode.stroke.elements.FormField.TextSingleFormField;
import com.isode.stroke.jid.JID;

public class FormSerializerTest {
    @BeforeClass
    public static void init() throws Exception {
    }

    @Test
    public void testSerializeFormInformation() {
        FormSerializer testling = new FormSerializer();
        Form form = new Form(Type.FORM_TYPE);
        form.setTitle("Bot Configuration");
        form
                .setInstructions("Hello!\nFill out this form to configure your new bot!");

        assertEquals(
                "<x type=\"form\" xmlns=\"jabber:x:data\">"
                        + "<title>Bot Configuration</title>"
                        + "<instructions>Hello!</instructions>"
                        + "<instructions>Fill out this form to configure your new bot!</instructions>"
                        + "</x>", testling.serialize(form));
    }

    @Test
    public void testSerializeFields() {
        FormSerializer testling = new FormSerializer();
        Form form = new Form(Type.FORM_TYPE);

        FormField field = HiddenFormField.create("jabber:bot");
        field.setName("FORM_TYPE");
        form.addField(field);

        form.addField(FixedFormField.create("Section 1: Bot Info"));

        field = TextSingleFormField.create();
        field.setName("botname");
        field.setLabel("The name of your bot");
        form.addField(field);

        field = TextMultiFormField
                .create("This is a bot.\nA quite good one actually");
        field.setName("description");
        field.setLabel("Helpful description of your bot");
        form.addField(field);

        field = BooleanFormField.create(true);
        field.setName("public");
        field.setLabel("Public bot?");
        field.setRequired(true);
        form.addField(field);

        field = TextPrivateFormField.create();
        field.setName("password");
        field.setLabel("Password for special access");
        form.addField(field);

        List<String> values = new ArrayList<String>();
        values.add("news");
        values.add("search");
        field = ListMultiFormField.create(values);
        field.setName("features");
        field.setLabel("What features will the bot support?");
        field.addOption(new Option("Contests", "contests"));
        field.addOption(new Option("News", "news"));
        field.addOption(new Option("Polls", "polls"));
        field.addOption(new Option("Reminders", "reminders"));
        field.addOption(new Option("Search", "search"));
        form.addField(field);

        field = ListSingleFormField.create("20");
        field.setName("maxsubs");
        field.setLabel("Maximum number of subscribers");
        field.addOption(new Option("10", "10"));
        field.addOption(new Option("20", "20"));
        field.addOption(new Option("30", "30"));
        field.addOption(new Option("50", "50"));
        field.addOption(new Option("100", "100"));
        field.addOption(new Option("", "none"));
        form.addField(field);

        List<JID> jids = new ArrayList<JID>();
        jids.add(new JID("foo@bar.com"));
        jids.add(new JID("baz@fum.org"));
        field = JIDMultiFormField.create(jids);
        field.setName("invitelist");
        field.setLabel("People to invite");
        field.setDescription("Tell all your friends about your new bot!");
        form.addField(field);

        assertEquals(
                "<x type=\"form\" xmlns=\"jabber:x:data\">"
                        + "<field type=\"hidden\" var=\"FORM_TYPE\">"
                        + "<value>jabber:bot</value>"
                        + "</field>"
                        + "<field type=\"fixed\"><value>Section 1: Bot Info</value></field>"
                        + "<field label=\"The name of your bot\" type=\"text-single\" var=\"botname\"><value></value></field>"
                        + "<field label=\"Helpful description of your bot\" type=\"text-multi\" var=\"description\"><value>This is a bot.</value><value>A quite good one actually</value></field>"
                        + "<field label=\"Public bot?\" type=\"boolean\" var=\"public\">"
                        + "<required/>"
                        + "<value>1</value>"
                        + "</field>"
                        + "<field label=\"Password for special access\" type=\"text-private\" var=\"password\"><value></value></field>"
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
                        + "<option><value>none</value></option>"
                        + "</field>"
                        + "<field label=\"People to invite\" type=\"jid-multi\" var=\"invitelist\">"
                        + "<desc>Tell all your friends about your new bot!</desc>"
                        + "<value>foo@bar.com</value>"
                        + "<value>baz@fum.org</value>" + "</field>" + "</x>",
                testling.serialize(form));
    }
}
