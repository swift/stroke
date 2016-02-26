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

public interface WhiteboardElementVisitor {

    public void visit(WhiteboardLineElement element);
    public void visit(WhiteboardFreehandPathElement element);
    public void visit(WhiteboardRectElement element);
    public void visit(WhiteboardPolygonElement element);
    public void visit(WhiteboardTextElement element);
    public void visit(WhiteboardEllipseElement element);
    
}
