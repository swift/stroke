/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.Form;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.PubSubPayload;

public class PubSubOptions extends PubSubPayload {

public PubSubOptions() {
}

public Form getData() {
	return data_;
}

public void setData(Form data) {
	data_ = data;
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

Form data_;
String subscriptionID_;
String node_;
JID jid_;

}
