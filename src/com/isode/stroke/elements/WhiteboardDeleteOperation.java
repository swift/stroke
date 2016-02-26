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

public class WhiteboardDeleteOperation extends WhiteboardOperation {

    private String elementID_ = "";
    
    public WhiteboardDeleteOperation() {
        // Empty Constructor
    }
    
    public WhiteboardDeleteOperation(WhiteboardDeleteOperation other) {
        super(other);
        this.elementID_ = other.elementID_;
    }
    
    public String getElementID() {
        return elementID_;
    }
    
    public void setElementID(String id) {
        elementID_ = id;
    }
    
}
