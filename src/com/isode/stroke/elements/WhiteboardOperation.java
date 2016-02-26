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

public class WhiteboardOperation {

    private String id_ = "";
    private String parentID_ = "";
    private int pos_ = 0;
    
    public WhiteboardOperation() {
        // Empty Constructor
    }
    
    public WhiteboardOperation(WhiteboardOperation other) {
        this.id_ = other.id_;
        this.parentID_ = other.parentID_;
        this.pos_ = other.pos_;
    }
    
    public String getID() {
        return id_;
    }

    public void setID(String id) {
        id_ = id;
    }

    public String getParentID() {
        return parentID_;
    }

    public void setParentID(String parentID) {
        parentID_ = parentID;
    }

    public int getPos() {
        return pos_;
    }

    public void setPos(int pos) {
        pos_ = pos;
    }

}
