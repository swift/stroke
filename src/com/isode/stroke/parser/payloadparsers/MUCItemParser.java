/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCOccupant.Affiliation;
import com.isode.stroke.elements.MUCOccupant.Role;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.tree.ParserElement;

/**
 * Class containing parser functions for MUC Item
 *
 */
public class MUCItemParser {

    /**
     * Get the MUC Item from the node
     * @param root XML node element
     * @return MUC Item, not null
     */
    public static MUCItem itemFromTree(ParserElement root) {
        MUCItem item = new MUCItem();
        String affiliation = root.getAttributes().getAttribute("affiliation");
        String role = root.getAttributes().getAttribute("role");
        String nick = root.getAttributes().getAttribute("nick");
        String jid = root.getAttributes().getAttribute("jid");
        item.affiliation = parseAffiliation(affiliation);
        item.role = parseRole(role);
        if (!jid.isEmpty()) {
            item.realJID = new JID(jid);
        }
        if (!nick.isEmpty()) {
            item.nick = nick;
        }
        String xmlns = root.getNamespace();
        String reason = root.getChild("reason", xmlns).getText();        
        String actor = root.getChild("actor", xmlns).getAttributes().getAttribute("jid");
        if (!reason.isEmpty()) {
            item.reason = reason;
        }
        if (!actor.isEmpty()) {
            item.actor = new JID(actor);
        }

        return item;
    }

    private static Role parseRole(String val) {
        for(Role role : Role.values()) {
            if(role.nodeName.equals(val)) {
                return role;
            }
        }
        return null;
    }

    private static Affiliation parseAffiliation(String val) {
        for(Affiliation aff : Affiliation.values()) {
            if(aff.nodeName.equals(val)) {
                return aff;
            }
        }
        return null;
    }
}
