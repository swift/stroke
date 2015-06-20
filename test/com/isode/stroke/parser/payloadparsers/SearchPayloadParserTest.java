/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.isode.stroke.elements.SearchPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.FormItem;
import com.isode.stroke.elements.FormSection;
import com.isode.stroke.elements.FormText;
import com.isode.stroke.elements.FormPage;
import com.isode.stroke.elements.FormReportedRef;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class SearchPayloadParserTest {

	public SearchPayloadParserTest() {

	}

	@Test
	public void testParse_FormRequestResponse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
				"<query xmlns=\"jabber:iq:search\">" +
				"<instructions>Foo</instructions>" +
				"<first/>" +
				"<last/>" +
				"</query>"
			));

		SearchPayload payload = (SearchPayload)parser.getPayload();
		assertEquals("Foo", payload.getInstructions());
		assertNotNull(payload.getFirst());
		assertNotNull(payload.getLast());
		assertNull(payload.getNick());
		}

	@Test
	public void testParse_Results() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
				"<query xmlns=\"jabber:iq:search\">" +
				"<item jid=\"juliet@capulet.com\">" +
					"<first>Juliet</first>" +
					"<last>Capulet</last>" +
					"<nick>JuliC</nick>" +
					"<email>juliet@shakespeare.lit</email>" +
				"</item>" +
				"<item jid=\"tybalt@shakespeare.lit\">" +
					"<first>Tybalt</first>" +
					"<last>Capulet</last>" +
					"<nick>ty</nick>" +
					"<email>tybalt@shakespeare.lit</email>" +
				"</item>" +
				"</query>"
			));

		SearchPayload payload = (SearchPayload)parser.getPayload();
		assertEquals(2, payload.getItems().size());
		assertEquals(new JID("juliet@capulet.com"), payload.getItems().get(0).jid);
		assertEquals("Juliet", payload.getItems().get(0).first);
		assertEquals("Capulet", payload.getItems().get(0).last);
		assertEquals("JuliC", payload.getItems().get(0).nick);
		assertEquals("juliet@shakespeare.lit", payload.getItems().get(0).email);
		assertEquals(new JID("tybalt@shakespeare.lit"), payload.getItems().get(1).jid);
		}

	@Test
	public void testParse_FormRequestResponse_XDATA() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
					"<query xmlns='jabber:iq:search'>" +
						"<instructions>" +
						"Use the enclosed form to search. If your Jabber client does not" +
						" support Data Forms, visit http://shakespeare.lit/" +
						"</instructions>" +
						"<x xmlns='jabber:x:data' type='form'>" +
						"<title>User Directory Search</title>" +
						"<instructions>" +
							"Please provide the following information" +
							" to search for Shakespearean characters." +
						"</instructions>" +
						"<field type='hidden'" +
							" var='FORM_TYPE'>" +
							"<value>jabber:iq:search</value>" +
						"</field>" +
						"<field type='text-single'" +
						" label='Given Name'" +
						" var='first'/>" +
						"<field type='text-single'" +
						" label='Family Name'" +
						" var='last'/>" +
						"<field type='list-single'" +
						" label='Gender'" +
						" var='x-gender'>" +
							"<option label='Male'><value>male</value></option>" +
							"<option label='Female'><value>female</value></option>" +
						"</field>" +
						"</x>" +
					"</query>"
			));

		SearchPayload payload = (SearchPayload)parser.getPayload();
		assertEquals("Use the enclosed form to search. If your Jabber client does not" +
								 " support Data Forms, visit http://shakespeare.lit/", payload.getInstructions());
		assertNotNull(payload.getForm());
		assertEquals("Please provide the following information" +
								 " to search for Shakespearean characters.", payload.getForm().getInstructions());
		}

	@Test
	public void testParse_Results_XDATA() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse("<query xmlns='jabber:iq:search'>" +
							"	<x xmlns='jabber:x:data' type='result'>" +
							"		<field type='hidden' var='FORM_TYPE'>" +
							"		<value>jabber:iq:search</value>" +
							"		</field>" +
							"		<reported>" +
							"		<field var='first' label='Given Name' type='text-single'/>" +
							"		<field var='last' label='Family Name' type='text-single'/>" +
							"		<field var='jid' label='Jabber ID' type='jid-single'/>" +
							"		<field var='x-gender' label='Gender' type='list-single'/>" +
							"		</reported>" +
							"		<item>" +
							"		<field var='first'><value>Benvolio</value></field>" +
							"		<field var='last'><value>Montague</value></field>" +
							"		<field var='jid'><value>benvolio@montague.net</value></field>" +
							"		<field var='x-gender'><value>male</value></field>" +
							"		</item>" +
							"		<item>" +
							"		<field var='first'><value>Romeo</value></field>" +
							"		<field var='last'><value>Montague</value></field>" +
							"		<field var='jid'><value>romeo@montague.net</value></field>" +
							"		<field var='x-gender'><value>male</value></field>" +
							"		</item>" +
							"	</x>" +
							"</query>"));

		SearchPayload payload = (SearchPayload)parser.getPayload();
		assertNotNull(payload);

		Form dataForm = payload.getForm();
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
		assertEquals("benvolio@montague.net", item.getItemFields().get(2).getValues().get(0));
		assertEquals("jid", item.getItemFields().get(2).getName());
		assertEquals("male", item.getItemFields().get(3).getValues().get(0));
		assertEquals("x-gender", item.getItemFields().get(3).getName());

		item = items.get(1);
		assertEquals(4, item.getItemFields().size());

		assertEquals("Romeo", item.getItemFields().get(0).getValues().get(0));
		assertEquals("first", item.getItemFields().get(0).getName());
		assertEquals("Montague", item.getItemFields().get(1).getValues().get(0));
		assertEquals("last", item.getItemFields().get(1).getName());
		assertEquals("romeo@montague.net", item.getItemFields().get(2).getValues().get(0));
		assertEquals("jid", item.getItemFields().get(2).getName());
		assertEquals("male", item.getItemFields().get(3).getValues().get(0));
		assertEquals("x-gender", item.getItemFields().get(3).getName());
	}
}