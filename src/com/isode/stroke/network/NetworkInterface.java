/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

public class NetworkInterface {

	private String name = "";
	private boolean loopback;
	private Vector<HostAddress> addresses = new Vector<HostAddress>();

	public NetworkInterface(final String name, boolean loopback) {
		this.name = name;
		this.loopback = loopback;
	}
	
	/**
	 * Creates a {@link NetworkInterface} from a {@link java.net.NetworkInterface} including
	 * all addresses in the {@link java.net.NetworkInterface}
	 * @param javaNI The  {@link java.net.NetworkInterface} to create the {@link NetworkInterface}
	 * from, should not be {@code null}.
	 * @throws SocketException If an I/O error occurs when trying to determine if it is
	 * a loop back interface.
	 */
	public NetworkInterface(java.net.NetworkInterface javaNI) throws SocketException {
	    this.name = javaNI.getName();
	    this.loopback = javaNI.isLoopback();
	    Enumeration<InetAddress> addressEnumeration = javaNI.getInetAddresses();
	    while (addressEnumeration.hasMoreElements()) {
	        InetAddress inetAddress = addressEnumeration.nextElement();
	        HostAddress hostAddress = new HostAddress(inetAddress);
	        addAddress(hostAddress);
	    }
	}

	public void addAddress(final HostAddress address) {
		addresses.add(address);
	}

	public Vector<HostAddress> getAddresses() {
		return addresses;
	}

	public String getName() {
		return name;
	}

	public boolean isLoopback() {
		return loopback;
	}
}