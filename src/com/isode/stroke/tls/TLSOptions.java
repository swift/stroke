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

package com.isode.stroke.tls;

public class TLSOptions {

	/**
  	 * This flag is not used in java, and is purely here to maintain
  	 * consistency with Swiften
  	 */
	public boolean schannelTLS1_0Workaround;

	public TLSOptions() {
		schannelTLS1_0Workaround = false;
	}
}