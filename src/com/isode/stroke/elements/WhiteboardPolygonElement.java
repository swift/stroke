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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhiteboardPolygonElement extends WhiteboardElement {
    
    private final List<Point> points_ = new ArrayList<Point>();
    private WhiteboardColor penColor_;
    private WhiteboardColor brushColor_;
    private int penWidth_ = 0;
    
    public List<Point> getPoints() {
        return new ArrayList<Point>(points_);
    }

    public void setPoints(Collection<? extends Point> points) {
        points_.clear();
        points_.addAll(points);
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
    
    public static class Point {
        public final int x;
        public final int y;
        public Point(int x,int y) {
            this.x = x;
            this.y = y;
        }
    }

}
