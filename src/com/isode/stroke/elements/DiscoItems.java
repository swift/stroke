/*
 * Copyright (c) 2010, Kevin Smith.
 * All rights reserved.
 */
/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.jid.JID;
import java.util.ArrayList;
import java.util.List;

/**
 *  Service discovery disco#items from XEP-0030.
 */
public class DiscoItems extends Payload {

    /**
     * A single result item.
     */
    public static class Item {

        /**
         * @param name Item name, not null.
         * @param jid Item JID, not null.
         */
        public Item(String name, JID jid) {
            this(name, jid, "");
        }

        /**
         * @param name Item name, not null.
         * @param jid Item JID, not null.
         * @param node Item node, not null.
         */
        public Item(String name, JID jid, String node) {
            NotNull.exceptIfNull(name, "name");
            NotNull.exceptIfNull(jid, "jid");
            NotNull.exceptIfNull(node, "node");
            name_ = name;
            jid_ = jid;
            node_ = node;
        }

        /**
         *
         * @return Item name, not null.
         */
        public String getName() {
            return name_;
        }

        /**
         * 
         * @return Item node, not null.
         */
        public String getNode() {
            return node_;
        }

        /**
         *
         * @return Item JID, not null.
         */
        public JID getJID() {
            return jid_;
        }
        private final String name_;
        private final JID jid_;
        private final String node_;
    };

    public DiscoItems() {
    }

    public String getNode() {
        return node_;
    }

    public void setNode(String node) {
        node_ = node;
    }

    public List<Item> getItems() {
        return new ArrayList(items_);
    }

    public void addItem(Item item) {
        items_.add(item);
    }
    private String node_;
    private final List<Item> items_ = new ArrayList<Item>();
}

