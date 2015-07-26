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

package com.isode.stroke.client;

import com.isode.stroke.client.BlockList;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public class BlockListImpl extends BlockList {

	private State state;
	private Vector<JID> items = new Vector<JID>();

	public BlockListImpl() {
		this.state = State.Init;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		if (!(this.state.equals(state))) {
			this.state = state;
			onStateChanged.emit();
		}
	}

	public Vector<JID> getItems() {
		return items;
	}

	public void setItems(final Vector<JID> items) {
		for (final JID jid : this.items) {
			if(items.contains(jid)) {
				onItemRemoved.emit(jid);
			}
		}

		for (final JID jid : items) {
			if(this.items.contains(jid)) {
				onItemAdded.emit(jid);
			}
		}
		this.items = items;
	}

	public void addItem(final JID item) {
		if(!(items.contains(item))) {
			items.add(item);
			onItemAdded.emit(item);			
		}
	}

	public void removeItem(final JID item) {
		int oldSize = items.size();
		while(items.contains(item)) {
			items.remove(item);
		}
		if (items.size() != oldSize) {
			onItemRemoved.emit(item);
		}
	}

	public void addItems(final Vector<JID> items) {
		Vector<JID> itemsToAdd = new Vector<JID>(items); //Have to do this to avoid ConcurrentModificationException.
		for (final JID item : itemsToAdd) {
			addItem(item);
		}
	}

	public void removeItems(final Vector<JID> items) {
		Vector<JID> itemsToRemove = new Vector<JID>(items); //Have to do this to avoid ConcurrentModificationException.
		for (final JID item : itemsToRemove) {
			removeItem(item);
		}
	}

	public void removeAllItems() {
		removeItems(items);
	}
}