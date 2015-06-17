/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Isode Limited.
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
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.JingleS5BTransportPayload;

public class JingleS5BTransportPayloadSerializer extends GenericPayloadSerializer<JingleS5BTransportPayload> {

	public JingleS5BTransportPayloadSerializer() {
		super(JingleS5BTransportPayload.class);
	}

	private String modeToString(JingleS5BTransportPayload.Mode mode) {
		switch(mode) {
			case TCPMode:
				return "tcp";
			case UDPMode:
				return "udp";
		}
		assert(false);
		return "";
	}

	private String typeToString(JingleS5BTransportPayload.Candidate.Type type) {
		switch(type) {
			case AssistedType:
				return "assisted";
			case DirectType:
				return "direct";
			case ProxyType:
				return "proxy";
			case TunnelType:
				return "tunnel";
		}
		assert(false);
		return "";
	}

	public String serializePayload(JingleS5BTransportPayload payload) {

		XMLElement payloadXML = new XMLElement("transport", "urn:xmpp:jingle:transports:s5b:1");
		payloadXML.setAttribute("sid", payload.getSessionID());
		payloadXML.setAttribute("mode", modeToString(payload.getMode()));
		if (payload.getDstAddr().length() != 0) {
			payloadXML.setAttribute("dstaddr", payload.getDstAddr());
		}

		for(JingleS5BTransportPayload.Candidate candidate : payload.getCandidates()) {
			XMLElement candidateXML = new XMLElement("candidate");
			candidateXML.setAttribute("cid", candidate.cid);
			candidateXML.setAttribute("host", candidate.hostPort.getAddress().toString());
			candidateXML.setAttribute("jid", candidate.jid.toString());
			candidateXML.setAttribute("port", Integer.toString(candidate.hostPort.getPort()));
			candidateXML.setAttribute("priority", Integer.toString(candidate.priority));
			candidateXML.setAttribute("type", typeToString(candidate.type));
			payloadXML.addNode(candidateXML);
		}

		if (payload.hasCandidateError()) {
			payloadXML.addNode(new XMLElement("candidate-error"));
		}
		if (payload.hasProxyError()) {
			payloadXML.addNode(new XMLElement("proxy-error"));
		}

		if (payload.getActivated().length() != 0) {
			XMLElement activatedXML = new XMLElement("activated");
			activatedXML.setAttribute("cid", payload.getActivated());
			payloadXML.addNode(activatedXML);
		}
		if (payload.getCandidateUsed().length() != 0) {
			XMLElement candusedXML = new XMLElement("candidate-used");
			candusedXML.setAttribute("cid", payload.getCandidateUsed());
			payloadXML.addNode(candusedXML);
		}

		return payloadXML.serialize();
	}
}