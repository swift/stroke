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
import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.Event;
import com.isode.stroke.base.SafeByteArray;

public class DummyConnection extends Connection implements EventOwner {

	public DummyConnection(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}

	public void listen() {
		assert(false);
	}

	public void connect(final HostAddressPort port) {
		assert(false);
	}

	public void disconnect() {
		//assert(false);
	}

	public void write(final SafeByteArray data) {
		eventLoop.postEvent(new Event.Callback() {
			@Override
			public void run() {
				onDataWritten.emit();
			}
		});
		onDataSent.emit(data);
	}

	public void receive(final SafeByteArray data) {
		eventLoop.postEvent(new Event.Callback() {
			@Override
			public void run() {
				onDataRead.emit(data);
			}
		});
	}

	public HostAddressPort getLocalAddress() {
		return localAddress;
	}

	public Signal1<SafeByteArray> onDataSent = new Signal1<SafeByteArray>();

	public EventLoop eventLoop;
	public HostAddressPort localAddress;
}