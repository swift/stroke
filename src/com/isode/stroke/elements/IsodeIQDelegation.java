/*
 * Copyright (c) 2014 Isode Limited.
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

public class IsodeIQDelegation extends Payload {

	private Forwarded forward;

	/**
	* Default Constructor.
	*/
	public IsodeIQDelegation() {

	}

	/**
	* @param forward, Not Null.
	*/
	public void setForward(Forwarded forward) {
		NotNull.exceptIfNull(forward, "forward");
		this.forward = forward;
	}

	/**
	* @return forward, Not Null.
	*/
	public Forwarded getForward() {
		return forward;
	}
}