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

public class WhiteboardRectElement extends WhiteboardElement {

    private int x_, y_, width_, height_;
    private WhiteboardColor penColor_;
    private WhiteboardColor brushColor_;
    private int penWidth_ = 1;
    
    public WhiteboardRectElement(int x, int y, int width, int height) {
        x_ = x;
        y_ = y;
        width_ = width;
        height_ = height;
    }
    
    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    public int getWidth() {
        return width_;
    }

    public int getHeight() {
        return height_;
    }

    public WhiteboardColor getPenColor() {
        return penColor_;
    }

    public void setPenColor(WhiteboardColor color) {
        penColor_ = color;
    }

    public WhiteboardColor getBrushColor() {
        return brushColor_;
    }

    public void setBrushColor(WhiteboardColor color) {
        brushColor_ = color;
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
