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

public class PubSubEventCollection extends PubSubEventPayload {

public PubSubEventCollection() {
}

public PubSubEventAssociate getAssociate() {
	return associate_;
}

public void setAssociate(PubSubEventAssociate associate) {
	associate_ = associate;
}

public String getNode() {
	return node_;
}

public void setNode(String node) {
	node_ = node;
}

public PubSubEventDisassociate getDisassociate() {
	return disassociate_;
}

public void setDisassociate(PubSubEventDisassociate disassociate) {
	disassociate_ = disassociate;
}

PubSubEventAssociate associate_;
String node_;
PubSubEventDisassociate disassociate_;

}
