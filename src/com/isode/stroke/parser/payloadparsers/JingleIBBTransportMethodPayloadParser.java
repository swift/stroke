/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.base.NotNull;

public class JingleIBBTransportMethodPayloadParser extends GenericPayloadParser<JingleIBBTransportPayload> {

	private int level = 0;

	public JingleIBBTransportMethodPayloadParser() {
		super(new JingleIBBTransportPayload());
		this.level = 0;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		try {
			String blockSize = attributes.getAttributeValue("block-size");
			if (blockSize != null) {
				getPayloadInternal().setBlockSize(Integer.parseInt(blockSize));
			}
		} 
		catch (NumberFormatException e) {

		}
		if(attributes.getAttributeValue("sid") != null) {
			getPayloadInternal().setSessionID(attributes.getAttributeValue("sid"));
		} else {
			getPayloadInternal().setSessionID("");
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