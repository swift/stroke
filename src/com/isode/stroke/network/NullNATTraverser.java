/*
 * Copyright (c) 2011 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.Event;
import com.isode.stroke.eventloop.EventLoop;

public class NullNATTraverser implements NATTraverser {

	private EventLoop eventLoop;

	class NullNATTraversalGetPublicIPRequest extends NATTraversalGetPublicIPRequest {

		public NullNATTraversalGetPublicIPRequest(EventLoop eventLoop) {
			this.eventLoop = eventLoop;
		}

		public void start() {
			eventLoop.postEvent(new Event.Callback() {
				@Override
				public void run() {
					onResult.emit(null);
				}
			});
		}

		public void stop() {
		}

		private EventLoop eventLoop;
	};

	class NullNATTraversalForwardPortRequest extends NATTraversalForwardPortRequest {

		public NullNATTraversalForwardPortRequest(EventLoop eventLoop) {
			this.eventLoop = eventLoop;			
		}

		public void start() {
			eventLoop.postEvent(new Event.Callback() {
				@Override
				public void run() {
					onResult.emit(null);
				}
			});
		}

		public void stop() {
		}

		private EventLoop eventLoop;
	};

	class NullNATTraversalRemovePortForwardingRequest extends NATTraversalRemovePortForwardingRequest {

		public NullNATTraversalRemovePortForwardingRequest(EventLoop eventLoop) {
			this.eventLoop = eventLoop;		
		}

		public void start() {
			eventLoop.postEvent(new Event.Callback() {
				@Override
				public void run() {
					onResult.emit(true);
				}
			});
		}

		public void stop() {
		}

		private EventLoop eventLoop;
	};

	public NullNATTraverser(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}

	public NATTraversalGetPublicIPRequest createGetPublicIPRequest() {
		return new NullNATTraversalGetPublicIPRequest(eventLoop);
	}

	public NATTraversalForwardPortRequest createForwardPortRequest(int localPort, int publicPort) {
		return new NullNATTraversalForwardPortRequest(eventLoop);
	}

	public NATTraversalRemovePortForwardingRequest createRemovePortForwardingRequest(int localPort, int publicPort) {
		return new NullNATTraversalRemovePortForwardingRequest(eventLoop);
	}
}