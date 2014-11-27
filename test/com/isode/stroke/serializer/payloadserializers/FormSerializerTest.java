/*
 * Copyright (c) 2012-2014 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.Form.Type;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.FormField.Option;
import com.isode.stroke.elements.FormItem;

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

        FormField field = new FormField(FormField.Type.UNKNOWN_TYPE);
        field.setName("field name");
        field.setLabel("description");
        field.addValue("someText");
        form.addReportedField(field);
        
        field = new FormField(FormField.Type.TEXT_MULTI_TYPE);
        field.setLabel("text-multi-field");
        field.setTextMultiValue("This is some text\nthis is some more");
        form.addField(field);

        FormItem item = new FormItem();
        field = new FormField(FormField.Type.UNKNOWN_TYPE);
        field.setName("itemField");
        field.addValue("itemValue");
        item.addItemField(field);
        form.addItem(item);

        field = new FormField(FormField.Type.HIDDEN_TYPE, "jabber:bot");
        field.setName("FORM_TYPE");
        form.addField(field);
        
        form.addField(new FormField(FormField.Type.FIXED_TYPE, "Section 1: Bot Info"));

        field = new FormField(FormField.Type.TEXT_SINGLE_TYPE);
        field.setName("botname");
        field.setLabel("The name of your bot");
        form.addField(field);

        field = new FormField(FormField.Type.TEXT_MULTI_TYPE);
        field.setTextMultiValue("This is a bot.\nA quite good one actually");
        field.setName("description");
        field.setLabel("Helpful description of your bot");
        form.addField(field);

        field = new FormField(FormField.Type.BOOLEAN_TYPE);
        field.setBoolValue(true);
        field.setName("public");
        field.setLabel("Public bot?");
        field.setRequired(true);
        form.addField(field);

        field = new FormField(FormField.Type.TEXT_PRIVATE_TYPE);
        field.setName("password");
        field.setLabel("Password for special access");
        form.addField(field);

        field = new FormField(FormField.Type.LIST_MULTI_TYPE);
        field.addValue("news");
        field.addValue("search");
        field.setName("features");
        field.setLabel("What features will the bot support?");
        field.addOption(new Option("Contests", "contests"));
        field.addOption(new Option("News", "news"));
        field.addOption(new Option("Polls", "polls"));
        field.addOption(new Option("Reminders", "reminders"));
        field.addOption(new Option("Search", "search"));
        form.addField(field);

        field = new FormField(FormField.Type.LIST_SINGLE_TYPE, "20");
        field.setName("maxsubs");
        field.setLabel("Maximum number of subscribers");
        field.addOption(new Option("10", "10"));
        field.addOption(new Option("20", "20"));
        field.addOption(new Option("30", "30"));
        field.addOption(new Option("50", "50"));
        field.addOption(new Option("100", "100"));
        field.addOption(new Option("", "none"));
        form.addField(field);
        
        String jid = "user@example.com";
        field = new FormField(FormField.Type.JID_SINGLE_TYPE);
        field.addValue(jid);
        field.setName("jidSingle");
        field.setLabel("jidSingleLabel");
        field.setDescription("jidSingleDescription");
        form.addField(field);

        field = new FormField(FormField.Type.JID_MULTI_TYPE);
        field.addValue("foo@bar.com");
        field.addValue("baz@fum.org");
        field.setName("invitelist");
        field.setLabel("People to invite");
        field.setDescription("Tell all your friends about your new bot!");
        form.addField(field);

        assertEquals(
                "<x type=\"form\" xmlns=\"jabber:x:data\">"
                        + "<reported>"
                        + "<field label=\"description\" var=\"field name\">"
                        + "<value>someText</value>"
                        + "</field>"
                        + "</reported>"
                        + "<item>"
                        + "<field var=\"itemField\">"
                        + "<value>itemValue</value>"
                        + "</field>"
                        + "</item>"
                        + "<field label=\"text-multi-field\" type=\"text-multi\">"
                        + "<value>This is some text</value><value>this is some more</value>"
                        + "</field>"
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
                        + "<option><value>none</value></option>"
                        + "</field>"
                        + "<field label=\"jidSingleLabel\" type=\"jid-single\" var=\"jidSingle\">"
                        + "<desc>jidSingleDescription</desc>"
                        + "<value>user@example.com</value>"
                        + "</field>"
                        + "<field label=\"People to invite\" type=\"jid-multi\" var=\"invitelist\">"
                        + "<desc>Tell all your friends about your new bot!</desc>"
                        + "<value>foo@bar.com</value>"
                        + "<value>baz@fum.org</value>" + "</field>" + "</x>",
                        
                        /*
                        + "<field label=\"booleanField\" type=\"boolean\"><value>0</value></field>"
                        + "<field label=\"fixedField\" type=\"fixed\"><value>Fixed></value></field>"
                        + "<field label=\"hiddenField\" type=\"hidden\"/>"
                        + "<field label=\"listSingleField\" type=\"list-single\">"
                        	+ "<option label=\"option1\"><value>listVal</value></option>"
                        	+ "<option label=\"option2\"><value>listVal</value></option>"
                        	+ "<option label=\"option3\"><value>listVal</value></option>"
                        + "</field>"
                        + "<field label=\"listMultiField\" type=\"list-multi\">"
                        	+ "<option label=\"option1\"><value>listVal</value></option>"
                        	+ "<option label=\"option1\"><value>listVal</value></option>"
                        	+ "<option label=\"option1\"><value>listVal</value></option>"
                        + "</field>"
                        + "<field label=\"textPrivateField\" type=\"text-private\"><value>textPrivateVal</value></field>"
                        + "<field label=\"textMultiField\" type=\"text-multi\">"
                        	+ ""
                        	+ ""
                        	+ ""
                        + "</field>"
                        */
                testling.serialize(form));
    }
}
