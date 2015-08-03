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

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.elements.Bytestreams;

public class S5BProxyRequest extends Payload {

	private StreamHost streamHost;
	private String sid = "";
	private JID activate;

	public static class StreamHost {
		public String host = "";
		public int port;
		public JID jid = new JID();
	};

	/**
	* Default Constructor.
	*/
	public S5BProxyRequest() {

	}

	/**
	* @return streamHost.
	*/
	public StreamHost getStreamHost() {
		return streamHost;
	}

	/**
	* @param streamHost.
	*/
	public void setStreamHost(StreamHost streamHost) {
		this.streamHost = streamHost;
	}

	/**
	* @return sid, Not Null.
	*/
	public String getSID() {
		return sid;
	}

	/**
	* @param sid, Not Null.
	*/
	public void setSID(String sid) {
		NotNull.exceptIfNull(sid, "sid");
		this.sid = sid;
	}

	/**
	* @return activate.
	*/
	public JID getActivate() {
		return activate;
	}

	/**
	* @param activate.
	*/
	public void setActivate(JID activate) {
		this.activate = activate;
	}
}