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
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.StreamInitiationFileInfo;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.DateTime;

public class StreamInitiationFileInfoParser extends GenericPayloadParser<StreamInitiationFileInfo> {

	private int level = 0;
	private boolean parseDescription;
	private String desc = "";
	
	public StreamInitiationFileInfoParser() {
		super(new StreamInitiationFileInfo());
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes, NotNull.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(attributes, "attributes");
		if (level == 0) {
			if(attributes.getAttributeValue("name") != null) {
				getPayloadInternal().setName(attributes.getAttributeValue("name"));
			} else {
				getPayloadInternal().setName("");
			}

			if(attributes.getAttributeValue("hash") != null) {
				getPayloadInternal().setHash(attributes.getAttributeValue("hash"));
			} else {
				getPayloadInternal().setHash("");
			}

			if(attributes.getAttributeValue("algo") != null) {
				getPayloadInternal().setAlgo(attributes.getAttributeValue("algo"));
			} else {
				getPayloadInternal().setAlgo("md5");
			}			

			if(attributes.getAttributeValue("size") != null) {
				try {
					getPayloadInternal().setSize(Long.parseLong(attributes.getAttributeValue("size")));
				} catch (NumberFormatException e) {
					getPayloadInternal().setSize(0L);
				}
			} else {
				getPayloadInternal().setSize(0L);
			}

			if(attributes.getAttributeValue("date") != null) {
				getPayloadInternal().setDate(DateTime.stringToDate(attributes.getAttributeValue("date")));
			} else {
				getPayloadInternal().setDate(DateTime.stringToDate(""));
			}

		} else if (level == 1) {
			if (element.equals("desc")) {
				parseDescription = true;
			} else {
				parseDescription = false;
				if (element.equals("range")) {
					long offset = 0;
					if (attributes.getAttributeValue("offset") != null) {
						try {
							offset = Long.parseLong(attributes.getAttributeValue("offset"));
						} catch (NumberFormatException e) {
							offset = 0;
						}
					} else {
						offset = 0;
					}
					if (offset == 0) {
						getPayloadInternal().setSupportsRangeRequests(true);
					} else {
						getPayloadInternal().setRangeOffset(offset);
					}
				}
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
		if (parseDescription && element.equals("desc")) {
			parseDescription = false;
			getPayloadInternal().setDescription(desc);
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		if (parseDescription) {
			desc += data;
		}
	}
}