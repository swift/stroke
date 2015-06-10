/*
 * Copyright (c) 2014 Isode Limited.
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
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.UserTune;
import com.isode.stroke.base.NotNull;

public class UserTuneSerializer extends GenericPayloadSerializer<UserTune> {

	private PayloadSerializerCollection serializers;

	public UserTuneSerializer(PayloadSerializerCollection serializers) {
		super(UserTune.class);
		this.serializers = serializers;
	}

	public String serializePayload(UserTune payload) {
		if (payload == null) {
			return "";
		}

		XMLElement element = new XMLElement("tune", "http://jabber.org/protocol/tune");
		if (payload.getRating() != null) {
			element.addNode(new XMLElement("rating", "", Integer.toString(payload.getRating())));
		}
		if (payload.getTitle() != null) {
			element.addNode(new XMLElement("title", "", payload.getTitle()));
		}
		if (payload.getTrack() != null) {
			element.addNode(new XMLElement("track", "", payload.getTrack()));
		}
		if (payload.getArtist() != null) {
			element.addNode(new XMLElement("artist", "", payload.getArtist()));
		}
		if (payload.getURI() != null) {
			element.addNode(new XMLElement("uri", "", payload.getURI()));
		}
		if (payload.getSource() != null) {
			element.addNode(new XMLElement("source", "", payload.getSource()));
		}
		if (payload.getLength() != null) {
			element.addNode(new XMLElement("length", "", Integer.toString(payload.getLength())));
		}
		return element.serialize();
	}
}