/*
 * Copyright (c) 2013 Isode Limited.
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
import com.isode.stroke.elements.UserLocation;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.DateTime;

public class UserLocationParser extends GenericPayloadParser<UserLocation> {

	private int level = 0;
	private String currentText = "";

	public UserLocationParser() {
		super(new UserLocation());
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
				if (element.equals("accuracy")) {
					getPayloadInternal().setAccuracy(Float.parseFloat(currentText));
				}
				else if (element.equals("alt")) {
					getPayloadInternal().setAltitude(Float.parseFloat(currentText));
				}
				else if (element.equals("area")) {
					getPayloadInternal().setArea(currentText);
				}
				else if (element.equals("bearing")) {
					getPayloadInternal().setBearing(Float.parseFloat(currentText));
				}
				else if (element.equals("building")) {
					getPayloadInternal().setBuilding(currentText);
				}
				else if (element.equals("country")) {
					getPayloadInternal().setCountry(currentText);
				}
				else if (element.equals("countrycode")) {
					getPayloadInternal().setCountryCode(currentText);
				}
				else if (element.equals("datum")) {
					getPayloadInternal().setDatum(currentText);
				}
				else if (element.equals("description")) {
					getPayloadInternal().setDescription(currentText);
				}
				else if (element.equals("error")) {
					getPayloadInternal().setError(Float.parseFloat(currentText));
				}
				else if (element.equals("floor")) {
					getPayloadInternal().setFloor(currentText);
				}
				else if (element.equals("lat")) {
					getPayloadInternal().setLatitude(Float.parseFloat(currentText));
				}
				else if (element.equals("locality")) {
					getPayloadInternal().setLocality(currentText);
				}
				else if (element.equals("lon")) {
					getPayloadInternal().setLongitude(Float.parseFloat(currentText));
				}
				else if (element.equals("postalcode")) {
					getPayloadInternal().setPostalCode(currentText);
				}
				else if (element.equals("region")) {
					getPayloadInternal().setRegion(currentText);
				}
				else if (element.equals("room")) {
					getPayloadInternal().setRoom(currentText);
				}
				else if (element.equals("speed")) {
					getPayloadInternal().setSpeed(Float.parseFloat(currentText));
				}
				else if (element.equals("street")) {
					getPayloadInternal().setStreet(currentText);
				}
				else if (element.equals("text")) {
					getPayloadInternal().setText(currentText);
				}
				else if (element.equals("timestamp")) {
					getPayloadInternal().setTimestamp(DateTime.stringToDate(currentText));
				}
				else if (element.equals("uri")) {
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