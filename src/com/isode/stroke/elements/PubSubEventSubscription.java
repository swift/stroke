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
import java.util.Date;
import com.isode.stroke.elements.PubSubEventPayload;

public class PubSubEventSubscription extends PubSubEventPayload {
public enum SubscriptionType
{
	None,
	Pending,
	Subscribed,
	Unconfigured
}

public PubSubEventSubscription() {
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

public Date getExpiry() {
	return expiry_;
}

public void setExpiry(Date expiry) {
	expiry_ = expiry;
}

SubscriptionType subscription_;
String subscriptionID_;
String node_;
JID jid_;
Date expiry_;

}
