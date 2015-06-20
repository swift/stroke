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

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.SearchPayloadSerializer;
import com.isode.stroke.elements.SearchPayload;
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
import java.util.List;

public class SearchPayloadSerializerTest {

	/**
	* Default Constructor.
	*/
	public SearchPayloadSerializerTest() {

	}

	@Test
	public void testSerialize_Request() {
		SearchPayloadSerializer testling = new SearchPayloadSerializer();

		SearchPayload payload = new SearchPayload();
		payload.setFirst("Juliet");
		payload.setLast("Capulet");

		assertEquals("<query xmlns=\"jabber:iq:search\">" +
					"<first>Juliet</first>" +
					"<last>Capulet</last>" +
				"</query>", testling.serialize(payload));
	}

	@Test
	public void testSerialize_Items() {
		SearchPayloadSerializer testling = new SearchPayloadSerializer();

		SearchPayload payload = new SearchPayload();
		SearchPayload.Item item1 = new SearchPayload.Item();
		item1.jid = new JID("juliet@capulet.com");
		item1.first = "Juliet";
		item1.last = "Capulet";
		item1.nick = "JuliC";
		item1.email = "juliet@shakespeare.lit";
		payload.addItem(item1);

		SearchPayload.Item item2 = new SearchPayload.Item();
		item2.jid = new JID("tybalt@shakespeare.lit");
		item2.first = "Tybalt";
		item2.last = "Capulet";
		item2.nick = "ty";
		item2.email = "tybalt@shakespeare.lit";
		payload.addItem(item2);

		assertEquals("<query xmlns=\"jabber:iq:search\">" +
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
				"</query>", testling.serialize(payload));
	}

	@Test
	public void testSerialize_DataForm() {
		SearchPayloadSerializer testling = new SearchPayloadSerializer();

		SearchPayload payload = new SearchPayload();
		Form form = new Form(Form.Type.RESULT_TYPE);

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

		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "benvolio@montague.net");
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

		field = new FormField(FormField.Type.TEXT_SINGLE_TYPE, "romeo@montague.net");
		field.setName("jid");
		secondItem.addItemField(field);

		field = new FormField(FormField.Type.LIST_SINGLE_TYPE, "male");
		field.setName("x-gender");
		secondItem.addItemField(field);


		form.addItem(firstItem);
		form.addItem(secondItem);

		payload.setForm(form);
			assertEquals("<query xmlns=\"jabber:iq:search\">"
			+		"<x type=\"result\" xmlns=\"jabber:x:data\">"
			+			"<field type=\"hidden\" var=\"FORM_TYPE\">"
			+				"<value>jabber:iq:search</value>"
			+			"</field>"
			+			"<reported>"
			+				"<field label=\"Given Name\" type=\"text-single\" var=\"first\"/>"
			+				"<field label=\"Family Name\" type=\"text-single\" var=\"last\"/>"
			+				"<field label=\"Jabber ID\" type=\"jid-single\" var=\"jid\"/>"
			+				"<field label=\"Gender\" type=\"list-single\" var=\"x-gender\"/>"
			+			"</reported>"
			+			"<item>"
			+				"<field var=\"first\"><value>Benvolio</value></field>"
			+				"<field var=\"last\"><value>Montague</value></field>"
			+				"<field var=\"jid\"><value>benvolio@montague.net</value></field>"
			+				"<field var=\"x-gender\"><value>male</value></field>"
			+			"</item>"
			+			"<item>"
			+				"<field var=\"first\"><value>Romeo</value></field>"
			+				"<field var=\"last\"><value>Montague</value></field>"
			+				"<field var=\"jid\"><value>romeo@montague.net</value></field>"
			+				"<field var=\"x-gender\"><value>male</value></field>"
			+			"</item>"
			+		"</x>"
			+	"</query>", testling.serialize(payload));
	}
}