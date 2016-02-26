/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.serializer.payloadserializers;

import java.util.Iterator;
import java.util.logging.Logger;

import com.isode.stroke.elements.WhiteboardDeleteOperation;
import com.isode.stroke.elements.WhiteboardElementVisitor;
import com.isode.stroke.elements.WhiteboardEllipseElement;
import com.isode.stroke.elements.WhiteboardFreehandPathElement;
import com.isode.stroke.elements.WhiteboardInsertOperation;
import com.isode.stroke.elements.WhiteboardLineElement;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardPayload;
import com.isode.stroke.elements.WhiteboardPayload.Type;
import com.isode.stroke.elements.WhiteboardPolygonElement;
import com.isode.stroke.elements.WhiteboardRectElement;
import com.isode.stroke.elements.WhiteboardTextElement;
import com.isode.stroke.elements.WhiteboardUpdateOperation;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;

public class WhiteboardSerializer extends GenericPayloadSerializer<WhiteboardPayload> {

    private static class WhiteboardElementSerializingVisitor implements WhiteboardElementVisitor {

        private XMLElement element;
        
        @Override
        public void visit(WhiteboardLineElement line) {
            element = new XMLElement("line");
            element.setAttribute("x1", String.valueOf(line.x1()));
            element.setAttribute("y1", String.valueOf(line.y1()));
            element.setAttribute("x2", String.valueOf(line.x2()));
            element.setAttribute("y2", String.valueOf(line.y2()));
            element.setAttribute("id", line.getID());
            element.setAttribute("stroke", line.getColor().toHex());
            element.setAttribute("stroke-width", String.valueOf(line.getPenWidth()));
            element.setAttribute("opacity", alphaToOpacity(line.getColor().getAlpha()));
        }

        @Override
        public void visit(WhiteboardFreehandPathElement path) {
            element = new XMLElement("path");
            element.setAttribute("id", path.getID());
            element.setAttribute("stroke", path.getColor().toHex());
            element.setAttribute("stroke-width", String.valueOf(path.getPenWidth()));
            element.setAttribute("opacity", alphaToOpacity(path.getColor().getAlpha()));
            StringBuilder pathDataBuilder = new StringBuilder();
            if (!path.getPoints().isEmpty()) {
                Iterator<WhiteboardFreehandPathElement.Point> it = path.getPoints().iterator();
                WhiteboardFreehandPathElement.Point point = it.next();
                pathDataBuilder.append('M');
                pathDataBuilder.append(point.x);
                pathDataBuilder.append(' ');
                pathDataBuilder.append(point.y);
                pathDataBuilder.append('L');
                while (it.hasNext()) {
                    point = it.next();
                    pathDataBuilder.append(point.x);
                    pathDataBuilder.append(' ');
                    pathDataBuilder.append(point.y);
                    pathDataBuilder.append(' ');
                }
            }
            element.setAttribute("d", pathDataBuilder.toString());
        }

        @Override
        public void visit(WhiteboardRectElement rect) {
            element = new XMLElement("rect");
            element.setAttribute("x", String.valueOf(rect.getX()));
            element.setAttribute("y", String.valueOf(rect.getY()));
            element.setAttribute("width", String.valueOf(rect.getWidth()));
            element.setAttribute("height", String.valueOf(rect.getHeight()));
            element.setAttribute("id", rect.getID());
            element.setAttribute("stroke", rect.getPenColor().toHex());
            element.setAttribute("fill", rect.getBrushColor().toHex());;
            element.setAttribute("stroke-width", String.valueOf(rect.getPenWidth()));
            element.setAttribute("opacity", alphaToOpacity(rect.getPenColor().getAlpha()));
            element.setAttribute("fill-opacity", alphaToOpacity(rect.getBrushColor().getAlpha()));
        }

        @Override
        public void visit(WhiteboardPolygonElement polygon) {
            element = new XMLElement("polygon");
            element.setAttribute("id", polygon.getID());
            element.setAttribute("stroke", polygon.getPenColor().toHex());
            element.setAttribute("fill", polygon.getBrushColor().toHex());;
            element.setAttribute("stroke-width", String.valueOf(polygon.getPenWidth()));
            element.setAttribute("opacity", alphaToOpacity(polygon.getPenColor().getAlpha()));
            element.setAttribute("fill-opacity", alphaToOpacity(polygon.getBrushColor().getAlpha()));
            StringBuilder points = new StringBuilder();
            for (WhiteboardPolygonElement.Point point : polygon.getPoints()) {
                points.append(point.x);
                points.append(',');
                points.append(point.y);
                points.append(' ');
            }
            element.setAttribute("points", points.toString());

        }

