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
import com.isode.stroke.elements.FormSection;
import com.isode.stroke.elements.FormText;
import com.isode.stroke.elements.FormPage;
import com.isode.stroke.elements.FormReportedRef;
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
		Form form = new Form(Form.Type.FORM_TYPE);

		FormField field = new FormField(FormField.Type.HIDDEN_TYPE, "jabber:bot");
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

		field = new FormField(FormField.Type.BOOLEAN_TYPE, "1");
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
		field.addOption(new FormField.Option("Contests", "contests"));
		field.addOption(new FormField.Option("News", "news"));
		field.addOption(new FormField.Option("Polls", "polls"));
		field.addOption(new FormField.Option("Reminders", "reminders"));
		field.addOption(new FormField.Option("Search", "search"));
		form.addField(field);

		field = new FormField(FormField.Type.LIST_SINGLE_TYPE, "20");
		field.setName("maxsubs");
		field.setLabel("Maximum number of subscribers");
		field.addOption(new FormField.Option("10", "10"));
		field.addOption(new FormField.Option("20", "20"));
		field.addOption(new FormField.Option("30", "30"));
		field.addOption(new FormField.Option("50", "50"));
		field.addOption(new FormField.Option("100", "100"));
		field.addOption(new FormField.Option("", "none"));
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
				+		"<field type=\"hidden\" var=\"FORM_TYPE\">"
				+			"<value>jabber:bot</value>"
				+		"</field>"
				+		"<field type=\"fixed\"><value>Section 1: Bot Info</value></field>"
				+		"<field label=\"The name of your bot\" type=\"text-single\" var=\"botname\"/>"
				+		"<field label=\"Helpful description of your bot\" type=\"text-multi\" var=\"description\"><value>This is a bot.</value><value>A quite good one actually</value></field>"
				+		"<field label=\"Public bot?\" type=\"boolean\" var=\"public\">"
				+			"<required/>"
				+			"<value>1</value>"
				+		"</field>"
				+		"<field label=\"Password for special access\" type=\"text-private\" var=\"password\"/>"
				+		"<field label=\"What features will the bot support?\" type=\"list-multi\" var=\"features\">"
				+			"<value>news</value>"
				+			"<value>search</value>"
				+			"<option label=\"Contests\"><value>contests</value></option>"
				+			"<option label=\"News\"><value>news</value></option>"
				+			"<option label=\"Polls\"><value>polls</value></option>"
				+			"<option label=\"Reminders\"><value>reminders</value></option>"
				+			"<option label=\"Search\"><value>search</value></option>"
				+		"</field>"
				+		"<field label=\"Maximum number of subscribers\" type=\"list-single\" var=\"maxsubs\">"
				+			"<value>20</value>"
				+			"<option label=\"10\"><value>10</value></option>"
				+			"<option label=\"20\"><value>20</value></option>"
				+			"<option label=\"30\"><value>30</value></option>"
				+			"<option label=\"50\"><value>50</value></option>"
				+			"<option label=\"100\"><value>100</value></option>"
				+			"<option><value>none</value></option>"
				+		"</field>"
				+		"<field label=\"People to invite\" type=\"jid-multi\" var=\"invitelist\">"
				+			"<desc>Tell all your friends about your new bot!</desc>"
				+			"<value>foo@bar.com</value>"
				+			"<value>baz@fum.org</value>"
				+		"</field>"
				+	"</x>",
				testling.serialize(form));
	}

	@Test
	public void testSerializeLayout() {
		FormSerializer testling = new FormSerializer();
		Form form = new Form(Type.FORM_TYPE);

		FormPage page = new FormPage();
		page.setLabel("P1");
		FormReportedRef reportedRef = new FormReportedRef();
		page.addReportedRef(reportedRef);
		FormText formText = new FormText();
		formText.setTextString("P1T1");
		page.addTextElement(formText);
		FormField field = new FormField(FormField.Type.TEXT_SINGLE_TYPE);
		field.setName("P1F1");
		field.setLabel("field one");
		page.addField(field);

		FormSection section = new FormSection();
		section.setLabel("P1S1");
		formText = new FormText();
		formText.setTextString("P1S1T1");
		section.addTextElement(formText);
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE);
		field.setName("P1S1F1");
		field.setLabel("field two");
		section.addField(field);
		page.addChildSection(section);
		form.addPage(page);

		page = new FormPage();
		page.setLabel("P2");
		section = new FormSection();
		section.setLabel("P2S1");
		FormSection subSection = new FormSection();
		subSection.setLabel("P2S2");
		FormSection subSection2 = new FormSection();
		subSection2.setLabel("P2S3");
		subSection.addChildSection(subSection2);
		section.addChildSection(subSection);
		page.addChildSection(section);
		form.addPage(page);

		// P1 = page one, S1 = section one, F1 = field one, T1 = text one
		assertEquals(
				"<x type=\"form\" xmlns=\"jabber:x:data\">"
			+		"<page label=\"P1\" xmlns=\"http://jabber.org/protocol/xdata-layout\">"
			+			"<text>P1T1</text>"
			+			"<fieldref var=\"P1F1\"/>"
			+			"<reportedref/>"
			+			"<section label=\"P1S1\">"
			+				"<text>P1S1T1</text>"
			+				"<fieldref var=\"P1S1F1\"/>"
			+			"</section>"
			+		"</page>"
			+		"<page label=\"P2\" xmlns=\"http://jabber.org/protocol/xdata-layout\">"
			+			"<section label=\"P2S1\">"
			+				"<section label=\"P2S2\">"
			+					"<section label=\"P2S3\"/>"
			+				"</section>"
			+			"</section>"
			+		"</page>"
			+		"<field label=\"field one\" type=\"text-single\" var=\"P1F1\"/>"
			+		"<field label=\"field two\" type=\"text-single\" var=\"P1S1F1\"/>"
			+	"</x>", testling.serialize(form));
		}

	@Test
	public void testSerializeFormItems() {
		FormSerializer testling = new FormSerializer();
		Form form = new Form(Type.RESULT_TYPE);

		FormField field = new FormField(FormField.Type.HIDDEN_TYPE, "jabber:iq:search");
		field.setName("FORM_TYPE");
		form.addField(field);

		// reported fields
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE);
		field.setName("first");
		field.setLabel("Given Name");
		form.addReportedField(field);

		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE);
		field.setName("last");
		field.setLabel("Family Name");
		form.addReportedField(field);

		field = new FormField(FormField.Type.JID_SINGLE_TYPE);
		field.setName("jid");
		field.setLabel("Jabber ID");
		form.addReportedField(field);

		field = new FormField(FormField.Type.LIST_SINGLE_TYPE);
		field.setName("x-gender");
		field.setLabel("Gender");
		form.addReportedField(field);

		FormItem firstItem = new FormItem();
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "Benvolio");
		field.setName("first");
		firstItem.addItemField(field);

		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "Montague");
		field.setName("last");
		firstItem.addItemField(field);

		field = new FormField(FormField.Type.JID_SINGLE_TYPE, "benvolio@montague.net");
		field.setName("jid");
		firstItem.addItemField(field);

		field = new FormField(FormField.Type.LIST_SINGLE_TYPE, "male");
		field.setName("x-gender");
		firstItem.addItemField(field);

		FormItem secondItem = new FormItem();
		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "Romeo");
		field.setName("first");
		secondItem.addItemField(field);

		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "Montague");
		field.setName("last");
		secondItem.addItemField(field);

		field = new FormField(FormField.Type.JID_SINGLE_TYPE, "romeo@montague.net");
		field.setName("jid");
		secondItem.addItemField(field);

		field = new FormField(FormField.Type.LIST_SINGLE_TYPE, "male");
		field.setName("x-gender");
		secondItem.addItemField(field);

		form.addItem(firstItem);
		form.addItem(secondItem);

		assertEquals(
				"<x type=\"result\" xmlns=\"jabber:x:data\">"
			+		"<field type=\"hidden\" var=\"FORM_TYPE\">"
			+			"<value>jabber:iq:search</value>"
			+		"</field>"
			+		"<reported>"
			+			"<field label=\"Given Name\" type=\"text-single\" var=\"first\"/>"
			+			"<field label=\"Family Name\" type=\"text-single\" var=\"last\"/>"
			+			"<field label=\"Jabber ID\" type=\"jid-single\" var=\"jid\"/>"
			+			"<field label=\"Gender\" type=\"list-single\" var=\"x-gender\"/>"
			+		"</reported>"
			+		"<item>"
			+			"<field var=\"first\"><value>Benvolio</value></field>"
			+			"<field var=\"last\"><value>Montague</value></field>"
			+			"<field var=\"jid\"><value>benvolio@montague.net</value></field>"
			+			"<field var=\"x-gender\"><value>male</value></field>"
			+		"</item>"
			+		"<item>"
			+			"<field var=\"first\"><value>Romeo</value></field>"
			+			"<field var=\"last\"><value>Montague</value></field>"
			+			"<field var=\"jid\"><value>romeo@montague.net</value></field>"
			+			"<field var=\"x-gender\"><value>male</value></field>"
			+		"</item>"
			+	"</x>", testling.serialize(form));
	}    
}
