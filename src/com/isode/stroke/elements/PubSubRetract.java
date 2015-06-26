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

public class PubSubRetract extends PubSubPayload {

public PubSubRetract() {
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

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public boolean isNotify() {
	return notify_;
}

public void setNotify(boolean notify) {
	notify_ = notify;
}

private ArrayList<PubSubItem> items_ = new ArrayList<PubSubItem>();
private String node_ = "";
private boolean notify_;

}
