/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.PubSubOwnerPayload;

public class PubSubOwnerPurge extends PubSubOwnerPayload {

public PubSubOwnerPurge() {
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

private String node_ = "";

}
