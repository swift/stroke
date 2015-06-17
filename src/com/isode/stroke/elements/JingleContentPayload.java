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

package com.isode.stroke.elements;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.elements.JingleTransportPayload;
import java.util.Vector;

public class JingleContentPayload extends Payload {

	public enum Creator {
		UnknownCreator,
		InitiatorCreator,
		ResponderCreator
	};

	/*public enum Senders {
		NoSenders,
		InitiatorSender,
		ResponderSender,
		BothSenders,
	};*/

	private Creator creator;
	private String name = "";
	//private Senders senders;
	private Vector<JingleDescription> descriptions = new Vector<JingleDescription>();
	private Vector<JingleTransportPayload> transports = new Vector<JingleTransportPayload>();

	/**
	* Default Constructor.
	*/
	public JingleContentPayload() {
		this.creator = Creator.UnknownCreator;
	}

	/**
	* @return creator, Not Null.
	*/
	public Creator getCreator() {
		return creator;
	}

	/**
	* @param creator, Not Null.
	*/
	public void setCreator(Creator creator) {
		NotNull.exceptIfNull(creator, "creator");
		this.creator = creator;
	}

	/**
	* @return name, Not Null.
	*/
	public String getName() {
		return name;
	}

	/**
	* @param name, Not Null.
	*/
	public void setName(String name) {
		NotNull.exceptIfNull(name, "name");
		this.name = name;
	}

	/**
	* @return descriptions, Not Null.
	*/
	public Vector<JingleDescription> getDescriptions() {
		return descriptions;
	}

	/**
	* @param description, Not Null.
	*/
	public void addDescription(JingleDescription description) {
		NotNull.exceptIfNull(description, "description");
		descriptions.add(description);
	}

	/**
	* @return transports, Not Null.
	*/
	public Vector<JingleTransportPayload> getTransports() {
		return transports;
	}

	/**
	* @param transport, Not Null.
	*/
	public void addTransport(JingleTransportPayload transport) {
		NotNull.exceptIfNull(transport, "transport");		
		transports.add(transport);
	}

	/**
	 * Get the description of the given type.
	 * @param <T> type.
	 * @param type, not null.
	 * @return description of given type.
	 */
	public <T extends Payload> T getDescription(T type) {
        for (JingleDescription description : descriptions) {
            if (description.getClass().isAssignableFrom(type.getClass())) {
                return (T)description;
            }
        }
        return null;
	}

	/**
	 * Get the transport of the given type.
	 * @param <T> type.
	 * @param type, not null.
	 * @return transport of given type.
	 */
	public <T extends Payload> T getTransport(T type) {
        for (JingleTransportPayload transport : transports) {
            if (transport.getClass().isAssignableFrom(type.getClass())) {
                return (T)transport;
            }
        }
        return null;
	}
}