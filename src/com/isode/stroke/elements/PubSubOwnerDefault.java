/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.PubSubOwnerPayload;

public class PubSubOwnerDefault extends PubSubOwnerPayload {

public PubSubOwnerDefault() {
}

public Form getData() {
	return data_;
}

public void setData(Form data) {
	data_ = data;
}

private Form data_;

}
