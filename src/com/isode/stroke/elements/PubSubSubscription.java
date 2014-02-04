/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.PubSubPayload;

public class PubSubSubscription extends PubSubPayload {
public enum SubscriptionType
{
	None,
	Pending,
	Subscribed,
	Unconfigured
}

public PubSubSubscription() {
}

public SubscriptionType getSubscription() {
	return subscription_;
}

public void setSubscription(SubscriptionType subscription) {
	subscription_ = subscription;
}

public String getSubscriptionID() {
	return subscriptionID_;
}

public void setSubscriptionID(String subscriptionID) {
	subscriptionID_ = subscriptionID;
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public JID getJID() {
	return jid_;
}

public void setJID(JID jid) {
	jid_ = jid;
}

public PubSubSubscribeOptions getOptions() {
	return options_;
}

public void setOptions(PubSubSubscribeOptions options) {
	options_ = options;
}

SubscriptionType subscription_;
String subscriptionID_;
String node_;
JID jid_;
PubSubSubscribeOptions options_;

}
