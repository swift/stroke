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

public class WhiteboardLineElement extends WhiteboardElement {

    private final int x1_, x2_, y1_, y2_;
    
    private WhiteboardColor color_ = new WhiteboardColor();
    
    private int penWidth_ = 1;

    public WhiteboardLineElement(int x1,int y1,int x2,int y2) {
        x1_ = x1;
        y1_ = y1;
        x2_ = x2;
        y2_ = y2;
    }
    
    public int x1() {
        return x1_;
    }
    
    public int x2() {
        return x2_;
    }
    
    public int y1() {
        return y1_;
    }
    
    public int y2() {
        return y2_;
    }
    
    public WhiteboardColor getColor() {
        return color_;
    }
    
    public void setColor(WhiteboardColor color) {
        color_ = color;
    }
    
    public int getPenWidth() {
        return penWidth_;
    }
    
    public void setPenWidth(int penWidth) {
        penWidth_ = penWidth;
    }

    @Override
    public void accept(WhiteboardElementVisitor visitor) {
        visitor.visit(this);
    }

}
