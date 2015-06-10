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
import com.isode.stroke.elements.UserTune;
import com.isode.stroke.base.NotNull;

public class UserTuneParser extends GenericPayloadParser<UserTune> {

	private int level = 0;
	private String currentText;

	public UserTuneParser() {
		super(new UserTune());
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		if (level == 1) {
			currentText = "";
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
			try {
				if (element.equals("artist")) {
					getPayloadInternal().setArtist(currentText);
				}
				else if (element.equals("length")) {
					getPayloadInternal().setLength(Integer.parseInt(currentText));
				}
				else if (element.equals("rating")) {
					getPayloadInternal().setRating(Integer.parseInt(currentText));
				}
				else if (element.equals("source")) {
					getPayloadInternal().setSource(currentText);
				}
				else if (element.equals("title")) {
					getPayloadInternal().setTitle(currentText);
				}
				else if (element.equals("track")) {
					getPayloadInternal().setTrack(currentText);
				}
				else if (element.equals("URI")) {
					getPayloadInternal().setURI(currentText);
				}
			}
			catch (NumberFormatException e) {

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