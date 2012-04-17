/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MUCAdminPayload;
import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.parser.GenericPayloadTreeParser;
import com.isode.stroke.parser.tree.ParserElement;

/**
 * Class representing a parser for MUC Admin payload
 *
 */
public class MUCAdminPayloadParser extends GenericPayloadTreeParser<MUCAdminPayload>{

    /**
     * Create the parser 
     */
    public MUCAdminPayloadParser() {
        super(new MUCAdminPayload());
    }

    @Override
    public void handleTree(ParserElement root) {
        for (ParserElement itemElement : root.getChildren("item", "http://jabber.org/protocol/muc#admin")) {
            MUCItem item = MUCItemParser.itemFromTree(itemElement);
            getPayloadInternal().addItem(item);
        }
    }
}
