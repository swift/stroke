/*
 * Copyright (c) 2011-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.elements;

import java.util.Vector;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.jid.JID;

public class UnblockPayload extends AbstractBlockPayload {

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
	@Override
    public void addItem(JID item) {
		NotNull.exceptIfNull(item, "item");
		items.add(item);
	}

	/**
	* @return items, Not Null.
	*/
	@Override
    public Vector<JID> getItems() {
		return items;
	}
}