/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.PubSubEventPayload;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

public class PubSubEventParser extends GenericPayloadParser<PubSubEvent> {

public PubSubEventParser(PayloadParserFactoryCollection parser) {
	super(new PubSubEvent());
	parsers_ = parser;
}

@Override
public void handleStartElement(String element, String ns, AttributeMap attributes) {
	if (level_ == 1) {
		if (element == "items" && ns == "http://jabber.org/protocol/pubsub#event") {
			currentPayloadParser_ = new PubSubEventItemsParser(parsers_);
		}
		if (element == "collection" && ns == "http://jabber.org/protocol/pubsub#event") {
			currentPayloadParser_ = new PubSubEventCollectionParser(parsers_);
		}
		if (element == "purge" && ns == "http://jabber.org/protocol/pubsub#event") {
			currentPayloadParser_ = new PubSubEventPurgeParser(parsers_);
		}
		if (element == "configuration" && ns == "http://jabber.org/protocol/pubsub#event") {
			currentPayloadParser_ = new PubSubEventConfigurationParser(parsers_);
		}
		if (element == "delete" && ns == "http://jabber.org/protocol/pubsub#event") {
			currentPayloadParser_ = new PubSubEventDeleteParser(parsers_);
		}
		if (element == "subscription" && ns == "http://jabber.org/protocol/pubsub#event") {
			currentPayloadParser_ = new PubSubEventSubscriptionParser(parsers_);
		}
	}

	if (level_>=1 && currentPayloadParser_!=null) {
		currentPayloadParser_.handleStartElement(element, ns, attributes);
	}
	++level_;
}

@Override
public void handleEndElement(String element, String ns) {
	--level_;
	if (currentPayloadParser_ != null) {
		if (level_ >= 1) {
			currentPayloadParser_.handleEndElement(element, ns);
		}

		if (level_ == 1) {
			if (currentPayloadParser_ != null) {
				getPayloadInternal().setPayload((PubSubEventPayload)currentPayloadParser_.getPayload());
			}
			currentPayloadParser_ = null;
		}
	}
}

@Override
public void handleCharacterData(String data) {
	if (level_ > 1 && currentPayloadParser_!=null) {
		currentPayloadParser_.handleCharacterData(data);
	}
}

PayloadParserFactoryCollection parsers_;
int level_;
PayloadParser currentPayloadParser_;
}
