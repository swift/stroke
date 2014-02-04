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
import com.isode.stroke.elements.Payload;

public class PubSubOwnerSubscription extends Payload {
public enum SubscriptionType
{
	None,
	Pending,
	Subscribed,
	Unconfigured
}

public PubSubOwnerSubscription() {
}

public SubscriptionType getSubscription() {
	return subscription_;
}

public void setSubscription(SubscriptionType subscription) {
	subscription_ = subscription;
}

public JID getJID() {
	return jid_;
}

public void setJID(JID jid) {
	jid_ = jid;
}

SubscriptionType subscription_;
JID jid_;

}
