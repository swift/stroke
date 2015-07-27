/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
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

package com.isode.stroke.network;

import com.isode.stroke.signals.Signal1;

public abstract class NATTraversalRemovePortForwardingRequest {

	public static class PortMapping {
		public enum Protocol {
			TCP,
			UDP
		};

		public int publicPort;
		public int localPort;
		public Protocol protocol;
		public long leaseInSeconds;
	};

	public abstract void start();
	public abstract void stop();

	public final Signal1<Boolean /* failure */> onResult = new Signal1<Boolean>();
}