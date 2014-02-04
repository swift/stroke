/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import java.util.ArrayList;
import com.isode.stroke.elements.PubSubOwnerPayload;

public class PubSubOwnerSubscriptions extends PubSubOwnerPayload {

public PubSubOwnerSubscriptions() {
}

public ArrayList<PubSubOwnerSubscription> getSubscriptions() {
	return subscriptions_;
}

public void setSubscriptions(ArrayList<PubSubOwnerSubscription> subscriptions) {
	subscriptions_ = subscriptions;
}

public void addSubscription(PubSubOwnerSubscription value) {
	subscriptions_.add(value);
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

ArrayList<PubSubOwnerSubscription> subscriptions_ = new ArrayList<PubSubOwnerSubscription>();
String node_;

}
