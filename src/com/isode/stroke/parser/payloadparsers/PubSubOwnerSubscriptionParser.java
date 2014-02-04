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
import com.isode.stroke.elements.PubSubOwnerSubscription;

public class PubSubOwnerSubscriptionParser extends GenericPayloadParser<PubSubOwnerSubscription> {
public PubSubOwnerSubscriptionParser(PayloadParserFactoryCollection parsers) {
	super(new PubSubOwnerSubscription());

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
		attributeValue = attributes.getAttribute("jid");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setJID(JID.fromString(attributeValue));
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

private static PubSubOwnerSubscription.SubscriptionType parseSubscriptionType(String value) {
	if (value.equals("none")) {
		return PubSubOwnerSubscription.SubscriptionType.None;
	} else if (value.equals("pending")) {
		return PubSubOwnerSubscription.SubscriptionType.Pending;
	} else if (value.equals("subscribed")) {
		return PubSubOwnerSubscription.SubscriptionType.Subscribed;
	} else if (value.equals("unconfigured")) {
		return PubSubOwnerSubscription.SubscriptionType.Unconfigured;
	} else {
		return null;
	}
}

PayloadParserFactoryCollection parsers_;
int level_;
PayloadParser currentPayloadParser_;
}
