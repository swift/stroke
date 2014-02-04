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
import com.isode.stroke.elements.Payload;

public class PubSubConfigure extends Payload {

public PubSubConfigure() {
}

public Form getData() {
	return data_;
}

public void setData(Form data) {
	data_ = data;
}

Form data_;

}
