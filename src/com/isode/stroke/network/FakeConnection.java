/*
 * Copyright (c) 2010-2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.Event;
import com.isode.stroke.base.SafeByteArray;
import java.util.Vector;

public class FakeConnection extends Connection {

	public enum State {
		Initial,
		Connecting,
		Connected,
		Disconnected,
		DisconnectedWithError
	};

	public FakeConnection(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
		this.state = State.Initial;
		this.delayConnect = false;
	}

	public void listen() {
		assert(false);
	}

	public HostAddressPort getLocalAddress() {
		return new HostAddressPort();
	}

	public void setError(final Error e) {
		error = e;
		state = State.DisconnectedWithError;
		if (connectedTo != null) {
			eventLoop.postEvent(new Event.Callback() {
				@Override
				public void run() {
					onDisconnected.emit(error);
				}
			});
		}
	}

	public void connect(final HostAddressPort address) {
		if (delayConnect) {
			state = State.Connecting;
		}
		else {
			if (error == null) {
				connectedTo = address;
				state = State.Connected;
			}
			else {
				state = State.DisconnectedWithError;
			}
			eventLoop.postEvent(new Event.Callback() {
				@Override
				public void run() {
					onConnectFinished.emit(error != null ? true : false);
				}
			});
		}
	}

	public void disconnect() {
		if (error == null) {
			state = State.Disconnected;
		}
		else {
			state = State.DisconnectedWithError;
		}
		connectedTo = null;
		eventLoop.postEvent(new Event.Callback() {
			@Override
			public void run() {
				onDisconnected.emit(error);
			}
		});
	}

	public void write(final SafeByteArray data) {
		dataWritten.add(data);
	}

	public void setDelayConnect() {
		delayConnect = true;
	}

	public EventLoop eventLoop;
	public HostAddressPort connectedTo;
	public Vector<SafeByteArray> dataWritten = new Vector<SafeByteArray>();
	public Error error;
	public State state;
	public boolean delayConnect;
}