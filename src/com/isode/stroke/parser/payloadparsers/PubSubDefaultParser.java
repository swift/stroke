/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.elements.PubSubDefault;

public class PubSubDefaultParser extends GenericPayloadParser<PubSubDefault> {
public PubSubDefaultParser(PayloadParserFactoryCollection parsers) {
	super(new PubSubDefault());

	parsers_ = parsers;
	level_ = 0;
}

public void handleStartElement(String element, String ns, AttributeMap attributes) {
	if (level_ == 0) {
		String attributeValue;
		attributeValue = attributes.getAttribute("node");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setNode(attributeValue);
		}
		attributeValue = attributes.getAttribute("type");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setType(parseType(attributeValue));
		}
	}

	if (level_ >= 1 && currentPayloadParser_ != null) {
		currentPayloadParser_.handleStartElement(element, ns, attributes);
	}
	++level_;
}

public void handleEndElement(String element, String ns) {
	--level_;
	if (currentPayloadParser_ != null) {
		if (level_ >= 1) {
			currentPayloadParser_.handleEndElement(element, ns);
		}
		if (level_ != 1) {
			return;
		}
		currentPayloadParser_ = null;
	}
}

public void handleCharacterData(String data) {
	if (level_ > 1 && currentPayloadParser_ != null) {
		currentPayloadParser_.handleCharacterData(data);
	}
}

private static PubSubDefault.Type parseType(String value) {
	if (value.equals("none")) {
		return PubSubDefault.Type.None;
	} else if (value.equals("collection")) {
		return PubSubDefault.Type.Collection;
	} else if (value.equals("leaf")) {
		return PubSubDefault.Type.Leaf;
	} else {
		return null;
	}
}

PayloadParserFactoryCollection parsers_;
int level_;
PayloadParser currentPayloadParser_;
}
