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

import com.isode.stroke.elements.Payload;
import java.util.Vector;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.base.ByteArray;

public class IBB extends Payload {

	public enum Action {
		Open,
		Close,
		Data
	};
	
	public enum StanzaType {
		IQStanza,
		MessageStanza
	};

	private Action action;
	private String streamID = "";
	private ByteArray data = new ByteArray();
	private StanzaType stanzaType;
	private int blockSize;
	private int sequenceNumber;

	/**
	* Default Constructor.
	*/
	public IBB() {
		this(Action.Open, "");
	}

	/**
	* Parameterized Constructor
	* @param action, NotNull.
	*/
	public IBB(Action action) {
		this(action, "");
	}

	/**
	* Parameterized Constructor
	* @param streamID, NotNull.
	* @param action, NotNull.
	*/
	public IBB(Action action, String streamID) {
		NotNull.exceptIfNull(action, "action");
		NotNull.exceptIfNull(streamID, "streamID");
		this.action = action;
		this.streamID = streamID;
		this.stanzaType = StanzaType.IQStanza;
		this.blockSize = -1;
		this.sequenceNumber = -1;
	}

	/**
	* @param streamID, NotNull.
	* @param blockSize.
	* @return IBB Object.
	*/
	public static IBB createIBBOpen(String streamID, int blockSize) {
		NotNull.exceptIfNull(streamID, "streamID");
		IBB result = new IBB(Action.Open, streamID);
		result.setBlockSize(blockSize);
		return result;
	}

	/**
	* @param streamID, NotNull.
	* @param sequenceNumber.
	* @param data, NotNull.
	* @return IBB Object.
	*/
	public static IBB createIBBData(String streamID, int sequenceNumber, ByteArray data) {
		NotNull.exceptIfNull(streamID, "streamID");
		NotNull.exceptIfNull(data, "data");		
		IBB result = new IBB(Action.Data, streamID);
		result.setSequenceNumber(sequenceNumber);
		result.setData(data);
		return result;
	}


	/**
	* @param streamID, NotNull.
	* @return IBB Object.
	*/
	public static IBB createIBBClose(String streamID) {
		return new IBB(Action.Close, streamID);
	}

	/**
	* @param action, Not Null.
	*/
	public void setAction(Action action) {
		NotNull.exceptIfNull(action, "action");
		this.action = action;
	}

	/**
	* @return action, Not Null.
	*/
	public Action getAction() {
		return action;
	}

	/**
	* @param stanzaType, Not Null.
	*/
	public void setStanzaType(StanzaType stanzaType) {
		NotNull.exceptIfNull(stanzaType, "stanzaType");
		this.stanzaType = stanzaType;
	}

	/**
	* @return stanzaType, Not Null.
	*/
	public StanzaType getStanzaType() {
		return stanzaType;
	}

	/**
	* @param id, Not Null.
	*/
	public void setStreamID(String id) {
		NotNull.exceptIfNull(id, "id");
		streamID = id;
	}

	/**
	* @return id, Not Null.
	*/
	public String getStreamID() {
		return streamID;
	}

	/**
	* @return data, Not Null.
	*/
	public ByteArray getData() {
		return data;
	}

	/**
	* @param data, Not Null.
	*/
	public void setData(ByteArray data) {
		NotNull.exceptIfNull(data, "data");
		this.data = data;
	}

	/**
	* @return blockSize.
	*/
	public int getBlockSize() {
		return blockSize;
	}

	/**
	* @param blockSize.
	*/
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/**
	* @return sequenceNumber.
	*/
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	* @param sequenceNumber.
	*/
	public void setSequenceNumber(int i) {
		sequenceNumber = i;
	}
}