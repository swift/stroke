/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

public class MAMArchived extends Payload {
    public void setBy(JID by) {
        by_ = by;
    }
    
    public JID getBy() {
        return by_;
    }

    public void setID(String id) {
        id_ = id;
    }

    public String getID() {
        return id_;
    }
            
    private JID by_;
    private String id_;
}
