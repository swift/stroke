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
import com.isode.stroke.elements.PubSubEventPayload;

public class PubSubEventConfiguration extends PubSubEventPayload {

public PubSubEventConfiguration() {
}

public Form getData() {
	return data_;
}

public void setData(Form data) {
	data_ = data;
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

Form data_;
String node_;

}
