/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.elements;

public class Forwarded extends Payload {
    public void setDelay(Delay delay) {
        delay_ = delay;
    }
    
    public Delay getDelay() {
        return delay_;
    }
    
    public void setStanza(Stanza stanza) {
        stanza_ = stanza;
    }
    
    public Stanza getStanza() {
        return stanza_;
    }
    
    private Delay delay_;
    private Stanza stanza_;
}
