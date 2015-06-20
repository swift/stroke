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

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.StreamInitiation;
import com.isode.stroke.elements.StreamInitiationFileInfo;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.base.NotNull;

public class StreamInitiationSerializer extends GenericPayloadSerializer<StreamInitiation> {

	private static final String FILE_TRANSFER_NS  = "http://jabber.org/protocol/si/profile/file-transfer";
	private static final String FEATURE_NEG_NS = "http://jabber.org/protocol/feature-neg";

	public StreamInitiationSerializer() {
		super(StreamInitiation.class);
	}

	public String serializePayload(StreamInitiation streamInitiation) {
		assert(streamInitiation.getIsFileTransfer() == true);

		XMLElement siElement = new XMLElement("si", "http://jabber.org/protocol/si");
		if (streamInitiation.getID().length() != 0) {
			siElement.setAttribute("id", streamInitiation.getID());
		}
		siElement.setAttribute("profile", FILE_TRANSFER_NS);

		if (streamInitiation.getFileInfo() != null) {
			StreamInitiationFileInfo file = streamInitiation.getFileInfo();
			XMLElement fileElement = new XMLElement("file", "http://jabber.org/protocol/si/profile/file-transfer");
			fileElement.setAttribute("name", file.getName());
			if (file.getSize() != 0) {
				fileElement.setAttribute("size", Long.toString(file.getSize()));
			}
			if (file.getDescription().length() != 0) {
				XMLElement descElement = new XMLElement("desc");
				descElement.addNode(new XMLTextNode(file.getDescription()));
				fileElement.addNode(descElement);
			}
			siElement.addNode(fileElement);
		}

		XMLElement featureElement = new XMLElement("feature", FEATURE_NEG_NS);
		if (streamInitiation.getProvidedMethods().size() > 0) {
			Form form = new Form(Form.Type.FORM_TYPE);
			FormField field = new FormField(FormField.Type.LIST_SINGLE_TYPE);
			field.setName("stream-method");
			for(String method : streamInitiation.getProvidedMethods()) {
				field.addOption(new FormField.Option("", method));
			}
			form.addField(field);
			featureElement.addNode(new XMLRawTextNode(new FormSerializer().serialize(form)));
		}
		else if (streamInitiation.getRequestedMethod().length() != 0) {
			Form form = new Form(Form.Type.SUBMIT_TYPE);
			FormField field = new FormField(FormField.Type.LIST_SINGLE_TYPE);
			field.addValue(streamInitiation.getRequestedMethod());
			field.setName("stream-method");
			form.addField(field);
			featureElement.addNode(new XMLRawTextNode(new FormSerializer().serialize(form)));
		}
		siElement.addNode(featureElement);
		return siElement.serialize();
	}
}