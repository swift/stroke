/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCUserPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.GenericPayloadTreeParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.tree.ParserElement;
import com.isode.stroke.parser.tree.TreeReparser;

/**
 * Class representing a parser for MUC User payload
 *
 */
public class MUCUserPayloadParser extends GenericPayloadTreeParser<MUCUserPayload> {

    private PayloadParserFactoryCollection factories;

    /**
     * Create the parser
     * @param collection reference to payload parser factory collection, not null
     */
    public MUCUserPayloadParser(PayloadParserFactoryCollection collection){
        super(new MUCUserPayload());
        this.factories = collection;
    }

    @Override
    public void handleTree(ParserElement root) {
        for (ParserElement child : root.getAllChildren()) {            
            if ("item".equals(child.getName()) && child.getNamespace().equals(root.getNamespace())) {
                MUCItem item = MUCItemParser.itemFromTree(child);
                getPayloadInternal().addItem(item);
            } else if ("password".equals(child.getName()) && child.getNamespace().equals(root.getNamespace())) {
                getPayloadInternal().setPassword(child.getText());
            } else if ("invite".equals(child.getName()) && child.getNamespace().equals(root.getNamespace())) {
                MUCUserPayload.Invite invite = new MUCUserPayload.Invite();
                String to = child.getAttributes().getAttribute("to");
                if (to != null && !to.isEmpty()) {
                    invite.to = JID.fromString(to);
                }
                String from = child.getAttributes().getAttribute("from");
                if (from != null && !from.isEmpty()) {
                    invite.from = JID.fromString(from);
                }
                ParserElement reason = child.getChild("reason", root.getNamespace());
                if (reason != null) {
                    invite.reason = reason.getText();
                }
                getPayloadInternal().setInvite(invite);
            } else if ("status".equals(child.getName()) && child.getNamespace().equals(root.getNamespace())) {
                MUCUserPayload.StatusCode status = new MUCUserPayload.StatusCode();
                try {
                    status.code = Integer.parseInt(child.getAttributes().getAttribute("code"));
                    getPayloadInternal().addStatusCode(status);
                }catch (NumberFormatException e) {
                }
            } else {
                getPayloadInternal().setPayload(TreeReparser.parseTree(child, factories));
            }
        }
    }    
}
