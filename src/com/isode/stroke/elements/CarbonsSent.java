/*
 * Copyright (c) 2015 Isode Limited.
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
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.base.NotNull;

public class CarbonsSent extends Payload {

	private Forwarded forwarded_;

	/**
	* Default Constructor.
	*/
	public CarbonsSent() {

	}

	/**
	* @param forwarded, Not Null.
	*/
	public void setForwarded(Forwarded forwarded) {
		NotNull.exceptIfNull(forwarded, "forwarded");
		forwarded_ = forwarded;
	}

	/**
	* @return forwarded, Not Null.
	*/
	public Forwarded getForwarded() {
		return forwarded_;
	}
}