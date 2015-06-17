/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.jid.JID;

public class JingleReasonParser extends GenericPayloadParser<JinglePayload.Reason> {

	private int level;
	private boolean parseText;
	private String text = "";

	public JingleReasonParser() {
		super(new JinglePayload.Reason());
		this.level = 0;
		this.parseText = false;
	}

	private	JinglePayload.Reason.Type stringToReasonType(String type) {
		if (type.equals("alternative-session")) {
			return JinglePayload.Reason.Type.AlternativeSession;
		} else if (type.equals("busy")) {
			return JinglePayload.Reason.Type.Busy;
		} else if (type.equals("cancel")) {
			return JinglePayload.Reason.Type.Cancel;
		} else if (type.equals("connectivity-error")) {
			return JinglePayload.Reason.Type.ConnectivityError;
		} else if (type.equals("decline")) {
			return JinglePayload.Reason.Type.Decline;
		} else if (type.equals("expired")) {
			return JinglePayload.Reason.Type.Expired;
		} else if (type.equals("failed-application")) {
			return JinglePayload.Reason.Type.FailedApplication;
		} else if (type.equals("failed-transport")) {
			return JinglePayload.Reason.Type.FailedTransport;
		} else if (type.equals("general-error")) {
			return JinglePayload.Reason.Type.GeneralError;
		} else if (type.equals("gone")) {
			return JinglePayload.Reason.Type.Gone;
		} else if (type.equals("incompatible-parameters")) {
			return JinglePayload.Reason.Type.IncompatibleParameters;
		} else if (type.equals("media-error")) {
			return JinglePayload.Reason.Type.MediaError;
		} else if (type.equals("security-error")) {
			return JinglePayload.Reason.Type.SecurityError;
		} else if (type.equals("success")) {
			return JinglePayload.Reason.Type.Success;
		} else if (type.equals("timeout")) {
			return JinglePayload.Reason.Type.Timeout;
		} else if (type.equals("unsupported-applications")) {
			return JinglePayload.Reason.Type.UnsupportedApplications;
		} else if (type.equals("unsupported-transports")) {
			return JinglePayload.Reason.Type.UnsupportedTransports;
		} else {
			return JinglePayload.Reason.Type.UnknownType;
		}
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		if (level == 1) {
			if (element.equals("text")) {
				parseText = true;
			} else {
				// reason type
				getPayloadInternal().type = stringToReasonType(element);
			}
		}
		++level;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {
		--level;
		if (element.equals("text")) {
			parseText = false;
			getPayloadInternal().text = text;
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		if (parseText) {
			text += data;
		}
	}
}