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

package com.isode.stroke.network;

import com.isode.stroke.signals.Signal1;

public abstract class ConnectionServer {

	public enum Error {
		Conflict,
		UnknownError
	};

	public abstract HostAddressPort getAddressPort();

	public abstract Error tryStart(); // FIXME: This should become the new start

	public abstract void start();

	public abstract void stop();

	public final Signal1<Connection> onNewConnection = new Signal1<Connection>();
}
