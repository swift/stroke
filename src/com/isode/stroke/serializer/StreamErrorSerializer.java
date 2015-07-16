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
import com.isode.stroke.elements.StreamError;
import com.isode.stroke.elements.Element;
import com.isode.stroke.base.SafeByteArray;

public class StreamErrorSerializer extends GenericElementSerializer<StreamError> {

	public StreamErrorSerializer() {
		super(StreamError.class);
	}

	public SafeByteArray serialize(Element element) {
		StreamError error = (StreamError)element;
		XMLElement errorElement = new XMLElement("error", "http://etherx.jabber.org/streams");

		String typeTag = "";
		switch (error.getType()) {
			case BadFormat: typeTag = "bad-format"; break;
			case BadNamespacePrefix: typeTag = "bad-namespace-prefix"; break;
			case Conflict: typeTag = "conflict"; break;
			case ConnectionTimeout: typeTag = "connection-timeout"; break;
			case HostGone: typeTag = "host-gone"; break;
			case HostUnknown: typeTag = "host-unknown"; break;
			case ImproperAddressing: typeTag = "improper-addressing"; break;
			case InternalServerError: typeTag = "internal-server-error"; break;
			case InvalidFrom: typeTag = "invalid-from"; break;
			case InvalidID: typeTag = "invalid-id"; break;
			case InvalidNamespace: typeTag = "invalid-namespace"; break;
			case InvalidXML: typeTag = "invalid-xml"; break;
			case NotAuthorized: typeTag = "not-authorized"; break;
			case NotWellFormed: typeTag = "not-well-formed"; break;
			case PolicyViolation: typeTag = "policy-violation"; break;
			case RemoteConnectionFailed: typeTag = "remote-connection-failed"; break;
			case Reset: typeTag = "reset"; break;
			case ResourceConstraint: typeTag = "resource-constraint"; break;
			case RestrictedXML: typeTag = "restricted-xml"; break;
			case SeeOtherHost: typeTag = "see-other-host"; break;
			case SystemShutdown: typeTag = "system-shutdown"; break;
			case UndefinedCondition: typeTag = "undefined-condition"; break;
			case UnsupportedEncoding: typeTag = "unsupported-encoding"; break;
			case UnsupportedStanzaType: typeTag = "unsupported-stanza-type"; break;
			case UnsupportedVersion: typeTag = "unsupported-version"; break;
		}
		errorElement.addNode(new XMLElement(typeTag, "urn:ietf:params:xml:ns:xmpp-streams"));	

		if (!error.getText().isEmpty()) {
			errorElement.addNode(new XMLElement("text", "urn:ietf:params:xml:ns:xmpp-streams", error.getText()));
		}

		return new SafeByteArray(errorElement.serialize());
	}
}