/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

public class ResourceBind extends Payload {
//FIXME: serializer and parser
    public void setJID(JID jid) {
        jid_ = jid;
    }

    public JID getJID() {
        return jid_;
    }

    public void setResource(String resource) {
        resource_ = resource;
    }

    public String getResource() {
        return resource_;
    }
    private JID jid_ = new JID();
    private String resource_ = "";
}
