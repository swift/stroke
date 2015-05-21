/*
 * Copyright (c) 2010-2011 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.base;

import java.util.UUID;

public class IDGenerator {

	/**
	* IDGenerator();
	*/
	public IDGenerator() {

	}

	/**
	* Randomly generates a UUID.
	* @return String representation of the UUID, which will never be null.
	*/
	public static String generateID() {
		return UUID.randomUUID().toString();
	}
}