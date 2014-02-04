/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.PubSubEventPayload;

public class PubSubEventDelete extends PubSubEventPayload {

public PubSubEventDelete() {
}

public PubSubEventRedirect getRedirects() {
	return redirects_;
}

public void setRedirects(PubSubEventRedirect redirects) {
	redirects_ = redirects;
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

PubSubEventRedirect redirects_;
String node_;

}
