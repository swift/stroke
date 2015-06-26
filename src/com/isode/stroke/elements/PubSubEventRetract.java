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

public class PubSubEventRetract extends Payload {

public PubSubEventRetract() {
}

public String getID() {
	return id_;
}

public void setID(String id) {
	id_ = id;
}

private String id_ = "";

}
