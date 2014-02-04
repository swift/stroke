/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.PubSubPayload;

public class PubSubSubscribe extends PubSubPayload {

public PubSubSubscribe() {
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public JID getJID() {
	return jid_;
}

public void setJID(JID jid) {
	jid_ = jid;
}

public PubSubOptions getOptions() {
	return options_;
}

public void setOptions(PubSubOptions options) {
	options_ = options;
}

String node_;
JID jid_;
PubSubOptions options_;

}
