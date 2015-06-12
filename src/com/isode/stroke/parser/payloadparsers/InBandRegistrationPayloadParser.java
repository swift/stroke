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
import com.isode.stroke.parser.payloadparsers.FormParser;
import com.isode.stroke.parser.payloadparsers.FormParserFactory;
import com.isode.stroke.elements.InBandRegistrationPayload;
import com.isode.stroke.base.NotNull;

public class InBandRegistrationPayloadParser extends GenericPayloadParser<InBandRegistrationPayload> {

	private final int TopLevel = 0;
	private final int PayloadLevel = 1;
	private int level = TopLevel;
	private FormParserFactory formParserFactory;
	private FormParser formParser;
	private String currentText = "";

	public InBandRegistrationPayloadParser() {
		super(new InBandRegistrationPayload());
		level = TopLevel;
		formParser = null;
		formParserFactory = new FormParserFactory();	
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(ns, "ns");
		if (level == TopLevel) {

		}
		else if (level == PayloadLevel) {
			if (element.equals("x") && ns.equals("jabber:x:data")) {
				assert(formParser == null);
				formParser = (FormParser)(formParserFactory.createPayloadParser());
			}
			else {
				currentText = "";
			}
		}

		if (formParser != null) {
			formParser.handleStartElement(element, ns, attributes);
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

		if (formParser != null) {
			formParser.handleEndElement(element, ns);
		}

		if (level == TopLevel) {

		}
		else if (level == PayloadLevel) {
			if (formParser != null) {
				getPayloadInternal().setForm(formParser.getPayloadInternal());
				formParser = null;
			}
			else if (element.equals("registered")) {
				getPayloadInternal().setRegistered(true);
			}
			else if (element.equals("remove")) {
				getPayloadInternal().setRemove(true);
			}
			else if (element.equals("instructions")) {
				getPayloadInternal().setInstructions(currentText);
			}
			else if (element.equals("username")) {
				getPayloadInternal().setUsername(currentText);
			}
			else if (element.equals("nick")) {
				getPayloadInternal().setNick(currentText);
			}
			else if (element.equals("password")) {
				getPayloadInternal().setPassword(currentText);
			}
			else if (element.equals("name")) {
				getPayloadInternal().setName(currentText);
			}
			else if (element.equals("first")) {
				getPayloadInternal().setFirst(currentText);
			}
			else if (element.equals("last")) {
				getPayloadInternal().setLast(currentText);
			}
			else if (element.equals("email")) {
				getPayloadInternal().setEMail(currentText);
			}
			else if (element.equals("address")) {
				getPayloadInternal().setAddress(currentText);
			}
			else if (element.equals("city")) {
				getPayloadInternal().setCity(currentText);
			}
			else if (element.equals("state")) {
				getPayloadInternal().setState(currentText);
			}
			else if (element.equals("zip")) {
				getPayloadInternal().setZip(currentText);
			}
			else if (element.equals("phone")) {
				getPayloadInternal().setPhone(currentText);
			}
			else if (element.equals("url")) {
				getPayloadInternal().setURL(currentText);
			}
			else if (element.equals("date")) {
				getPayloadInternal().setDate(currentText);
			}
			else if (element.equals("misc")) {
				getPayloadInternal().setMisc(currentText);
			}
			else if (element.equals("text")) {
				getPayloadInternal().setText(currentText);
			}
			else if (element.equals("key")) {
				getPayloadInternal().setKey(currentText);
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		if (formParser != null) {
			formParser.handleCharacterData(data);
		}
		else {
			currentText += data;
		}
	}
}