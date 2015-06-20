/*
 * Copyright (c) 2010-2015 Isode Limited.
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
import com.isode.stroke.elements.StreamInitiation;
import com.isode.stroke.elements.StreamInitiationFileInfo;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.base.NotNull;

public class StreamInitiationParser extends GenericPayloadParser<StreamInitiation> {

	private static final String FILE_TRANSFER_NS  = "http://jabber.org/protocol/si/profile/file-transfer";
	private static final String FEATURE_NEG_NS = "http://jabber.org/protocol/feature-neg";
	private final int TopLevel = 0;
	private final int PayloadLevel = 1;
	private final int FileOrFeatureLevel = 2;
	private final int FormOrDescriptionLevel = 3;
	private int level = 0;
	private FormParserFactory formParserFactory = new FormParserFactory();
	private FormParser formParser;
	private boolean inFile;
	private boolean inFeature;
	private StreamInitiationFileInfo currentFile;
	private String currentText = "";

	public StreamInitiationParser() {
		super(new StreamInitiation());
	}

	/**
	* @param element, NotNull.
	* @param ns, NotNull.
	* @param attributes, NotNull.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(ns, "ns");
		NotNull.exceptIfNull(attributes, "attributes");		
		if (level == TopLevel) {
			getPayloadInternal().setID(attributes.getAttribute("id"));
			if (attributes.getAttribute("profile").length() != 0) {
				getPayloadInternal().setIsFileTransfer(attributes.getAttribute("profile").equals(FILE_TRANSFER_NS));
			}
		}
		else if (level == PayloadLevel) {
			if (element.equals("file")) {
				inFile = true;
				currentFile = new StreamInitiationFileInfo();
				currentFile.setName(attributes.getAttribute("name"));
				try {
					currentFile.setSize(Long.parseLong(attributes.getAttribute("size")));
				}
				catch (NumberFormatException e) {
				}
			}
			else if (element.equals("feature") && ns.equals(FEATURE_NEG_NS)) {
				inFeature = true;
			}
		}
		else if (level == FileOrFeatureLevel) {
			if (inFile && element.equals("desc")) {
				currentText = "";
			}
			else if (inFeature && formParserFactory.canParse(element, ns, attributes)) {
				assert(formParser == null);
				formParser = (FormParser)(formParserFactory.createPayloadParser());
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
			if (element.equals("file")) {
				getPayloadInternal().setFileInfo(currentFile);
				inFile = false;
			}
			else if (element.equals("feature") && ns.equals(FEATURE_NEG_NS)) {
				inFeature = false;
			}
		}
		else if (level == FileOrFeatureLevel) {
			if (inFile && element.equals("desc")) {
				currentFile.setDescription(currentText);
			}
			else if (formParser != null) {
				Form form = formParser.getPayloadInternal();
				if (form != null) {
					FormField field = (FormField)(form.getField("stream-method"));
					if (field != null) {
						if (form.getType().equals(Form.Type.FORM_TYPE)) {
							for (FormField.Option option : field.getOptions()) {
								getPayloadInternal().addProvidedMethod(option.value_);
							}
						}
						else if (form.getType().equals(Form.Type.SUBMIT_TYPE)) {
							if (!field.getValues().isEmpty()) {
								getPayloadInternal().setRequestedMethod(field.getValues().get(0));
							}
						}
					}
				}
				formParser = null;
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