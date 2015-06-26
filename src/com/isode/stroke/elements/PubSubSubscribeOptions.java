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

public class PubSubSubscribeOptions extends Payload {

public PubSubSubscribeOptions() {
}

public boolean isRequired() {
	return required_;
}

public void setRequired(boolean required) {
	required_ = required;
}

private boolean required_;

}
