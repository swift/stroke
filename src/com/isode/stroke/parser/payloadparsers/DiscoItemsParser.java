/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.DiscoItems;
import com.isode.stroke.elements.DiscoItems.Item;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class DiscoItemsParser extends GenericPayloadParser<DiscoItems> {
    public DiscoItemsParser() {
        super(new DiscoItems());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == PayloadLevel) {
            if (element.equals("item")) {
                Item item = new Item(attributes.getAttribute("name"), new JID(attributes.getAttribute("jid")), attributes.getAttribute("node"));
                getPayloadInternal().addItem(item);
            }
        }
        else if (level_ == TopLevel) {
            if (element.equals("query")) {
                getPayloadInternal().setNode(attributes.getAttribute("node"));
            }
        }
        ++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
    }

    public void handleCharacterData(String data) {
    }
    private static final int TopLevel = 0;
    private static final int PayloadLevel = 1;
    private int level_ = TopLevel;
}