        @Override
        public void visit(WhiteboardTextElement text) {
            element = new XMLElement("text");
            element.setAttribute("x", String.valueOf(text.getX()));
            element.setAttribute("y", String.valueOf(text.getY()));
            element.setAttribute("font-size", String.valueOf(text.getSize()));
            element.setAttribute("id", text.getID());
            element.setAttribute("fill", text.getColor().toHex());
            element.setAttribute("opacity", alphaToOpacity(text.getColor().getAlpha()));
            element.addNode(new XMLTextNode(text.getText()));

        }

        @Override
        public void visit(WhiteboardEllipseElement ellipse) {
            element = new XMLElement("ellipse");
            element.setAttribute("cx", String.valueOf(ellipse.getCX()));
            element.setAttribute("cy", String.valueOf(ellipse.getCY()));
            element.setAttribute("rx", String.valueOf(ellipse.getRX()));
            element.setAttribute("ry", String.valueOf(ellipse.getRY()));
            element.setAttribute("id", ellipse.getID());
            element.setAttribute("stroke", ellipse.getPenColor().toHex());
            element.setAttribute("fill", ellipse.getBrushColor().toHex());;
            element.setAttribute("stroke-width", String.valueOf(ellipse.getPenWidth()));
            element.setAttribute("opacity", alphaToOpacity(ellipse.getPenColor().getAlpha()));
            element.setAttribute("fill-opacity", alphaToOpacity(ellipse.getBrushColor().getAlpha()));
        }
        
        public XMLElement getResult() {
            return element;
        }
        
        private String alphaToOpacity(int alpha) {
            int opacity = 100*alpha/254;
            if (opacity == 100) {
                return "1";
            } else {
                return String.format(".%d", opacity);
            }
        }
        
    }
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    public WhiteboardSerializer() {
        super(WhiteboardPayload.class);
    }

    @Override
    protected String serializePayload(WhiteboardPayload payload) {
        XMLElement element = new XMLElement("wb","http://swift.im/whiteboard");
        if (payload.getType() == Type.Data) {
            XMLElement operationNode = new XMLElement("operation");
            WhiteboardElementSerializingVisitor visitor = new WhiteboardElementSerializingVisitor();
            WhiteboardOperation operation = payload.getOperation();
            if (operation instanceof WhiteboardInsertOperation) {
                WhiteboardInsertOperation insertOp = (WhiteboardInsertOperation) operation;
                operationNode.setAttribute("type", "insert");
                operationNode.setAttribute("pos", String.valueOf(insertOp.getPos()));
                operationNode.setAttribute("id", insertOp.getID());
                operationNode.setAttribute("parentid", insertOp.getParentID());
                insertOp.getElement().accept(visitor);
                operationNode.addNode(operationNode);
            }
            if (operation instanceof WhiteboardUpdateOperation) {
                WhiteboardUpdateOperation updateOp = (WhiteboardUpdateOperation) operation;
                operationNode.setAttribute("type", "update");
                operationNode.setAttribute("pos", String.valueOf(updateOp.getPos()));
                operationNode.setAttribute("id", updateOp.getID());
                operationNode.setAttribute("parentid", updateOp.getParentID());
                operationNode.setAttribute("newpos", String.valueOf(updateOp.getNewPos()));
                updateOp.getElement().accept(visitor);
                operationNode.addNode(visitor.getResult());
            }
            if (operation instanceof WhiteboardDeleteOperation) {
                WhiteboardDeleteOperation deleteOp = new WhiteboardDeleteOperation();
                operationNode.setAttribute("type", "delete");
                operationNode.setAttribute("pos", String.valueOf(deleteOp.getPos()));
                operationNode.setAttribute("id", deleteOp.getID());
                operationNode.setAttribute("parentid", deleteOp.getParentID());
                operationNode.setAttribute("elementid", deleteOp.getElementID());
            }
            element.addNode(operationNode);
        }
        element.setAttribute("type", typeToString(payload.getType()));
        return element.serialize();
    }
    
    private String typeToString(Type type) {
        switch (type) {
        case Data:
            return "data";
        case SessionAccept:
            return "session-accept";
        case SessionRequest:
            return "session-request";
        case SessionTerminate:
            return "session-terminate";
        case UnknownType:
            logger.warning("Warning: Serializing unknown action value.");
            return "";
        default:
            assert(false);
            return "";
        }
    }

}
