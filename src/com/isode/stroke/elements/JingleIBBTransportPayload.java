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

import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.base.NotNull;

public class JingleIBBTransportPayload extends JingleTransportPayload {

	public enum StanzaType {
		IQStanza,
		MessageStanza
	};

	private Integer blockSize;
	private StanzaType stanzaType;

	/**
	* Default Constructor.
	*/
	public JingleIBBTransportPayload() {
		this.stanzaType = StanzaType.IQStanza;
	}

	/**
	* @param stanzaType, NotNull.
	*/
	public void setStanzaType(StanzaType stanzaType) {
		NotNull.exceptIfNull(stanzaType, "stanzaType");
		this.stanzaType = stanzaType;
	}

	/**
	* @return stanzaType, NotNull.
	*/
	public StanzaType getStanzaType() {
		return stanzaType;
	}

	/**
	* @return blockSize.
	*/
	public Integer getBlockSize() {
		return blockSize;
	}

	/**
	* @param blockSize.
	*/
	public void setBlockSize(Integer blockSize) {
		this.blockSize = blockSize;
	}
}