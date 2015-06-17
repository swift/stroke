/*
 * Copyright (c) 2011 Jan Kaluza
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.RosterItemExchangePayload;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.jid.JID;

public class RosterItemExchangeParser extends GenericPayloadParser<RosterItemExchangePayload> {

	private final int TopLevel = 0;
	private final int PayloadLevel = 1;
	private final int ItemLevel = 2;
	private int level_ = 0;
	private boolean inItem_;
	private RosterItemExchangePayload.Item currentItem_;
	private String currentText_ = "";

	public RosterItemExchangeParser() {
		super(new RosterItemExchangePayload());
		this.level_ = TopLevel;
		this.inItem_ = false;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes, NotNull.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(attributes, "attributes");
		if (level_ == PayloadLevel) {
			if (element.equals("item")) {
				inItem_ = true;

				currentItem_ = new RosterItemExchangePayload.Item();

				currentItem_.setJID(new JID(attributes.getAttribute("jid")));
				currentItem_.setName(attributes.getAttribute("name"));

				String action = attributes.getAttribute("action");
				if (action.equals("add")) {
					currentItem_.setAction(RosterItemExchangePayload.Item.Action.Add);
				}
				else if (action.equals("modify")) {
					currentItem_.setAction(RosterItemExchangePayload.Item.Action.Modify);
				}
				else if (action.equals("delete")) {
					currentItem_.setAction(RosterItemExchangePayload.Item.Action.Delete);
				}
				else {
					// Add is default action according to XEP
					currentItem_.setAction(RosterItemExchangePayload.Item.Action.Add);
				}
			}
		}
		else if (level_ == ItemLevel) {
			if (element.equals("group")) {
				currentText_ = "";
			}
		}
		++level_;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {
		NotNull.exceptIfNull(element, "element");
		--level_;
		if (level_ == PayloadLevel) {
			if (inItem_) {
				getPayloadInternal().addItem(currentItem_);
				inItem_ = false;
			}
		}
		else if (level_ == ItemLevel) {
			if (element.equals("group")) {
				currentItem_.addGroup(currentText_);
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		currentText_ += data;
	}
}