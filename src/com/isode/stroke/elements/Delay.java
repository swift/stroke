/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.elements;

import java.util.Date;
import com.isode.stroke.jid.JID;

public class Delay extends Payload {
    public Delay() {
    }
    
    public Delay(Date time, JID from) {
        time_ = time;
        from_ = from;
    }

    public Date getStamp() {
        return time_;
    }
    
    public void setStamp(Date time) {
        time_ = time;
    }

    public JID getFrom() {
        return from_;
    }
    
    public void setFrom(JID from) {
        from_ = from;
    }

    private Date time_;
    private JID from_;
}
