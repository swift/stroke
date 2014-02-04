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
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.PubSubSubscribeOptions;
import com.isode.stroke.elements.PubSubSubscription;

public class PubSubSubscriptionParser extends GenericPayloadParser<PubSubSubscription> {
public PubSubSubscriptionParser(PayloadParserFactoryCollection parsers) {
	super(new PubSubSubscription());

	parsers_ = parsers;
	level_ = 0;
}

public void handleStartElement(String element, String ns, AttributeMap attributes) {
	if (level_ == 0) {
		String attributeValue;
		attributeValue = attributes.getAttribute("subscription");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setSubscription(parseSubscriptionType(attributeValue));
		}
		attributeValue = attributes.getAttribute("subid");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setSubscriptionID(attributeValue);
		}
		attributeValue = attributes.getAttribute("node");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setNode(attributeValue);
		}
		attributeValue = attributes.getAttribute("jid");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setJID(JID.fromString(attributeValue));
		}
	}

	if (level_ == 1) {
		if (element.equals("subscribe-options") && ns.equals("http://jabber.org/protocol/pubsub")) {
			currentPayloadParser_ = new PubSubSubscribeOptionsParser(parsers_);
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
		if (element.equals("subscribe-options") && ns.equals("http://jabber.org/protocol/pubsub")) {
			getPayloadInternal().setOptions((PubSubSubscribeOptions)(currentPayloadParser_.getPayload()));
		}
		currentPayloadParser_ = null;
	}
}

public void handleCharacterData(String data) {
	if (level_ > 1 && currentPayloadParser_ != null) {
		currentPayloadParser_.handleCharacterData(data);
	}
}

private static PubSubSubscription.SubscriptionType parseSubscriptionType(String value) {
	if (value.equals("none")) {
		return PubSubSubscription.SubscriptionType.None;
	} else if (value.equals("pending")) {
		return PubSubSubscription.SubscriptionType.Pending;
	} else if (value.equals("subscribed")) {
		return PubSubSubscription.SubscriptionType.Subscribed;
	} else if (value.equals("unconfigured")) {
		return PubSubSubscription.SubscriptionType.Unconfigured;
	} else {
		return null;
	}
}

PayloadParserFactoryCollection parsers_;
int level_;
PayloadParser currentPayloadParser_;
}
