/*
 * Copyright (c) 2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2013 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
package com.isode.stroke.elements;

import java.util.Date;

public class Idle extends Payload {
	public Idle() {}
	public Idle(Date since) {
		since_ = since;
	}

	public void setSince(Date since) {
		since_ = since;
	}

	public Date getSince() {
		return since_;
	}

	private Date since_;
}
