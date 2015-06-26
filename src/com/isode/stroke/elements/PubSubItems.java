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

public class PubSubItems extends PubSubPayload {

public PubSubItems() {
}

public Long getMaximumItems() {
	return maximumItems_;
}

public void setMaximumItems(Long maximumItems) {
	maximumItems_ = maximumItems;
}

public ArrayList<PubSubItem> getItems() {
	return items_;
}

public void setItems(ArrayList<PubSubItem> items) {
	items_ = items;
}

public void addItem(PubSubItem value) {
	items_.add(value);
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

private Long maximumItems_;
private ArrayList<PubSubItem> items_ = new ArrayList<PubSubItem>();
private String subscriptionID_;
private String node_;

}
