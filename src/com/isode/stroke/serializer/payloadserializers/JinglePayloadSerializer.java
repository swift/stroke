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

import com.isode.stroke.serializer.payloadserializers.JingleContentPayloadSerializer;
import com.isode.stroke.serializer.payloadserializers.JingleFileTransferHashSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.base.NotNull;
import java.util.Vector;
import java.util.logging.Logger;

public class JinglePayloadSerializer extends GenericPayloadSerializer<JinglePayload> {

	private PayloadSerializerCollection serializers;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public JinglePayloadSerializer(PayloadSerializerCollection serializers) {
		super(JinglePayload.class);
		this.serializers = serializers;
	}

	private String actionToString(JinglePayload.Action action) {
		switch(action) {
			case ContentAccept:
				return "content-accept";
			case ContentAdd:
				return "content-add";
			case ContentModify:
				return "content-modify";
			case ContentReject:
				return "content-reject";
			case ContentRemove:
				return "content-remove";
			case DescriptionInfo:
				return "description-info";
			case SecurityInfo:
				return "security-info";
			case SessionAccept:
				return "session-accept";
			case SessionInfo:
				return "session-info";
			case SessionInitiate:
				return "session-initiate";
			case SessionTerminate:
				return "session-terminate";
			case TransportAccept:
				return "transport-accept";
			case TransportInfo:
				return "transport-info";
			case TransportReject:
				return "transport-reject";
			case TransportReplace:
				return "transport-replace";
			case UnknownAction:
				logger_.warning("Serializing unknown action value.\n");
				return "";
		}
		assert(false);
		return "";
	}

	private String reasonTypeToString(JinglePayload.Reason.Type type) {
		switch(type) {
			case UnknownType:
				logger_.warning("Unknown jingle reason type!\n");
				return "";
			case AlternativeSession:
				return "alternative-session";
			case Busy:
				return "busy";
			case Cancel:
				return "cancel";
			case ConnectivityError:
				return "connectivity-error";
			case Decline:
				return "decline";
			case Expired:
				return "expired";
			case FailedApplication:
				return "failed-application";
			case FailedTransport:
				return "failed-transport";
			case GeneralError:
				return "general-error";
			case Gone:
				return "gone";
			case IncompatibleParameters:
				return "incompatible-parameters";
			case MediaError:
				return "media-error";
			case SecurityError:
				return "security-error";
			case Success:
				return "success";
			case Timeout:
				return "timeout";
			case UnsupportedApplications:
				return "unsupported-applications";
			case UnsupportedTransports:
				return "unsupported-transports";
		}
		assert(false);
		return "";
	}

	public String serializePayload(JinglePayload payload) {
		XMLElement jinglePayload = new XMLElement("jingle", "urn:xmpp:jingle:1");
		jinglePayload.setAttribute("action", actionToString(payload.getAction()));
		jinglePayload.setAttribute("initiator", payload.getInitiator().toString());
		jinglePayload.setAttribute("sid", payload.getSessionID());

		Vector<Payload> payloads = payload.getPayloads();
		if (!payloads.isEmpty()) {
			for(Payload subPayload : payloads) {
				PayloadSerializer serializer = serializers.getPayloadSerializer(subPayload);
				if (serializer != null) {
					jinglePayload.addNode(new XMLRawTextNode(serializer.serialize(subPayload)));
				}
			}
		}
		
		if (payload.getReason() != null) {
			XMLElement reason = new XMLElement("reason");
			reason.addNode(new XMLElement(reasonTypeToString(payload.getReason().type)));
			if (payload.getReason().text.length() != 0) {
				reason.addNode(new XMLElement("desc", "", payload.getReason().text));
			}
			jinglePayload.addNode(reason);
		}
		
		return jinglePayload.serialize();
	}
}