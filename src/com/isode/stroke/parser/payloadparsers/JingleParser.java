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

public class JingleParser extends GenericPayloadParser<JinglePayload> {

	private PayloadParserFactoryCollection factories;
	private int level = 0;
	private PayloadParser currentPayloadParser;

	public JingleParser(PayloadParserFactoryCollection factories) {
		super(new JinglePayload());
		this.factories = factories;
		this.level = 0;
	}

	private JinglePayload.Action stringToAction(String str) {
		if (str.equals("content-accept")) {
			return JinglePayload.Action.ContentAccept;
		} else if (str.equals("content-add")) {
			return JinglePayload.Action.ContentAdd;
		} else if (str.equals("content-modify")) {
			return JinglePayload.Action.ContentModify;
		} else if (str.equals("content-reject")) {
			return JinglePayload.Action.ContentReject;
		} else if (str.equals("content-remove")) {
			return JinglePayload.Action.ContentRemove;
		} else if (str.equals("description-info")) {
			return JinglePayload.Action.DescriptionInfo;
		} else if (str.equals("security-info")) {
			return JinglePayload.Action.SecurityInfo;
		} else if (str.equals("session-accept")) {
			return JinglePayload.Action.SessionAccept;
		} else if (str.equals("session-info")) {
			return JinglePayload.Action.SessionInfo;
		} else if (str.equals("session-initiate")) {
			return JinglePayload.Action.SessionInitiate;
		} else if (str.equals("session-terminate")) {
			return JinglePayload.Action.SessionTerminate;
		} else if (str.equals("transport-accept")) {
			return JinglePayload.Action.TransportAccept;
		} else if (str.equals("transport-info")) {
			return JinglePayload.Action.TransportInfo;
		} else if (str.equals("transport-reject")) {
			return JinglePayload.Action.TransportReject;
		} else if (str.equals("transport-replace")) {
			return JinglePayload.Action.TransportReplace;
		} else {
			return JinglePayload.Action.UnknownAction;
		}
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(attributes, "attributes");
		if (level == 0) {
			// <jingle > tag
			JinglePayload payload = getPayloadInternal();
			if (attributes.getAttributeValue("action") != null) {
				payload.setAction(stringToAction(attributes.getAttributeValue("action")));
			} else {
				payload.setAction(stringToAction(""));
			}		

			if(attributes.getAttributeValue("initiator") != null) {
				payload.setInitiator(new JID(attributes.getAttributeValue("initiator")));
			} else {
				payload.setInitiator(new JID(""));
			}

			if(attributes.getAttributeValue("responder") != null) {
				payload.setResponder(new JID(attributes.getAttributeValue("responder")));
			} else {
				payload.setResponder(new JID(""));
			}

			if (attributes.getAttributeValue("sid") != null) {
				payload.setSessionID(attributes.getAttributeValue("sid"));
			} else {
				payload.setSessionID("");
			}
		}
		
		if (level == 1) {
			PayloadParserFactory payloadParserFactory = factories.getPayloadParserFactory(element, ns, attributes);
			if (payloadParserFactory != null) {
				currentPayloadParser = payloadParserFactory.createPayloadParser();
			}
		}
		
		if (level >= 1 && currentPayloadParser != null) {
			currentPayloadParser.handleStartElement(element, ns, attributes);
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
		if (currentPayloadParser != null) {
			if (level >= 1) {
				currentPayloadParser.handleEndElement(element, ns);
			}
			
			if (level == 1) {
				if(currentPayloadParser.getPayload() instanceof JinglePayload.Reason) {
					JinglePayload.Reason reason = (JinglePayload.Reason)(currentPayloadParser.getPayload());
					if (reason != null) {
						getPayloadInternal().setReason(reason);
					}
				}

				if(currentPayloadParser.getPayload() instanceof JingleContentPayload) {
					JingleContentPayload payload = (JingleContentPayload)(currentPayloadParser.getPayload());
					if (payload != null) {
						getPayloadInternal().addContent(payload);
					}
				}

				if(currentPayloadParser.getPayload() instanceof JingleFileTransferHash) {
					JingleFileTransferHash hash = (JingleFileTransferHash)(currentPayloadParser.getPayload());
					if (hash != null) {
						getPayloadInternal().addPayload(hash);
					}
				}
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		if (level > 1 && currentPayloadParser != null) {
			currentPayloadParser.handleCharacterData(data);
		}
	}
}