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
import com.isode.stroke.elements.PubSubEventItem;
import com.isode.stroke.elements.PubSubEventRetract;
import com.isode.stroke.elements.PubSubEventItems;

public class PubSubEventItemsParser extends GenericPayloadParser<PubSubEventItems> {
public PubSubEventItemsParser(PayloadParserFactoryCollection parsers) {
	super(new PubSubEventItems());

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
	}

	if (level_ == 1) {
		if (element.equals("item") && ns.equals("http://jabber.org/protocol/pubsub#event")) {
			currentPayloadParser_ = new PubSubEventItemParser(parsers_);
		}
		if (element.equals("retract") && ns.equals("http://jabber.org/protocol/pubsub#event")) {
			currentPayloadParser_ = new PubSubEventRetractParser(parsers_);
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
		if (element.equals("item") && ns.equals("http://jabber.org/protocol/pubsub#event")) {
			getPayloadInternal().addItem((PubSubEventItem)currentPayloadParser_.getPayload());
		}
		if (element.equals("retract") && ns.equals("http://jabber.org/protocol/pubsub#event")) {
			getPayloadInternal().addRetract((PubSubEventRetract)currentPayloadParser_.getPayload());
		}
		currentPayloadParser_ = null;
	}
}

public void handleCharacterData(String data) {
	if (level_ > 1 && currentPayloadParser_ != null) {
		currentPayloadParser_.handleCharacterData(data);
	}
}

PayloadParserFactoryCollection parsers_;
int level_;
PayloadParser currentPayloadParser_;
}
