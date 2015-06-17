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
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.stringcodecs.Base64;

public class JingleFileTransferFileInfoParser extends GenericPayloadParser<JingleFileTransferFileInfo> {

	private int level = 0;
	private String charData = "";
	private String hashAlg = "";
	private Long rangeOffset = null;

	public JingleFileTransferFileInfoParser() {
		super(new JingleFileTransferFileInfo());
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		charData = "";
		if (element.equals("hash")) {
			if(attributes.getAttributeValue("algo") != null) {
				hashAlg = attributes.getAttributeValue("algo");
			} else {
				hashAlg = "";
			}
		}
		else if (element.equals("range")) {
			if(attributes.getAttributeValue("offset") != null) {
				try {
				rangeOffset = Long.parseLong(attributes.getAttributeValue("offset"));
				}
				catch(NumberFormatException e) {
					rangeOffset = null;
				}
			} else {
				rangeOffset = null;
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
		if (level == 1) {
			if (element.equals("date")) {
				getPayloadInternal().setDate(DateTime.stringToDate(charData));
			}
			else if (element.equals("desc")) {
				getPayloadInternal().setDescription(charData);
			}
			else if (element.equals("media-type")) {
				getPayloadInternal().setMediaType(charData);
			}
			else if (element.equals("name")) {
				getPayloadInternal().setName(charData);
			}
			else if (element.equals("size")) {
				getPayloadInternal().setSize(Long.parseLong(charData));
			}
			else if (element.equals("range")) {
				getPayloadInternal().setSupportsRangeRequests(true);
				if (rangeOffset != null) {
					getPayloadInternal().setRangeOffset(rangeOffset.longValue());
				} else {
					getPayloadInternal().setRangeOffset(0L);
				}
			}
			else if (element.equals("hash")) {
				getPayloadInternal().addHash(new HashElement(hashAlg, Base64.decode(charData)));
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		charData += data;
	}
}