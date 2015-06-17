/*
 * Copyright (c) 2011 Jan Kaluza
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
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

public class RosterItemExchangePayload extends Payload {

	public static class Item {

		public enum Action { 
			Add, Modify, Delete 
		};

		private Action action;
		private JID jid = new JID();
		private String name = "";
		private Vector<String> groups = new Vector<String>();

		/**
		* Default Constructor.
		*/
		public Item() {
			this(Item.Action.Add);
		}

		/**
		* Parameterized Constructor.
		* @param action, Not Null.
		*/
		public Item(Action action) {
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
		* @param action, Not Null.
		*/
		public void setAction(Action action) {
			NotNull.exceptIfNull(action, "action");
			this.action = action;
		}

		/**
		* @return jid, Not Null.
		*/
		public JID getJID() {
			return jid;
		}

		/**
		* @param jid, Not Null.
		*/
		public void setJID(JID jid) {
			NotNull.exceptIfNull(jid, "jid");
			this.jid = jid;
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
		* @return groups, Not Null.
		*/
		public Vector<String> getGroups() {
			return groups;
		}

		/**
		* @param groups, Not Null.
		*/
		public void setGroups(Vector<String> groups) {
			NotNull.exceptIfNull(groups, "groups");
			this.groups = groups;
		}

		/**
		* @param group, Not Null.
		*/
		public void addGroup(String group) {
			NotNull.exceptIfNull(group, "group");
			groups.add(group);
		}
	}

	private Vector<RosterItemExchangePayload.Item> items_ = new Vector<RosterItemExchangePayload.Item>();

	public RosterItemExchangePayload() {

	}

	/**
	* @param item, Not Null.
	*/
	public void addItem(RosterItemExchangePayload.Item item) {
		NotNull.exceptIfNull(item, "item");
		items_.add(item);
	}

	/**
	* @return items, Not Null.
	*/
	public Vector<RosterItemExchangePayload.Item> getItems() {
		return items_;
	}
}
