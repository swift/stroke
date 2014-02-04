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
import com.isode.stroke.elements.Payload;

public class PubSubEventItem extends Payload {

public PubSubEventItem() {
}

public String getPublisher() {
	return publisher_;
}

public void setPublisher(String publisher) {
	publisher_ = publisher;
}

public ArrayList<Payload> getData() {
	return data_;
}

public void setData(ArrayList<Payload> data) {
	data_ = data;
}

public void addData(Payload value) {
	data_.add(value);
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public String getID() {
	return id_;
}

public void setID(String id) {
	id_ = id;
}

String publisher_;
ArrayList<Payload> data_ = new ArrayList<Payload>();
String node_;
String id_;

}
