/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.base.IDGenerator;
import java.util.Set;
import java.util.HashSet;

public class SOCKS5BytestreamRegistry {

	private Set<String> availableBytestreams = new HashSet<String>();
	private IDGenerator idGenerator = new IDGenerator();

	public SOCKS5BytestreamRegistry() {

	}

	public void setHasBytestream(final String destination, boolean b) {
		if (b) {
			availableBytestreams.add(destination);
		}
		else {
			availableBytestreams.remove(destination);
		}
	}

	public boolean hasBytestream(final String destination) {
		return availableBytestreams.contains(destination);
	}

	/**
	 * Generate a new session ID to use for new S5B streams.
	 */
	public String generateSessionID() {
		return idGenerator.generateID();
	}
}