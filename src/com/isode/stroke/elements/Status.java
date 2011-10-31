/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class Status extends Payload {

    String text_ = "";

    public Status() {
    }

    public Status(String text) {
        text_ = text;
    }

    public void setText(String text) {
        text_ = text;
    }

    public String getText() {
        return text_;
    }
}
