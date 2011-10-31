/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

public class Body extends Payload {
    String text_ = "";

    public Body() {}

    public Body(String text) {
        setText(text);
    }

    public void setText(String text) {
        text_ = text;
    }

    public String getText() {
        return text_;
    }
}
