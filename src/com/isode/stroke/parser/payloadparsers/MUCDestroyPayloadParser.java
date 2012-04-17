/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MUCDestroyPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.GenericPayloadTreeParser;
import com.isode.stroke.parser.tree.ParserElement;

/**
 * Class representing a parser for MUC Destroy payload
 *
 */
public class MUCDestroyPayloadParser extends GenericPayloadTreeParser<MUCDestroyPayload> {

    /**
     * Create the parser 
     */
    public MUCDestroyPayloadParser() {
        super(new MUCDestroyPayload());
    }

    @Override
    public void handleTree(ParserElement root) {
        String ns = root.getNamespace();
        String jid = root.getAttributes().getAttribute("jid");
        if (jid != null && !jid.isEmpty()) {
            getPayloadInternal().setNewVenue(new JID(jid));
        }
        getPayloadInternal().setReason(root.getChild("reason", ns).getText());
    }
}
