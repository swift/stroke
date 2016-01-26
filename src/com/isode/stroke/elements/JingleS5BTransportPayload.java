/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.elements;

import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.elements.Bytestreams;
import com.isode.stroke.network.HostAddressPort;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.NotNull;
import java.util.Vector;
import java.util.Comparator;

public class JingleS5BTransportPayload extends JingleTransportPayload {

	public enum Mode {
		TCPMode, // default case
		UDPMode
	};

	public static class Candidate {

		public enum Type {
			DirectType, // default case
			AssistedType,
			TunnelType,
			ProxyType
		};

		public String cid = "";
		public JID jid = new JID();
		public HostAddressPort hostPort;
		public int priority;
		public Type type;

		public Candidate() {
			this.priority = 0;
			this.type = Type.DirectType;
		}
	}

	public static class CompareCandidate implements Comparator<JingleS5BTransportPayload.Candidate> {
		public int compare(JingleS5BTransportPayload.Candidate c1, JingleS5BTransportPayload.Candidate c2) {
			if (c1.priority == c2.priority) { return 0; }
			else if (c1.priority < c2.priority) { return -1; }
			else  { return 1; }
		}
	}

	private Mode mode;
	private Vector<Candidate> candidates = new Vector<Candidate>();

	private String candidateUsedCID = "";
	private String activatedCID = "";
	private String dstAddr = "";
	private boolean candidateError;
	private boolean proxyError;

	public JingleS5BTransportPayload() {
		this.mode = Mode.TCPMode;
		this.candidateError = false;
		this.proxyError = false;
	}

	/**
	* @return mode, Not Null.
	*/
	public Mode getMode() {
		return mode;
	}

	/**
	* @param mode, Not Null.
	*/
	public void setMode(Mode mode) {
		NotNull.exceptIfNull(mode, "mode");
		this.mode = mode;
	}

	/**
	* @return candidates, Not Null.
	*/
	public Vector<Candidate> getCandidates() {
		return candidates;
	}

	/**
	* @param candidate, NotNull.
	*/
	public void addCandidate(Candidate candidate) {
		NotNull.exceptIfNull(candidate, "candidate");
		candidates.add(candidate);
	}

	/**
	* @param cid, NotNull.
	*/
	public void setCandidateUsed(String cid) {
		NotNull.exceptIfNull(cid, "cid");
		candidateUsedCID = cid;
	}

	/**
	* @return candidateUsedCID, Not Null.
	*/
	public String getCandidateUsed() {
		return candidateUsedCID;
	}

	/**
	* @param cid, NotNull.
	*/
	public void setActivated(String cid) {
		NotNull.exceptIfNull(cid, "cid");
		activatedCID = cid;
	}

	/**
	* @return activatedCID, Not Null.
	*/
	public String getActivated() {
		return activatedCID;
	}

	/**
	* @param addr, NotNull.
	*/
	public void setDstAddr(String addr) {
		NotNull.exceptIfNull(addr, "addr");
		dstAddr = addr;
	}

	/**
	* @return dstAddr, Not Null.
	*/
	public String getDstAddr() {
		return dstAddr;
	}

	/**
	* @param candidateError.
	*/
	public void setCandidateError(boolean hasError) {
		candidateError = hasError;
	}

	/**
	* @return candidateError.
	*/
	public boolean hasCandidateError() {
		return candidateError;
	}

	/**
	* @param proxyError.
	*/
	public void setProxyError(boolean hasError) {
		proxyError = hasError;
	}

	/**
	* @return proxyError.
	*/
	public boolean hasProxyError() {
		return proxyError;
	}
}