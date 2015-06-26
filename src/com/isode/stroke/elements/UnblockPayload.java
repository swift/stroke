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

import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.NotNull;
import java.util.Vector;

public class UnblockPayload extends Payload {

	private Vector<JID> items = new Vector<JID>();

	public UnblockPayload() {

	}

	/**
	* Parameterized Constructor.
	* @param items, Not NUll.
	*/
	public UnblockPayload(Vector<JID> items) {
		NotNull.exceptIfNull(items, "items");
		this.items = items;
	}

	/**
	* @param item, Not Null.
	*/
	public void addItem(JID item) {
		NotNull.exceptIfNull(item, "item");
		items.add(item);
	}

	/**
	* @return items, Not Null.
	*/
	public Vector<JID> getItems() {
		return items;
	}
}