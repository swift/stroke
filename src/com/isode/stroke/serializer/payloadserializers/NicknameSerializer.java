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
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.Nickname;
import com.isode.stroke.base.NotNull;

public class NicknameSerializer extends GenericPayloadSerializer<Nickname> {

	public NicknameSerializer() {
		super(Nickname.class);
	}

	public String serializePayload(Nickname nick) {
		XMLElement nickElement = new XMLElement("nick", "http://jabber.org/protocol/nick");
		nickElement.addNode(new XMLTextNode(nick.getNickname()));
		return nickElement.serialize();
	}
}