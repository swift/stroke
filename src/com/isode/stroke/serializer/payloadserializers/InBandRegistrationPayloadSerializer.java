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

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.payloadserializers.FormSerializer;
import com.isode.stroke.elements.InBandRegistrationPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.base.NotNull;

public class InBandRegistrationPayloadSerializer extends GenericPayloadSerializer<InBandRegistrationPayload> {

	public InBandRegistrationPayloadSerializer() {
		super(InBandRegistrationPayload.class);
	}

	public String serializePayload(InBandRegistrationPayload registration) {
		XMLElement registerElement = new XMLElement("query", "jabber:iq:register");

		if (registration.isRegistered()) {
			registerElement.addNode(new XMLElement("registered"));
		}

		if (registration.isRemove()) {
			registerElement.addNode(new XMLElement("remove"));
		}

		if (registration.getInstructions() != null) {
			registerElement.addNode(new XMLElement("instructions", "", registration.getInstructions()));
		}


		if (registration.getUsername() != null) {
			registerElement.addNode(new XMLElement("username", "", registration.getUsername()));
		}

		if (registration.getNick() != null) {
			registerElement.addNode(new XMLElement("nick", "", registration.getNick()));
		}

		if (registration.getPassword() != null) {
			registerElement.addNode(new XMLElement("password", "", registration.getPassword()));
		}

		if (registration.getName() != null) {
			registerElement.addNode(new XMLElement("name", "", registration.getName()));
		}

		if (registration.getFirst() != null) {
			registerElement.addNode(new XMLElement("first", "", registration.getFirst()));
		}

		if (registration.getLast() != null) {
			registerElement.addNode(new XMLElement("last", "", registration.getLast()));
		}

		if (registration.getEMail() != null) {
			registerElement.addNode(new XMLElement("email", "", registration.getEMail()));
		}

		if (registration.getAddress() != null) {
			registerElement.addNode(new XMLElement("address", "", registration.getAddress()));
		}

		if (registration.getCity() != null) {
			registerElement.addNode(new XMLElement("city", "", registration.getCity()));
		}

		if (registration.getState() != null) {
			registerElement.addNode(new XMLElement("state", "", registration.getState()));
		}

		if (registration.getZip() != null) {
			registerElement.addNode(new XMLElement("zip", "", registration.getZip()));
		}

		if (registration.getPhone() != null) {
			registerElement.addNode(new XMLElement("phone", "", registration.getPhone()));
		}

		if (registration.getURL() != null) {
			registerElement.addNode(new XMLElement("url", "", registration.getURL()));
		}

		if (registration.getDate() != null) {
			registerElement.addNode(new XMLElement("date", "", registration.getDate()));
		}

		if (registration.getMisc() != null) {
			registerElement.addNode(new XMLElement("misc", "", registration.getMisc()));
		}

		if (registration.getText() != null) {
			registerElement.addNode(new XMLElement("text", "", registration.getText()));
		}

		if (registration.getKey() != null) {
			registerElement.addNode(new XMLElement("key", "", registration.getKey()));
		}

		if (registration.getForm() != null) {
			Form form = registration.getForm();
			registerElement.addNode(new XMLRawTextNode(new FormSerializer().serialize(form)));
		}

		return registerElement.serialize();	
	}
}