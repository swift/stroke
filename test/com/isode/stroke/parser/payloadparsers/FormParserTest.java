/*
 * Copyright (c) 2012-2014 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
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
import com.isode.stroke.elements.Payload;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import java.util.List;
import java.util.ArrayList;

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
				+ "<reported>"
				+ "<field var=\"field name\" label=\"description\" type=\"unknown\">"
				+ "<value>someText</value>"
				+ "</field>"
				+ "</reported>"
				+ "<item>"
				+ "<field label=\"itemField\">"
				+ "<value>itemValue</value>"
				+ "</field>"
				+ "</item>"
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
				+ "</field>"
				+ "</x>");
		

		
		assertEquals(10, payload.getFields().size());
		
		assertEquals("jabber:bot", payload.getFields().get(0).getValues().get(0));
		
		assertEquals("FORM_TYPE", payload.getFields().get(0).getName());
		assertTrue(!payload.getFields().get(0).getRequired());
		
		assertEquals("description", payload.getReportedFields().get(0).getLabel());
		assertEquals("someText", payload.getReportedFields().get(0).getValues().get(0));
		assertEquals("itemField", payload.getItems().get(0).getItemFields().get(0).getLabel());
		assertEquals("itemValue", payload.getItems().get(0).getItemFields().get(0).getValues().get(0));

		assertEquals("Section 1: Bot Info", payload.getFields().get(1).getValues().get(0));

		assertEquals("The name of your bot", payload.getFields().get(2)
				.getLabel());

		assertEquals("This is a bot.\nA quite good one actually",
				payload.getFields().get(3).getTextMultiValue());

		assertEquals(Boolean.TRUE, payload.getFields()
				.get(4).getBoolValue());
		assertTrue(payload.getFields().get(4).getRequired());
		assertEquals("1", payload.getFields().get(4).getValues().get(0));

		assertEquals("news", payload.getFields().get(6).getValues().get(0));
		assertEquals("search", payload.getFields().get(6).getValues().get(1));
		assertEquals(5, payload.getFields().get(6).getOptions().size());
		assertEquals("Contests",
				payload.getFields().get(6).getOptions().get(0).label_);
		assertEquals("contests",
				payload.getFields().get(6).getOptions().get(0).value_);
		assertEquals("News",
				payload.getFields().get(6).getOptions().get(1).label_);
		assertEquals("news",
				payload.getFields().get(6).getOptions().get(1).value_);

		assertEquals("20", payload.getFields().get(7).getValues().get(0));

		assertEquals(new JID("foo@bar.com"), payload
				.getFields().get(8).getJIDMultiValue(0));
		assertEquals(new JID("baz@fum.org"), payload
				.getFields().get(8).getJIDMultiValue(1));
		assertEquals("Tell all your friends about your new bot!", payload
				.getFields().get(8).getDescription());

		assertEquals("foo", payload.getFields().get(9).getValues().get(0));
	}

	@Test
	public void testParse_FormLayout() {

		// P1 = page one, S1 = section one, F1 = field one, T1 = text one
		Form payload = parse(
			"<x type=\"form\" xmlns=\"jabber:x:data\">" +
				"<page label=\"P1\" xmlns=\"http://jabber.org/protocol/xdata-layout\">" +
					"<reportedref/>" +
					"<text>P1T1</text>" +
					"<fieldref var=\"P1F1\"/>" +
					"<section label=\"P1S1\">" +
						"<text>P1S1T1</text>" +
						"<fieldref var=\"P1S1F1\"/>" +
					"</section>" +
				"</page>" +
				"<page label=\"P2\" xmlns=\"http://jabber.org/protocol/xdata-layout\">" +
					"<section label=\"P2S1\">" +
						"<section label=\"P2S2\">" +
							"<section label=\"P2S3\"/>" +
						"</section>" +
					"</section>" +
				"</page>" +
				"<field label=\"field one\" type=\"text-single\" var=\"P1F1\"/>" +
				"<field label=\"field two\" type=\"text-single\" var=\"P1S1F1\"/>" +
			"</x>");

		assertEquals(0, payload.getFields().size());
		// PAGE ONE - parsing of element types
		assertEquals("P1", payload.getPages().get(0).getLabel());
		assertNotNull(payload.getPages().get(0).getReportedRefs().get(0));
		assertEquals("P1T1", payload.getPages().get(0).getTextElements().get(0).getTextString());
		assertEquals("P1F1", payload.getPages().get(0).getFields().get(0).getName());
		assertEquals("P1S1", payload.getPages().get(0).getChildSections().get(0).getLabel());
		assertEquals("P1S1T1", payload.getPages().get(0).getChildSections().get(0).getTextElements().get(0).getTextString());
		assertEquals("P1S1F1", payload.getPages().get(0).getChildSections().get(0).getFields().get(0).getName());
		// PAGE TWO - parsing of nested elements
		assertEquals("P2", payload.getPages().get(1).getLabel());
		assertEquals("P2S1", payload.getPages().get(1).getChildSections().get(0).getLabel());
		assertEquals("P2S2", payload.getPages().get(1).getChildSections().get(0).getChildSections().get(0).getLabel());
		assertEquals("P2S3", payload.getPages().get(1).getChildSections().get(0).getChildSections().get(0).getChildSections().get(0).getLabel());
	}

	@Test
	public void testParse_FormItems() {

		Form dataForm = parse(
			"<x xmlns='jabber:x:data' type='result'>" +
				"<field type='hidden' var='FORM_TYPE'>" +
					"<value>jabber:iq:search</value>" +
				"</field>" +
				"<reported>" +
					"<field var='first' label='Given Name' type='text-single'/>" +
					"<field var='last' label='Family Name' type='text-single'/>" +
					"<field var='jid' label='Jabber ID' type='jid-single'/>" +
					"<field var='x-gender' label='Gender' type='list-single'/>" +
				"</reported>" +
				"<item>" +
					"<field var='first'><value>Benvolio</value></field>" +
					"<field var='last'><value>Montague</value></field>" +
					"<field var='jid'><value>benvolio@montague.net</value></field>" +
					"<field var='x-gender'><value>male</value></field>" +
				"</item>" +
				"<item>" +
					"<field var='first'><value>Romeo</value></field>" +
					"<field var='last'><value>Montague</value></field>" +
					"<field var='jid'><value>romeo@montague.net</value></field>" +
					"<field var='x-gender'><value>male</value></field>" +
				"</item>" +
			"</x>");

		assertNotNull(dataForm);

		List<FormField> reported = dataForm.getReportedFields();
		assertEquals(4, reported.size());

		List<FormItem> items = dataForm.getItems();
		assertEquals(2, items.size());

		FormItem item = items.get(0);
		assertEquals(4, item.getItemFields().size());

		assertEquals("Benvolio", item.getItemFields().get(0).getValues().get(0));
		assertEquals("first", item.getItemFields().get(0).getName());
		assertEquals("Montague", item.getItemFields().get(1).getValues().get(0));
		assertEquals("last", item.getItemFields().get(1).getName());
		FormField jidField = item.getItemFields().get(2);
		assertEquals(new JID("benvolio@montague.net"), jidField.getJIDSingleValue());
		assertEquals("jid", item.getItemFields().get(2).getName());
		assertEquals("male", item.getItemFields().get(3).getValues().get(0));
		assertEquals("x-gender", item.getItemFields().get(3).getName());

		item = items.get(1);
		assertEquals(4, item.getItemFields().size());

		assertEquals("Romeo", item.getItemFields().get(0).getValues().get(0));
		assertEquals("first", item.getItemFields().get(0).getName());
		assertEquals("Montague", item.getItemFields().get(1).getValues().get(0));
		assertEquals("last", item.getItemFields().get(1).getName());
		jidField = item.getItemFields().get(2);
		assertNotNull(jidField);
		assertEquals(new JID("romeo@montague.net"), jidField.getJIDSingleValue());
		assertEquals("jid", item.getItemFields().get(2).getName());
		assertEquals("male", item.getItemFields().get(3).getValues().get(0));
		assertEquals("x-gender", item.getItemFields().get(3).getName());
	}
}