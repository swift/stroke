/*
 * Copyright (c) 2010 Isode Limited.
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
import com.isode.stroke.serializer.payloadserializers.SecurityLabelsCatalogSerializer;
import com.isode.stroke.elements.SecurityLabelsCatalog;
import com.isode.stroke.elements.SecurityLabel;
import com.isode.stroke.jid.JID;

public class SecurityLabelsCatalogSerializerTest {

	/**
	* Default Constructor.
	*/
	public SecurityLabelsCatalogSerializerTest() {

	}

	@Test
	public void testSerialize() {
		SecurityLabelsCatalogSerializer testling = new SecurityLabelsCatalogSerializer();
		SecurityLabelsCatalog catalog = new SecurityLabelsCatalog();
		catalog.setTo(new JID("example.com"));
		catalog.setName("Default");
		catalog.setDescription("an example set of labels");

		SecurityLabelsCatalog.Item item1 = new SecurityLabelsCatalog.Item();
		SecurityLabel securityLabel1 = new SecurityLabel();
		item1.setLabel(securityLabel1);
		securityLabel1.setDisplayMarking("SECRET");
		securityLabel1.setForegroundColor("black");
		securityLabel1.setBackgroundColor("red");
		item1.setIsDefault(false);
		item1.setSelector("Classified|SECRET");
		securityLabel1.setLabel("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel>");
		catalog.addItem(item1);

		SecurityLabelsCatalog.Item item2 = new SecurityLabelsCatalog.Item();
		SecurityLabel securityLabel2 = new SecurityLabel();
		item2.setLabel(securityLabel2);
		securityLabel2.setDisplayMarking("CONFIDENTIAL");
		securityLabel2.setForegroundColor("black");
		securityLabel2.setBackgroundColor("navy");
		item2.setIsDefault(true);
		item2.setSelector("Classified|CONFIDENTIAL");
		securityLabel2.setLabel("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQMGASk=</esssecuritylabel>");
		catalog.addItem(item2);

		SecurityLabelsCatalog.Item item3 = new SecurityLabelsCatalog.Item();
		item3.setSelector("Unclassified|UNCLASSIFIED");
		catalog.addItem(item3);

		assertEquals(
			"<catalog desc=\"an example set of labels\" name=\"Default\" to=\"example.com\" xmlns=\"urn:xmpp:sec-label:catalog:2\">"
		+	 "<item selector=\"Classified|SECRET\">"
		+		"<securitylabel xmlns=\"urn:xmpp:sec-label:0\">"
		+			"<displaymarking bgcolor=\"red\" fgcolor=\"black\">SECRET</displaymarking>"
		+			"<label><esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel></label>"
		+		"</securitylabel>"
		+	 "</item>"
		+	 "<item default=\"true\" selector=\"Classified|CONFIDENTIAL\">"
		+		"<securitylabel xmlns=\"urn:xmpp:sec-label:0\">"
		+			"<displaymarking bgcolor=\"navy\" fgcolor=\"black\">CONFIDENTIAL</displaymarking>"
		+			"<label><esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQMGASk=</esssecuritylabel></label>"
		+		"</securitylabel>"
		+	 "</item>"
		+	 "<item selector=\"Unclassified|UNCLASSIFIED\"/>"
		+	"</catalog>", testling.serialize(catalog));
	}
}