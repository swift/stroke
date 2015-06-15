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
import com.isode.stroke.elements.IBB;
import com.isode.stroke.stringcodecs.Base64;
import com.isode.stroke.base.NotNull;

public class IBBSerializer extends GenericPayloadSerializer<IBB> {

	public IBBSerializer() {
		super(IBB.class);
	}

	public String serializePayload(IBB ibb) {
		switch(ibb.getAction()) {
			case Data: {
				XMLElement ibbElement = new XMLElement("data", "http://jabber.org/protocol/ibb");
				ibbElement.setAttribute("sid", ibb.getStreamID());
				if (ibb.getSequenceNumber() >= 0) {
					ibbElement.setAttribute("seq", Integer.toString(ibb.getSequenceNumber()));
				}
				ibbElement.addNode(new XMLTextNode(Base64.encode(ibb.getData())));
				return ibbElement.serialize();
			}
			case Open: {
				XMLElement ibbElement = new XMLElement("open", "http://jabber.org/protocol/ibb");
				ibbElement.setAttribute("sid", ibb.getStreamID());
				switch (ibb.getStanzaType()) {
					case IQStanza: ibbElement.setAttribute("stanza", "iq"); break;
					case MessageStanza: ibbElement.setAttribute("stanza", "message"); break;
				}
				assert(ibb.getBlockSize() > 0);
				ibbElement.setAttribute("block-size", Integer.toString(ibb.getBlockSize()));
				return ibbElement.serialize();
			}
			case Close: {
				XMLElement ibbElement = new XMLElement("close", "http://jabber.org/protocol/ibb");
				ibbElement.setAttribute("sid", ibb.getStreamID());
				return ibbElement.serialize();
			}
		}
		assert(false);
		return "";
	}
}