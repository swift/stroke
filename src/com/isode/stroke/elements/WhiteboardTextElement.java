/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.elements;

public class WhiteboardTextElement extends WhiteboardElement {

    private final int x_, y_;
    private int size_;
    private String text_;
    private WhiteboardColor color_;

    public WhiteboardTextElement(int x, int y) {
        x_ = x;
        y_ = y;
    }
    
    public void setText(String text) {
        text_ = text;
    }

    public String getText() {
        return text_;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    public WhiteboardColor getColor() {
        return color_;
    }

    public void setColor(WhiteboardColor color) {
        color_ = color;
    }

    public int getSize() {
        return size_;
    }

    public void setSize(int size) {
        size_ = size;
    }
    
    @Override
    public void accept(WhiteboardElementVisitor visitor) {
        visitor.visit(this);
    }

}
