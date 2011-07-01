/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

public class Subject extends Payload {
    String text_ = "";

    public Subject() {}

    public Subject(String text) {
        setText(text);
    }

    public void setText(String text) {
        text_ = text;
    }

    public String getText() {
        return text_;
    }
}
