/*
 * Copyright (c) 2011-2015 Isode Limited.
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

public interface NATTraverser {

	public NATTraversalGetPublicIPRequest createGetPublicIPRequest();
	public NATTraversalForwardPortRequest createForwardPortRequest(int localPort, int publicPort);
	public NATTraversalRemovePortForwardingRequest createRemovePortForwardingRequest(int localPort, int publicPort);
}