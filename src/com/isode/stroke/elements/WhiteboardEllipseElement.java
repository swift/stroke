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

public class WhiteboardEllipseElement extends WhiteboardElement {

    private final int cx_, cy_, rx_, ry_;
    private WhiteboardColor penColor_;
    private WhiteboardColor brushColor_;
    private int penWidth_;
    
    public WhiteboardEllipseElement(int cx, int cy, int rx, int ry) {
        cx_ = cx;
        cy_ = cy;
        rx_ = rx;
        ry_ = ry;
    }
    
    public int getCX() {
        return cx_;
    }

    public int getCY() {
        return cy_;
    }

    public int getRX() {
        return rx_;
    }

    public int getRY() {
        return ry_;
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
