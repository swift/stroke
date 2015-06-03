/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;

public class VCardUpdate extends Payload {

	private String photoHash_ = new String();

	public VCardUpdate() {
		this("");
	}

	public VCardUpdate(String photoHash) {
		this.photoHash_ = photoHash;
	}

	public void setPhotoHash(String photoHash) {
		photoHash_ = photoHash;
	}

	public String getPhotoHash() {
		return photoHash_;
	}
}