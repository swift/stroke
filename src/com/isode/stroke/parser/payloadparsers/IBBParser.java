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
import com.isode.stroke.elements.IBB;
import com.isode.stroke.stringcodecs.Base64;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.ByteArray;
import java.util.Vector;

public class IBBParser extends GenericPayloadParser<IBB> {

	public IBBParser() {
		super(new IBB());
	}

	private final int TopLevel = 0;
	private int level;
	private String currentText = "";

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(attributes, "attributes");
		if (level == TopLevel) {
			if (element.equals("data")) {
				getPayloadInternal().setAction(IBB.Action.Data);
				getPayloadInternal().setStreamID(attributes.getAttribute("sid"));
				try {
					getPayloadInternal().setSequenceNumber(Integer.parseInt(attributes.getAttribute("seq")));
				}
				catch (NumberFormatException e) {

				}
			}
			else if (element.equals("open")) {
				getPayloadInternal().setAction(IBB.Action.Open);
				getPayloadInternal().setStreamID(attributes.getAttribute("sid"));
				if (attributes.getAttribute("stanza").equals("message")) {
					getPayloadInternal().setStanzaType(IBB.StanzaType.MessageStanza);
				}
				else {
					getPayloadInternal().setStanzaType(IBB.StanzaType.IQStanza);
				}
				try {
					getPayloadInternal().setBlockSize(Integer.parseInt(attributes.getAttribute("block-size")));
				}
				catch (NumberFormatException e) {

				}
			}
			else if (element.equals("close")) {
				getPayloadInternal().setAction(IBB.Action.Close);
				getPayloadInternal().setStreamID(attributes.getAttribute("sid"));
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
		NotNull.exceptIfNull(element, "element");
		--level;
		if (level == TopLevel) {
			if (element.equals("data")) {
				ByteArray data = new ByteArray(currentText);
				getPayloadInternal().setData(Base64.decode(data.toString()));
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