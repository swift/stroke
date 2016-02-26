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

public class WhiteboardUpdateOperation extends WhiteboardOperation {
    
    private WhiteboardElement element_;
    private int newPos_ = 0;

    public WhiteboardUpdateOperation() {
        // Empty Constructor
    }
    
    public WhiteboardUpdateOperation(WhiteboardUpdateOperation other) {
        super(other);
        this.element_ = other.element_;
        this.newPos_ = other.newPos_;
    }
    
    public WhiteboardElement getElement() {
        return element_;
    }

    public void setElement(WhiteboardElement element) {
        element_ = element;
    }

    public int getNewPos() {
        return newPos_;
    }

    public void setNewPos(int newPos) {
        newPos_ = newPos;
    }

}
