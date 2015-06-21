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
import com.isode.stroke.serializer.payloadserializers.SecurityLabelSerializer;
import com.isode.stroke.elements.SecurityLabel;

public class SecurityLabelSerializerTest {

	/**
	* Default Constructor.
	*/
	public SecurityLabelSerializerTest() {

	}

	@Test
	public void testSerialize() {
		SecurityLabelSerializer testling = new SecurityLabelSerializer();
		SecurityLabel securityLabel = new SecurityLabel();
		securityLabel.setDisplayMarking("SECRET");
		securityLabel.setForegroundColor("black");
		securityLabel.setBackgroundColor("red");
		securityLabel.setLabel("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel>");
		securityLabel.addEquivalentLabel("<icismlabel xmlns=\"http://example.gov/IC-ISM/0\" classification=\"S\" ownerProducer=\"USA\" disseminationControls=\"FOUO\"/>");
		securityLabel.addEquivalentLabel("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MRUCAgD9DA9BcXVhIChvYnNvbGV0ZSk=</esssecuritylabel>");

		assertEquals(
			"<securitylabel xmlns=\"urn:xmpp:sec-label:0\">"
		+		"<displaymarking bgcolor=\"red\" fgcolor=\"black\">SECRET</displaymarking>"
		+		"<label>"
		+			"<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel>"
		+		"</label>"
		+		"<equivalentlabel>"
		+			"<icismlabel xmlns=\"http://example.gov/IC-ISM/0\" classification=\"S\" ownerProducer=\"USA\" disseminationControls=\"FOUO\"/>"
		+		"</equivalentlabel>"
		+		"<equivalentlabel>"
		+			"<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MRUCAgD9DA9BcXVhIChvYnNvbGV0ZSk=</esssecuritylabel>"
		+		"</equivalentlabel>"
		+	"</securitylabel>", testling.serialize(securityLabel));
	}

	@Test
	public void testSerialize_EmptyLabel() {
		SecurityLabelSerializer testling = new SecurityLabelSerializer();
		SecurityLabel securityLabel = new SecurityLabel();
		securityLabel.setDisplayMarking("SECRET");
		securityLabel.setLabel("");

		assertEquals(
			"<securitylabel xmlns=\"urn:xmpp:sec-label:0\">"
		+		"<displaymarking>SECRET</displaymarking>"
		+		"<label></label>"
		+	"</securitylabel>", testling.serialize(securityLabel));
	}
}