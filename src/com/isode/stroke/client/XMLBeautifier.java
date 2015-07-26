/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2014-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.client;

import java.util.Stack;
import com.isode.stroke.parser.XMLParserClient;
import com.isode.stroke.parser.XMLParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PlatformXMLParserFactory;

public class XMLBeautifier implements XMLParserClient {

	private boolean doIndention;
	private boolean doColoring;

	private int intLevel;
	private String inputBuffer = "";
	private StringBuffer buffer = new StringBuffer();
	private XMLParser parser;

	private boolean lastWasStepDown;
	private Stack<String> parentNSs = new Stack<String>();

	// all bold but reset
	public static final String colorReset = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String colorRed = "\u001B[31m";
	public static final String colorGreen = "\u001B[32m";
	public static final String colorYellow = "\u001B[33m";
	public static final String colorBlue = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String colorCyan = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public XMLBeautifier(boolean indention, boolean coloring) {
		this.doIndention = indention;
		this.doColoring = coloring;
		intLevel = 0;
		parser = null;
		lastWasStepDown = false;
	}

	public String beautify(final String text) {
		parser = PlatformXMLParserFactory.createXMLParser(this);
		intLevel = 0;
		buffer.append("");
		parser.parse(text);
		parser = null;
		return buffer.toString();
	}

	public void handleStartElement(final String element, final String ns, final AttributeMap attributes) {
		if (doIndention) {
			if (intLevel != 0) buffer.append("\n");
		}
		indent();
		buffer.append("<").append(doColoring ? styleTag(element) : element);
		if (!ns.isEmpty() && (!parentNSs.isEmpty() && !parentNSs.peek().equals(ns))) {
			buffer.append(" ");
			buffer.append((doColoring ? styleAttribute("xmlns") : "xmlns"));
			buffer.append("=");
			buffer.append("\"").append((doColoring ? styleNamespace(ns) : ns)).append("\"");
		}
		if (!attributes.getEntries().isEmpty()) {
			for(AttributeMap.Entry entry : attributes.getEntries()) {
				buffer.append(" ");
				buffer.append((doColoring ? styleAttribute(entry.getAttribute().getName()) : entry.getAttribute().getName()));
				buffer.append("=");
				buffer.append("\"").append((doColoring ? styleValue(entry.getValue()) : entry.getValue())).append("\"");
			}
		}
		buffer.append(">");
		++intLevel;
		lastWasStepDown = false;
		parentNSs.push(ns);
	}

	public void handleEndElement(final String element, final String ns) {
		--intLevel;
		parentNSs.pop();
		if (/*hadCDATA.top() ||*/ lastWasStepDown) {
			if (doIndention) {
				buffer.append("\n");
			}
			indent();
		}
		buffer.append("</").append((doColoring ? styleTag(element) : element)).append(">");
		lastWasStepDown = true;
	}

	public void handleCharacterData(final String data) {
		buffer.append(data);
		lastWasStepDown = false;
	}

	private void indent() {
	for (int i = 0; i < intLevel; ++i) {
			buffer.append(" ");
		}
	}

	private String styleTag(final String text) {
		String result = "";
		result += colorYellow;
		result += text;
		result += colorReset;
		return result;
	}

	private String styleNamespace(final String text) {
		String result = "";
		result += colorRed;
		result += text;
		result += colorReset;
		return result;
	}

	private String styleAttribute(final String text) {
		String result = "";
		result += colorGreen;
		result += text;
		result += colorReset;
		return result;
	}

	private String styleValue(final String text) {
		String result = "";
		result += colorCyan;
		result += text;
		result += colorReset;
		return result;
	}
}