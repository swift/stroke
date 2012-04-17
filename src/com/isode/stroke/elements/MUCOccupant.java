/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

/**
 * Class representing a MUC Occupant
 *
 */
public class MUCOccupant {

    /**
     * MUC Role
     *
     */
    public enum Role {
        Moderator("moderator"),
        Participant("participant"),
        Visitor("visitor"),
        NoRole("none");

        Role(String name) {
            this.nodeName = name;
        }

        public String nodeName;
    };

    /**
     * MUC Affiliation of the user
     *
     */
    public enum Affiliation {
        Owner("owner"), 
        Admin("admin"),
        Member("member"),
        Outcast("outcast"),
        NoAffiliation("none");

        public String nodeName;

        Affiliation(String name) {
            this.nodeName = name;
        }
    };

    /**
     * Create the MUC Occupant object
     * @param nick nick name, not null
     * @param role MUC Role, not null
     * @param affiliation MUC Affiliation, not null
     */
    public MUCOccupant(String nick, Role role, Affiliation affiliation) {
        this.nick_ = nick;
        this.role_ = role;
        this.affiliation_ = affiliation;
    }

    /**
     * Create a copy of the given MUC Occupant
     * @param other object to copy from
     */
    public MUCOccupant(MUCOccupant other) {
        this.nick_ = other.nick_;
        this.role_ = other.role_;
        this.affiliation_ = other.affiliation_;
        this.realJID_ = new JID(other.realJID_ != null ? other.realJID_.toString() : "");
    }

    /**
     * Get the nick name
     * @return nick name, not null
     */
    public String getNick() {
        return nick_;
    }

    /**
     * Get the role
     * @return role, not null
     */
    public Role getRole(){
        return role_;
    }

    /**
     * Get the affiliation of the user
     * @return affiliation , not null
     */
    public Affiliation getAffiliation() {
        return affiliation_;
    }

    /**
     * Get the real Jabber ID of the user
     * @return real Jabber ID, not null if set
     */
    public JID getRealJID(){
        return realJID_;
    }

    /**
     * Set the real Jabber ID
     * @param jid Jabber ID, not null
     */
    public void setRealJID(JID jid) {
        this.realJID_ = jid;
    }

    /**
     * Set the nick name of the user
     * @param nick nick name, not null
     */
    public void setNick(String nick) {
        this.nick_ = nick;
    }

    private String nick_;
    private Role role_;
    private Affiliation affiliation_;
    private JID realJID_;
    /* If you add a field, remember to update the const copy constructor */
}
