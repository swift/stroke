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
import com.isode.stroke.elements.PubSubPayload;

public class PubSubSubscriptions extends PubSubPayload {

public PubSubSubscriptions() {
}

public ArrayList<PubSubSubscription> getSubscriptions() {
	return subscriptions_;
}

public void setSubscriptions(ArrayList<PubSubSubscription> subscriptions) {
	subscriptions_ = subscriptions;
}

public void addSubscription(PubSubSubscription value) {
	subscriptions_.add(value);
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

private ArrayList<PubSubSubscription> subscriptions_ = new ArrayList<PubSubSubscription>();
private String node_;

}
