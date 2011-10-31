/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;
import java.util.ArrayList;
import java.util.List;

public class SearchPayload extends Payload {

    public static class Item {

        public String first;
        public String last;
        public String nick;
        public String email;
        public JID jid;
    };

    public SearchPayload() {
    }

    //Form::ref getForm() const { return form; } /* Not ported yet */
    //void setForm(Form::ref f) { form = f; } /* Not ported yet */
    /**
     * @return Can be null
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * @return Can be null
     */
    public String getNick() {
        return nick;
    }

    /**
     * @return Can be null
     */
    public String getFirst() {
        return first;
    }

    /**
     * @return Can be null
     */
    public String getLast() {
        return last;
    }

    /**
     * @return Can be null
     */
    public String getEMail() {
        return email;
    }

    /**
     * @param v Null means no value.
     */
    public void setInstructions(String v) {
        this.instructions = v;
    }

    /**
     * @param v Null means no value.
     */
    public void setNick(String v) {
        this.nick = v;
    }

    /**
     * @param v Null means no value.
     */
    public void setFirst(String v) {
        this.first = v;
    }

    /**
     * @param v Null means no value.
     */
    public void setLast(String v) {
        this.last = v;
    }

    /**
     * @param v Null means no value.
     */
    public void setEMail(String v) {
        this.email = v;
    }

    /**
     *
     * @return non-null
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     *
     * @param item Non-null.
     */
    public void addItem(Item item) {
        items.add(item);
    }

    //private	Form::ref form; /*Not ported yet*/
    private String instructions;
    private String nick;
    private String first;
    private String last;
    private String email;
    private ArrayList<Item> items = new ArrayList<Item>();
}
