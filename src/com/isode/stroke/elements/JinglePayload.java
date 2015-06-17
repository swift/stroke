/*
 * Copyright (c) 2011-2014 Isode Limited.
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
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public class JinglePayload extends Payload {

	public static class Reason extends Payload {

		public enum Type {
			UnknownType,
			AlternativeSession,
			Busy,
			Cancel,
			ConnectivityError,
			Decline,
			Expired,
			FailedApplication,
			FailedTransport,
			GeneralError,
			Gone,
			IncompatibleParameters,
			MediaError,
			SecurityError,
			Success,
			Timeout,
			UnsupportedApplications,
			UnsupportedTransports
		};

		public Type type;
		public String text = "";

		public Reason() {
			this(Type.UnknownType, "");
		}

		public Reason(Type type) {
			this(type, "");
		}

		public Reason(Type type, String text) {
			NotNull.exceptIfNull(type, "type");
			NotNull.exceptIfNull(text, "text");
			this.type = type;
			this.text = text;
		}

		/**
		* @return Type, NotNull.
		*/
		public Type getType() {
			return this.type;
		}

		/**
		* @param Type, NotNull.
		*/
		public void setType(Type type) {
			NotNull.exceptIfNull(type, "type");
			this.type = type;
		}

		/**
		* @return Text, NotNull.
		*/
		public String getText() {
			return this.text;
		}

		/**
		* @param Text, NotNull.
		*/
		public void setText(String text) {
			NotNull.exceptIfNull(text, "text");
			this.text = text;
		}
	}
	
	public enum Action {
		UnknownAction,
		ContentAccept,
		ContentAdd,
		ContentModify,
		ContentReject,
		ContentRemove,
		DescriptionInfo,
		SecurityInfo,
		SessionAccept,
		SessionInfo,
		SessionInitiate,
		SessionTerminate,
		TransportAccept,
		TransportInfo,
		TransportReject,
		TransportReplace
	};

	private Action action;
	private JID initiator = new JID();
	private JID responder = new JID();
	private String sessionID = "";
	private Vector<Payload> payloads = new Vector<Payload>();
	private Reason reason = null;

	public JinglePayload() {
		this(Action.SessionTerminate, "");
	}

	public JinglePayload(Action action, String sessionID) {
		NotNull.exceptIfNull(action, "action");
		NotNull.exceptIfNull(sessionID, "sessionID");
		this.action = action;
		this.sessionID = sessionID;
	}

	/**
	* @param action, NotNull.
	*/
	public void setAction(Action action) {
		NotNull.exceptIfNull(action, "action");
		this.action = action;
	}

	/**
	* @return action, NotNull.
	*/
	public Action getAction() {
		return action;
	}

	/**
	* @param initiator, NotNull.
	*/
	public void setInitiator(JID initiator) {
		NotNull.exceptIfNull(initiator, "initiator");
		this.initiator = initiator;
	}

	/**
	* @return initiator, NotNull.
	*/
	public JID getInitiator() {
		return initiator;
	}

	/**
	* @param responder, NotNull.
	*/
	public void setResponder(JID responder) {
		NotNull.exceptIfNull(responder, "responder");
		this.responder = responder;
	}

	/**
	* @return responder, NotNull.
	*/
	public JID getResponder() {
		return responder;
	}

	/**
	* @param sessionID, NotNull.
	*/
	public void setSessionID(String id) {
		NotNull.exceptIfNull(id, "sessionID");
		sessionID = id;
	}

	/**
	* @return sessionID, NotNull.
	*/
	public String getSessionID() {
		return sessionID;
	}

	/**
	* @param content.
	*/
	public void addContent(JingleContentPayload content) {
		this.payloads.add(content);
	}

	/**
	* @param payload.
	*/			
	public void addPayload(Payload payload) {
		this.payloads.add(payload);
	}

	/**
	* @return payloads, of type JingleContentPayload.
	*/	
	public Vector<JingleContentPayload> getContents() {
		return getPayloads(new JingleContentPayload());
	}

	/**
	* @return payloads.
	*/	
	public Vector<Payload> getPayloads() {
		return payloads;
	}

	/**
	 * Get the payload of the given type from the stanza
	 * @param <T> payload type
	 * @param type payload type object instance, not null
	 * @return payload of given type, can be null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Payload> T getPayload(T type) {
		for (Payload payload : payloads) {
			if (payload.getClass().isAssignableFrom(type.getClass())) {
				return (T)payload;
			}
		}
		return null;
	}

	/**
	 * Get the payloads of the given type from the stanza
	 * @param <T> payload type
	 * @param type payload type object instance, not null
	 * @return list of payloads of given type, not null but can be empty
	 */
	@SuppressWarnings("unchecked")
	public <T extends Payload> Vector<T> getPayloads(T type) {
		Vector<T> matched_payloads = new Vector<T>();
		for (Payload payload : payloads) {
			if (payload.getClass().isAssignableFrom(type.getClass())) {
				matched_payloads.add((T)payload);
			}
		}
		return matched_payloads;
	}

	/**
	* @param reason.
	*/	
	public void setReason(Reason reason) {
		this.reason = reason;
	}

	/**
	* @return reason.
	*/	
	public Reason getReason() {
		return reason;
	}
}