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

public class WhiteboardInsertOperation extends WhiteboardOperation {

    private WhiteboardElement element_;
    
    public WhiteboardInsertOperation() {
        // Empty Constructor
    }
    
    public WhiteboardInsertOperation(WhiteboardInsertOperation other) {
        super(other);
        this.element_ = other.element_;
    }
    
    public WhiteboardElement getElement() {
        return element_;
    }

    public void setElement(WhiteboardElement element) {
        element_ = element;
    }

}
