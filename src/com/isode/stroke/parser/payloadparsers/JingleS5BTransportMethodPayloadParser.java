/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
* Copyright (c) 2014-2015 Isode Limited.
* All rights reserved.v3.
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
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.network.HostAddress;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JingleS5BTransportMethodPayloadParser extends GenericPayloadParser<JingleS5BTransportPayload> {

	private int level = 0;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public JingleS5BTransportMethodPayloadParser() {
		super(new JingleS5BTransportPayload());
		this.level = 0;
	}

	private JingleS5BTransportPayload.Candidate.Type stringToType(String str) {
		if (str.equals("direct")) {
			return JingleS5BTransportPayload.Candidate.Type.DirectType;
		} else if (str.equals("assisted")) {
			return JingleS5BTransportPayload.Candidate.Type.AssistedType;
		} else if (str.equals("tunnel")) {
			return JingleS5BTransportPayload.Candidate.Type.TunnelType;
		} else if (str.equals("proxy")) {
			return JingleS5BTransportPayload.Candidate.Type.ProxyType;
		} else {
			logger_.warning("Unknown candidate type; falling back to default!");
			return JingleS5BTransportPayload.Candidate.Type.DirectType;
		}
	}
	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {

		if (level == 0) {
			if(attributes.getAttributeValue("sid") != null) {
				getPayloadInternal().setSessionID(attributes.getAttributeValue("sid"));
			} else {
				getPayloadInternal().setSessionID("");
			}

			String mode = "";
			if(attributes.getAttributeValue("mode") != null) {
				mode = attributes.getAttributeValue("mode");
			} else {
				mode = "tcp";
			}

			if (mode.equals("tcp")) {
				getPayloadInternal().setMode(JingleS5BTransportPayload.Mode.TCPMode);
			} else if(mode.equals("udp")) {
				getPayloadInternal().setMode(JingleS5BTransportPayload.Mode.UDPMode);
			} else {
				logger_.warning("Unknown S5B mode; falling back to default!");
				getPayloadInternal().setMode(JingleS5BTransportPayload.Mode.TCPMode);
			}

			if(attributes.getAttributeValue("dstaddr") != null) {
				getPayloadInternal().setDstAddr(attributes.getAttributeValue("dstaddr"));
			} else {
				getPayloadInternal().setDstAddr("");
			}

		} else if (level == 1) {
			if (element.equals("candidate")) {
				JingleS5BTransportPayload.Candidate candidate = new JingleS5BTransportPayload.Candidate();
				candidate.cid = "";
				if(attributes.getAttributeValue("cid") != null) {
					candidate.cid = attributes.getAttributeValue("cid");
				}

				int port = -1;
				try {
					if(attributes.getAttributeValue("port") != null) {
						port = Integer.parseInt(attributes.getAttributeValue("port"));
					}
				} catch (NumberFormatException e) {

				}
				
				try {
					candidate.hostPort = new HostAddressPort(new HostAddress(InetAddress.getByName("")), port);
					if(attributes.getAttributeValue("host") != null) {
						candidate.hostPort = new HostAddressPort(new HostAddress(InetAddress.getByName(attributes.getAttributeValue("host"))), port);
					}
				} catch (UnknownHostException e) {

				}

				candidate.jid = new JID("");
				if(attributes.getAttributeValue("jid") != null) {
					candidate.jid = new JID(attributes.getAttributeValue("jid"));
				}

				int priority = -1;
				try {
					if(attributes.getAttributeValue("priority") != null) {
						priority = Integer.parseInt(attributes.getAttributeValue("priority"));
					}
				} catch (NumberFormatException e) {

				}
				candidate.priority = priority;

				candidate.type = stringToType("direct");
				if(attributes.getAttributeValue("type") != null) {
					candidate.type = stringToType(attributes.getAttributeValue("type"));
				}
				getPayloadInternal().addCandidate(candidate);

			} else if (element.equals("candidate-used")) {
				if(attributes.getAttributeValue("cid") != null) {
					getPayloadInternal().setCandidateUsed(attributes.getAttributeValue("cid"));
				} else {
					getPayloadInternal().setCandidateUsed("");
				}
			} else if (element.equals("candidate-error")) {
				getPayloadInternal().setCandidateError(true);
			} else if (element.equals("activated")) {
				if(attributes.getAttributeValue("cid") != null) {
					getPayloadInternal().setActivated(attributes.getAttributeValue("cid"));
				} else {
					getPayloadInternal().setActivated("");
				}
			} else if (element.equals("proxy-error")) {
				getPayloadInternal().setProxyError(true);
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
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {

	}
}