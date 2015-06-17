/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
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
import com.isode.stroke.serializer.xml.XMLNode;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.serializer.payloadserializers.JingleFileTransferDescriptionSerializer;
import com.isode.stroke.serializer.payloadserializers.JingleIBBTransportPayloadSerializer;
import com.isode.stroke.serializer.payloadserializers.JingleS5BTransportPayloadSerializer;
import java.util.logging.Logger;

public class JingleContentPayloadSerializer extends GenericPayloadSerializer<JingleContentPayload> {

	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public JingleContentPayloadSerializer() {
		super(JingleContentPayload.class);
	}

	private String creatorToString(JingleContentPayload.Creator creator) {
		switch(creator) {
			case InitiatorCreator:
				return "initiator";
			case ResponderCreator:
				return "responder";
			case UnknownCreator:
				logger_.warning("Serializing unknown creator value.");
				return "ERROR ERROR ERROR";
		}
		assert(false);
		return "";
	}

	public String serializePayload(JingleContentPayload payload) {

		XMLElement payloadXML = new XMLElement("content");
		payloadXML.setAttribute("creator", creatorToString(payload.getCreator()));
		payloadXML.setAttribute("name", payload.getName());
		
		if (!payload.getDescriptions().isEmpty()) {
			// JingleFileTransferDescription
			JingleFileTransferDescriptionSerializer ftSerializer = new JingleFileTransferDescriptionSerializer();
			JingleFileTransferDescription filetransfer;
			
			for(JingleDescription desc : payload.getDescriptions()) {
				if(desc instanceof JingleFileTransferDescription) {
					filetransfer = (JingleFileTransferDescription)(desc);
					payloadXML.addNode(new XMLRawTextNode(ftSerializer.serializePayload(filetransfer)));
				}
			}
		}
		
		if (!payload.getTransports().isEmpty()) {	
			// JingleIBBTransportPayload
			JingleIBBTransportPayloadSerializer ibbSerializer = new JingleIBBTransportPayloadSerializer();
			JingleIBBTransportPayload ibb;
			
			// JingleS5BTransportPayload
			JingleS5BTransportPayloadSerializer s5bSerializer = new JingleS5BTransportPayloadSerializer();
			JingleS5BTransportPayload s5b;

			for(JingleTransportPayload transport: payload.getTransports()) {
				if(transport instanceof JingleIBBTransportPayload) {
					ibb = (JingleIBBTransportPayload)(transport);
					payloadXML.addNode(new XMLRawTextNode(ibbSerializer.serializePayload(ibb)));
				} else if (transport instanceof JingleS5BTransportPayload) {
					s5b = (JingleS5BTransportPayload)(transport);
					payloadXML.addNode(new XMLRawTextNode(s5bSerializer.serializePayload(s5b)));
				}
			}
		}
		return payloadXML.serialize();
	}
}