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

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.base.NotNull;

public class JingleFileTransferHashParser extends GenericPayloadParser<JingleFileTransferHash> {

	private int level = 0;
	private PayloadParser currentPayloadParser;

	public JingleFileTransferHashParser() {
		super(new JingleFileTransferHash());
		this.level = 0;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		if (level == 1 && element.equals("file")) {
			currentPayloadParser = new JingleFileTransferFileInfoParser();
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
		if (level >= 1 && currentPayloadParser != null) {
			currentPayloadParser.handleEndElement(element, ns);
		}

		if (level == 1) {
			JingleFileTransferFileInfo info = (JingleFileTransferFileInfo)(currentPayloadParser.getPayload());
			if (info != null) {
				getPayloadInternal().setFileInfo(info);
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		if (level >= 1 && currentPayloadParser != null) {
			currentPayloadParser.handleCharacterData(data);
		}
	}
}