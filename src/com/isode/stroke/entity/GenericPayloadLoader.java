/*
 * Copyright (c) 2015, Isode Limited.
 * All rights reserved.
 */
package com.isode.stroke.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PlatformXMLParserFactory;
import com.isode.stroke.parser.XMLParser;
import com.isode.stroke.parser.XMLParserClient;

public class GenericPayloadLoader<PayloadType extends Payload, Parser extends PayloadParser> {
	
	private final Parser parser;

	public GenericPayloadLoader(Parser parser) {
		this.parser = parser;
	}
	
	@SuppressWarnings("unchecked")
	public PayloadType loadPayload(final InputStream is) throws IOException {
		if (is == null) return null;
		final StringBuilder sb = new StringBuilder(2048);
		final char[] read = new char[2048];
		final InputStreamReader ir = new InputStreamReader(is, "UTF-8");
		for (int i; -1 != (i = ir.read(read)); sb.append(read, 0, i));
		
		final XMLParser xmlParser = PlatformXMLParserFactory.createXMLParser(new ParserClient(parser));
		xmlParser.parse(sb.toString());
		
		return (PayloadType) parser.getPayload();
	}

	private static class ParserClient implements XMLParserClient {
		private final PayloadParser parser;
		
		public ParserClient(PayloadParser parser) {
			this.parser = parser;
		}

		@Override
		public void handleStartElement(String element, String ns, AttributeMap attributes) {
			parser.handleStartElement(element, ns, attributes);
		}

		@Override
		public void handleEndElement(String element, String ns) {
			parser.handleEndElement(element, ns);
		}

		@Override
		public void handleCharacterData(String data) {
			parser.handleCharacterData(data);
		}
		
	}

}
