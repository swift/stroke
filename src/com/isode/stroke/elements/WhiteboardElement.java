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

public abstract class WhiteboardElement {

    private String id_ = "";
    
    public WhiteboardElement() {
        // Empty Constructor
    }
    
    public abstract void accept(WhiteboardElementVisitor visitor);
    
    public final String getID() {
        return id_;
    }
    
    public final void setID(String id) {
        id_ = id;
    }

}
