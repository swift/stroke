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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.isode.stroke.elements.PubSubEventSubscription;

public class PubSubEventSubscriptionParser extends GenericPayloadParser<PubSubEventSubscription> {
public PubSubEventSubscriptionParser(PayloadParserFactoryCollection parsers) {
	super(new PubSubEventSubscription());

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
		attributeValue = attributes.getAttribute("expiry");
		if (!attributeValue.isEmpty()) {
			getPayloadInternal().setExpiry(stringToDate(attributeValue));
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

private static PubSubEventSubscription.SubscriptionType parseSubscriptionType(String value) {
	if (value.equals("none")) {
		return PubSubEventSubscription.SubscriptionType.None;
	} else if (value.equals("pending")) {
		return PubSubEventSubscription.SubscriptionType.Pending;
	} else if (value.equals("subscribed")) {
		return PubSubEventSubscription.SubscriptionType.Subscribed;
	} else if (value.equals("unconfigured")) {
		return PubSubEventSubscription.SubscriptionType.Unconfigured;
	} else {
		return null;
	}
}

private static Date stringToDate(String date) {
	String format = "YYYY-MM-ddThh:mm:ssZ";
	SimpleDateFormat parser = new SimpleDateFormat(format);
	try {
		return parser.parse(date);
	} catch (ParseException e) {
		return null;
	}
}

PayloadParserFactoryCollection parsers_;
int level_;
PayloadParser currentPayloadParser_;
}
