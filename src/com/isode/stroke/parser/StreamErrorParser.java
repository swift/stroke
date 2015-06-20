/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser;

import com.isode.stroke.parser.GenericElementParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.StreamError;
import com.isode.stroke.base.NotNull;

public class StreamErrorParser extends GenericElementParser<StreamError> {

	private final int TopLevel = 0; 
	private final int ElementLevel = 1;
	private int level = 0;
	private String currentText = "";

	public StreamErrorParser() {
		super(StreamError.class);
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		++level;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {
		NotNull.exceptIfNull(element, "element");
		--level;
		if (level == ElementLevel && ns.equals("urn:ietf:params:xml:ns:xmpp-streams")) {
			if (element.equals("text")) {
				getElementGeneric().setText(currentText);
			}
			else if (element.equals("bad-format")) {
				getElementGeneric().setType(StreamError.Type.BadFormat);
			}
			else if(element.equals("bad-namespace-prefix")) {
				getElementGeneric().setType(StreamError.Type.BadNamespacePrefix);
			}
			else if(element.equals("conflict")) {
				getElementGeneric().setType(StreamError.Type.Conflict);
			}
			else if(element.equals("connection-timeout")) {
				getElementGeneric().setType(StreamError.Type.ConnectionTimeout);
			}
			else if(element.equals("host-gone")) {
				getElementGeneric().setType(StreamError.Type.HostGone);
			}
			else if(element.equals("host-unknown")) {
				getElementGeneric().setType(StreamError.Type.HostUnknown);
			}
			else if(element.equals("improper-addressing")) {
				getElementGeneric().setType(StreamError.Type.ImproperAddressing);
			}
			else if(element.equals("internal-server-error")) {
				getElementGeneric().setType(StreamError.Type.InternalServerError);
			}
			else if(element.equals("invalid-from")) {
				getElementGeneric().setType(StreamError.Type.InvalidFrom);
			}
			else if(element.equals("invalid-id")) {
				getElementGeneric().setType(StreamError.Type.InvalidID);
			}
			else if(element.equals("invalid-namespace")) {
				getElementGeneric().setType(StreamError.Type.InvalidNamespace);
			}
			else if(element.equals("invalid-xml")) {
				getElementGeneric().setType(StreamError.Type.InvalidXML);
			}
			else if(element.equals("not-authorized")) {
				getElementGeneric().setType(StreamError.Type.NotAuthorized);
			}
			else if(element.equals("not-well-formed")) {
				getElementGeneric().setType(StreamError.Type.NotWellFormed);
			}
			else if(element.equals("policy-violation")) {
				getElementGeneric().setType(StreamError.Type.PolicyViolation);
			}
			else if(element.equals("remote-connection-failed")) {
				getElementGeneric().setType(StreamError.Type.RemoteConnectionFailed);
			}
			else if(element.equals("reset")) {
				getElementGeneric().setType(StreamError.Type.Reset);
			}
			else if(element.equals("resource-constraint")) {
				getElementGeneric().setType(StreamError.Type.ResourceConstraint);
			}
			else if(element.equals("restricted-xml")) {
				getElementGeneric().setType(StreamError.Type.RestrictedXML);
			}
			else if(element.equals("see-other-host")) {
				getElementGeneric().setType(StreamError.Type.SeeOtherHost);
			}
			else if(element.equals("system-shutdown")) {
				getElementGeneric().setType(StreamError.Type.SystemShutdown);
			}
			else if(element.equals("undefined-condition")) {
				getElementGeneric().setType(StreamError.Type.UndefinedCondition);
			}
			else if(element.equals("unsupported-encoding")) {
				getElementGeneric().setType(StreamError.Type.UnsupportedEncoding);
			}
			else if(element.equals("unsupported-stanza-type")) {
				getElementGeneric().setType(StreamError.Type.UnsupportedStanzaType);
			}
			else if(element.equals("unsupported-version")) {
				getElementGeneric().setType(StreamError.Type.UnsupportedVersion);
			}
			else {
				getElementGeneric().setType(StreamError.Type.UndefinedCondition);
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		currentText += data;
	}
}