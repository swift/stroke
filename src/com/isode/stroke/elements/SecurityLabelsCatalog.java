/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.util.ArrayList;
import java.util.Collection;

import com.isode.stroke.jid.JID;

public class SecurityLabelsCatalog extends Payload {
	private JID to_;
	private String name_ = "";
	private String description_ = "";
	private Collection<Item> items_ = new ArrayList<Item>();

	public static class Item {
		private SecurityLabel label_;
		private String selector_ = "";
		private boolean default_;

		public SecurityLabel getLabel() {
			return label_;
		}

		public void setLabel(SecurityLabel label) {
			label_ = label;
		}

		public final String getSelector() { return selector_; }

		public void setSelector(final String selector) {
			selector_ = selector;
		}

		public boolean getIsDefault() { return default_; }

		public void setIsDefault(boolean isDefault) {
			default_ = isDefault;
		}
	};
	
	public SecurityLabelsCatalog() {
		this(new JID());
	}

	public SecurityLabelsCatalog(final JID to) {
		to_ = to;
	}

	public final Collection<Item> getItems() {
		return items_;
	}

	public void addItem(final Item item) {
		items_.add(item);
	}

	public final JID getTo() {
		return to_;
	}

	public void setTo(final JID to) {
		to_ = to;
	}

	public final String getName() {
		return name_;
	}

	public void setName(final String name) {
		name_ = name;
	}

	public final String getDescription() {
		return description_;
	}

	public void setDescription(final String description) {
		description_ = description;
	}

}
