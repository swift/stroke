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

package com.isode.stroke.elements;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.Payload;
import java.util.Vector;

public class Bytestreams extends Payload {

	public class StreamHost {

		public String host;
		public JID jid;
		public int port;

		/**
		* Default Constructor.
		*/
		public StreamHost() {
			this("", new JID(), -1);
		}

		/**
		* StreamHost with single parameter, host.
		*/
		public StreamHost(String host) {
			this(host, new JID(), -1);
		}

		/**
		* StreamHost with two parameter, host and jid.
		*/
		public StreamHost(String host, JID jid) {
			this(host, jid, -1);
		}

		/**
		* StreamHost with three parameter, host, jid and port.
		*/
		public StreamHost(String host, JID jid, int port) {
			NotNull.exceptIfNull(host, "host");
			NotNull.exceptIfNull(jid, "jid");
			this.host = host;
			this.jid = jid;
			this.port = port;
		}

		/**
		* @return host, NotNull.
		*/
		public String getHost() {
			return host;
		}

		/**
		* @param host, NotNull.
		*/
		public void setHost(String host) {
			NotNull.exceptIfNull(host, "host");			
			this.host = host;
		}

		/**
		* @return jid, NotNull.
		*/
		public JID getJID() {
			return jid;
		}

		/**
		* @param jid, NotNull.
		*/
		public void setJID(JID jid) {
			NotNull.exceptIfNull(jid, "jid");
			this.jid = jid;
		}

		/**
		* @return port.
		*/
		public int getPort() {
			return port;
		}

		/**
		* @param port.
		*/
		public void setPort(int port) {
			this.port = port;
		}

	}

	private String id = "";
	JID usedStreamHost;
	Vector<StreamHost> streamHosts = new Vector<StreamHost>();

	/**
	* Default Constructor.
	*/
	public Bytestreams() {

	}

	/**
	* @return id, notnull.
	*/
	public String getStreamID() {
		return id;
	}

	/**
	* @param id, notnull.
	*/
	public void setStreamID(String id) {
		NotNull.exceptIfNull(id, "id");
		this.id = id;
	}

	/**
	* @return usedStreamHost
	*/
	public JID getUsedStreamHost() {
		return usedStreamHost;
	}

	/**
	* @param host
	*/
	public void setUsedStreamHost(JID host) {
		usedStreamHost = host;
	}

	/**
	* @return streamHosts, notnull.
	*/
	public Vector<StreamHost> getStreamHosts() {
		return streamHosts;
	}

	/**
	* @param streamHost, notnull.
	*/
	public void addStreamHost(StreamHost streamHost) {
		NotNull.exceptIfNull(streamHost, "streamHost");
		streamHosts.add(streamHost);
	}
}