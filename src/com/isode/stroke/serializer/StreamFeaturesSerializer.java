/*
 * Copyright (c) 2010-2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer;

import com.isode.stroke.serializer.GenericElementSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.elements.StreamFeatures;
import com.isode.stroke.elements.Element;
import com.isode.stroke.base.SafeByteArray;

public class StreamFeaturesSerializer extends GenericElementSerializer<StreamFeatures> {

	public StreamFeaturesSerializer() {
		super(StreamFeatures.class);
	}

	public SafeByteArray serialize(Element element) {
		StreamFeatures streamFeatures = (StreamFeatures)(element);

		XMLElement streamFeaturesElement = new XMLElement("stream:features");
		if (streamFeatures.hasStartTLS()) {
			streamFeaturesElement.addNode(new XMLElement("starttls", "urn:ietf:params:xml:ns:xmpp-tls"));
		}
		if (!streamFeatures.getCompressionMethods().isEmpty()) {
			XMLElement compressionElement = new XMLElement("compression", "http://jabber.org/features/compress");
			for(String method : streamFeatures.getCompressionMethods()) {
				XMLElement methodElement = new XMLElement("method");
				methodElement.addNode(new XMLTextNode(method));
				compressionElement.addNode(methodElement);
			}
			streamFeaturesElement.addNode(compressionElement);
		}
		if (!streamFeatures.getAuthenticationMechanisms().isEmpty()) {
			XMLElement mechanismsElement = new XMLElement("mechanisms", "urn:ietf:params:xml:ns:xmpp-sasl");
			for(String mechanism : streamFeatures.getAuthenticationMechanisms()) {
				XMLElement mechanismElement = new XMLElement("mechanism");
				mechanismElement.addNode(new XMLTextNode(mechanism));
				mechanismsElement.addNode(mechanismElement);
			}
			streamFeaturesElement.addNode(mechanismsElement);
		}
		if (streamFeatures.hasResourceBind()) {
			streamFeaturesElement.addNode(new XMLElement("bind", "urn:ietf:params:xml:ns:xmpp-bind"));
		}
		if (streamFeatures.hasSession()) {
			streamFeaturesElement.addNode(new XMLElement("session", "urn:ietf:params:xml:ns:xmpp-session"));
		}
		if (streamFeatures.hasStreamManagement()) {
			streamFeaturesElement.addNode(new XMLElement("sm", "urn:xmpp:sm:2"));
		}
		if (streamFeatures.hasRosterVersioning()) {
			streamFeaturesElement.addNode(new XMLElement("ver", "urn:xmpp:features:rosterver"));
		}
		return new SafeByteArray(streamFeaturesElement.serialize());
	}
}