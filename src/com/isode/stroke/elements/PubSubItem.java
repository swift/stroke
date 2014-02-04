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

public class PubSubItem extends Payload {

public PubSubItem() {
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

public String getID() {
	return id_;
}

public void setID(String id) {
	id_ = id;
}

ArrayList<Payload> data_ = new ArrayList<Payload>();
String id_;

}
