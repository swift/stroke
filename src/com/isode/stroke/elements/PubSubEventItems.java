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
import com.isode.stroke.elements.PubSubEventPayload;

public class PubSubEventItems extends PubSubEventPayload {

public PubSubEventItems() {
}

public ArrayList<PubSubEventItem> getItems() {
	return items_;
}

public void setItems(ArrayList<PubSubEventItem> items) {
	items_ = items;
}

public void addItem(PubSubEventItem value) {
	items_.add(value);
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public ArrayList<PubSubEventRetract> getRetracts() {
	return retracts_;
}

public void setRetracts(ArrayList<PubSubEventRetract> retracts) {
	retracts_ = retracts;
}

public void addRetract(PubSubEventRetract value) {
	retracts_.add(value);
}

ArrayList<PubSubEventItem> items_ = new ArrayList<PubSubEventItem>();
String node_;
ArrayList<PubSubEventRetract> retracts_ = new ArrayList<PubSubEventRetract>();

}
