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

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.Bytestreams;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.NotNull;

public class BytestreamsParser extends GenericPayloadParser<Bytestreams> {

	private final int TopLevel = 0;
	private final int PayloadLevel = 1;
	private int level = TopLevel;

	public BytestreamsParser() {
		super(new Bytestreams());
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes, notnull.
	*/
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(attributes, "attributes");
		if (level == TopLevel) {
			getPayloadInternal().setStreamID(attributes.getAttribute("sid"));
		}
		else if (level == PayloadLevel) {
			if (element.equals("streamhost")) {
				Bytestreams bytestreams = new Bytestreams();
				try {
					getPayloadInternal().addStreamHost(bytestreams.new StreamHost(attributes.getAttribute("host"), new JID(attributes.getAttribute("jid")), Integer.parseInt(attributes.getAttribute("port"))));
				}
				catch(NumberFormatException e) {

				}
			}
			else if (element.equals("streamhost-used")) {
				getPayloadInternal().setUsedStreamHost(new JID(attributes.getAttribute("jid")));
			}
		}
		++level;
	}

	/**
	* @param element.
	* @param ns.
	*/
	public void handleEndElement(String element, String ns) {
		--level;
	}

	public void handleCharacterData(String data) {

	}
}