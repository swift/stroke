/*
 * Copyright (c) 2011-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MUCInvitationPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.GenericPayloadTreeParser;
import com.isode.stroke.parser.tree.NullParserElement;
import com.isode.stroke.parser.tree.ParserElement;

/**
 * Class representing a parser for MUC Invitation payload
 *
 */
public class MUCInvitationPayloadParser extends GenericPayloadTreeParser<MUCInvitationPayload> {
    
    public MUCInvitationPayloadParser() {
        super(new MUCInvitationPayload());
    }

    @Override
    public void handleTree(ParserElement root) {
        MUCInvitationPayload invite = getPayloadInternal();
        invite.setIsContinuation(root.getAttributes().getBoolAttribute("continue", false));
        invite.setJID(new JID(root.getAttributes().getAttribute("jid")));
        invite.setPassword(root.getAttributes().getAttribute("password"));
        invite.setReason(root.getAttributes().getAttribute("reason"));
        invite.setThread(root.getAttributes().getAttribute("thread"));
        ParserElement impromptuNode = root.getChild("impromptu", "http://swift.im/impromptu");
        invite.setIsImpromptu(!(impromptuNode instanceof NullParserElement));
    }
}
