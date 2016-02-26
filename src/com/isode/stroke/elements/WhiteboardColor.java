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

public class WhiteboardColor {

    private final int red_, green_, blue_;
    private int alpha_;
    
    public WhiteboardColor() {
        this(0,0,0,255);
    }
    
    public WhiteboardColor(int red,int green,int blue) {
        this(red,green,blue,255);
    }
    
    public WhiteboardColor(int red,int green,int blue,int alpha) {
        red_ = red;
        green_ = green;
        blue_ = blue;
        alpha_ = alpha;
    }
    
    public WhiteboardColor(String hex) {
        alpha_ = 255;
        int value = Integer.parseInt(hex.substring(1));
        red_ = (value >> 16) & 0xFF;
        green_ = (value >> 8) & 0xFF;
        blue_ = value & 0xFF;
    }
    
    public String toHex() {
        int value = (red_ << 16) + (green_ << 8) + blue_;
        StringBuilder builder = new StringBuilder(Integer.toHexString(value));
        while (builder.length() < 6) {
            builder.insert(0, '0');
        }
        builder.insert(0, '#');
        return builder.toString();
    }

    public int getRed() {
        return red_;
    }

    public int getGreen() {
        return green_;
    }

    public int getBlue() {
        return blue_;
    }

    public int getAlpha() {
        return alpha_;
    }

    public void setAlpha(int alpha) {
        alpha_ = alpha;
    }
}
