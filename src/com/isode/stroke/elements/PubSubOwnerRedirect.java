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

public class PubSubOwnerRedirect extends Payload {

public PubSubOwnerRedirect() {
}

public String getURI() {
	return uri_;
}

public void setURI(String uri) {
	uri_ = uri;
}

String uri_;

}
