/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;

public class PubSubEventDisassociate extends Payload {

public PubSubEventDisassociate() {
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

String node_;

}
