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

package com.isode.stroke.base;

import com.isode.stroke.network.Timer;
import com.isode.stroke.base.StartStoppable;

public class StartStopper<T extends StartStoppable> {

	private T target;

	public StartStopper(T target) {
		this.target = target;
		target.start();
	}
}