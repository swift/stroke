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

public class PubSubOwnerDelete extends PubSubOwnerPayload {

public PubSubOwnerDelete() {
}

public PubSubOwnerRedirect getRedirect() {
	return redirect_;
}

public void setRedirect(PubSubOwnerRedirect redirect) {
	redirect_ = redirect;
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

PubSubOwnerRedirect redirect_;
String node_;

}
