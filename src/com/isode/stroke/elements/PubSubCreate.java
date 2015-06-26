/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.PubSubPayload;

public class PubSubCreate extends PubSubPayload {

public PubSubCreate() {
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public PubSubConfigure getConfigure() {
	return configure_;
}

public void setConfigure(PubSubConfigure configure) {
	configure_ = configure;
}

private String node_ = "";
private PubSubConfigure configure_;

}
