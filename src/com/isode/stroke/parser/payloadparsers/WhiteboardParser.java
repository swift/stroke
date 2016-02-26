/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.parser.payloadparsers;

import java.util.ArrayList;
import java.util.List;

import com.isode.stroke.elements.WhiteboardColor;
import com.isode.stroke.elements.WhiteboardDeleteOperation;
import com.isode.stroke.elements.WhiteboardElement;
import com.isode.stroke.elements.WhiteboardEllipseElement;
import com.isode.stroke.elements.WhiteboardFreehandPathElement;
import com.isode.stroke.elements.WhiteboardInsertOperation;
import com.isode.stroke.elements.WhiteboardLineElement;
import com.isode.stroke.elements.WhiteboardOperation;
import com.isode.stroke.elements.WhiteboardPayload;
import com.isode.stroke.elements.WhiteboardPolygonElement;
import com.isode.stroke.elements.WhiteboardRectElement;
import com.isode.stroke.elements.WhiteboardUpdateOperation;
import com.isode.stroke.elements.WhiteboardPayload.Type;
import com.isode.stroke.elements.WhiteboardTextElement;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class WhiteboardParser extends GenericPayloadParser<WhiteboardPayload> {
    
    private boolean actualIsText;
    private int level_;
    private String data_;
    private WhiteboardElement wbElement;
    private WhiteboardOperation operation;

    public WhiteboardParser() {
        super(new WhiteboardPayload());
    }

    @Override
    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        if (level_ == 0) {
            getPayloadInternal().setType(stringToType(getAttributeOr(attributes, "type", "")));
        } 
        else if (level_ == 1) {
            String type = getAttributeOr(attributes, "type", "");
            if (type.equals("insert")) {
                operation = new WhiteboardInsertOperation();
            }
            else if (type.equals("update")) {
                WhiteboardUpdateOperation updateOp = new WhiteboardUpdateOperation();
                String move = getAttributeOr(attributes, "newpos", "0");
                updateOp.setNewPos(Integer.parseInt(move));
                operation = updateOp;
            }
            else if (type.equals("delete")) {
                WhiteboardDeleteOperation deleteOp = new WhiteboardDeleteOperation();
                deleteOp.setElementID(getAttributeOr(attributes, "elementid", ""));
                operation = deleteOp;
            }
            if (operation != null) {
                operation.setID(getAttributeOr(attributes, "id", ""));
                operation.setParentID(getAttributeOr(attributes, "parentid", ""));
                try {
                    operation.setPos(getIntAttribute(attributes, "pos", 0));
                } catch (NumberFormatException e) {
                    // Dont set pos
                }
            }

        } 
        else if (level_ == 2) {
            if ("line".equals(element)) {
                int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                try {
                    x1 = getIntAttribute(attributes, "x1", 0);
                    y1 = getIntAttribute(attributes, "y1", 0);
                    x2 = getIntAttribute(attributes, "x2", 0);
                    y2 = getIntAttribute(attributes, "y2", 0);
                } catch (NumberFormatException e) {
                }
                WhiteboardLineElement whiteboardElement = new WhiteboardLineElement(x1, y1, x2, y2);

                WhiteboardColor color = new WhiteboardColor(getAttributeOr(attributes, "stroke", "#000000"));
                color.setAlpha(opacityToAlpha(getAttributeOr(attributes, "opacity", "1")));
                whiteboardElement.setColor(color);

                int penWidth = 1;
                try {
                    penWidth = getIntAttribute(attributes, "stroke-width", 1);
                } catch (NumberFormatException e) {
                    // Empty Catch
                }

                whiteboardElement.setPenWidth(penWidth);
                whiteboardElement.setID(getAttributeOr(attributes,"id",""));
                getPayloadInternal().setElement(whiteboardElement);
                wbElement = whiteboardElement;
            }
            else if ("path".equals(element)) {
                WhiteboardFreehandPathElement whiteboardElement = new WhiteboardFreehandPathElement();
                String pathData = getAttributeOr(attributes, "d", "");
                List<WhiteboardFreehandPathElement.Point> points =
                        new ArrayList<WhiteboardFreehandPathElement.Point>();
                if (!pathData.isEmpty() && pathData.charAt(0) == 'M') {
                    try {
                        int pos = 1, npos;
                        int x, y;
                        if (pathData.charAt(pos) == ' ') {
                            pos++;
                        }
                        npos = pathData.indexOf(' ',pos);
                        x = Integer.parseInt(pathData.substring(pos, npos));
                        pos = npos+1;
                        npos = pathData.indexOf('L',pos);
                        y = Integer.parseInt(pathData.substring(pos,npos));
                        pos = npos+1;
                        if (pos < pathData.length() && pathData.charAt(pos) == ' ') {
                            pos++;
                        }
                        points.add(new WhiteboardFreehandPathElement.Point(x,y));
                        while (pos < pathData.length()) {
                            npos = pathData.indexOf(' ',pos);
                            x = Integer.parseInt(pathData.substring(pos, npos));
                            pos = npos+1;
                            npos = pathData.indexOf(' ',pos);
                            y = Integer.parseInt(pathData.substring(pos, npos));
                            pos = npos+1;
                            points.add(new WhiteboardFreehandPathElement.Point(x,y));
                        }
                    } 
                    catch (NumberFormatException e) {
                        // Empty catch
                    }
                }
                whiteboardElement.setPoints(points);

                int penWidth = 1;
                try {
                    penWidth = getIntAttribute(attributes, "stroke-width", 1);
                } catch (NumberFormatException e) {
                    // Empty Catch
                }
                whiteboardElement.setPenWidth(penWidth);
                
                WhiteboardColor color = new WhiteboardColor(getAttributeOr(attributes, "stroke", "#000000"));
                color.setAlpha(opacityToAlpha(getAttributeOr(attributes, "opacity", "1")));
                whiteboardElement.setColor(color);
                whiteboardElement.setID(getAttributeOr(attributes,"id",""));
                getPayloadInternal().setElement(whiteboardElement);
                wbElement = whiteboardElement;
            }
            else if ("rect".equals(element)) {
                int x = 0, y = 0, width = 0, height = 0;
                try {
                    x = getIntAttribute(attributes, "x", 0);
                    y = getIntAttribute(attributes, "y", 0);
                    width = getIntAttribute(attributes, "width", 0);
                    height = getIntAttribute(attributes, "height", 0);
                } catch (Exception e) {
                    // Empty Catch
                }
                
                WhiteboardRectElement whiteboardElement = new WhiteboardRectElement(x,y,width,height);
                
                int penWidth = 1;
                try {
                    penWidth = getIntAttribute(attributes, "stroke-width", 1);
                } catch (NumberFormatException e) {
                    // Empty Catch Block
                }
                whiteboardElement.setPenWidth(penWidth);
                
                WhiteboardColor penColor = new WhiteboardColor(getAttributeOr(attributes, "stroke", "#000000"));
                WhiteboardColor brushColor = new WhiteboardColor(getAttributeOr(attributes, "fill", "#000000"));
                penColor.setAlpha(opacityToAlpha(getAttributeOr(attributes, "opacity", "1")));
                brushColor.setAlpha(opacityToAlpha(getAttributeOr(attributes, "fill-opacity", "1")));
                whiteboardElement.setPenColor(penColor);
                whiteboardElement.setBrushColor(brushColor);;
                whiteboardElement.setID(getAttributeOr(attributes, "id", ""));
                getPayloadInternal().setElement(whiteboardElement);
                wbElement = whiteboardElement;
            }
            else if ("polygon".equals(element)) {
                WhiteboardPolygonElement whiteboardElement = new WhiteboardPolygonElement();
                
                String pointsData = getAttributeOr(attributes, "points", "");
                List<WhiteboardPolygonElement.Point> points = new ArrayList<WhiteboardPolygonElement.Point>();
                int pos = 0;
                int npos;
                int x,y;
                try {
                    while (pos < pointsData.length()) {
                        npos = pointsData.indexOf(',', pos);
                        if (npos == -1) {
                            break;
                        }
                        x = Integer.parseInt(pointsData.substring(pos, npos));
                        pos = npos+1;
                        npos = pointsData.indexOf(' ',pos);
                        if (npos == -1) {
                            npos = pointsData.length();
                        }
                        y = Integer.parseInt(pointsData.substring(pos,npos));
                        pos = npos+1;
                        points.add(new WhiteboardPolygonElement.Point(x,y));
                    }
                } catch (NumberFormatException e) {
                    // Empty catch 
                }
                
                whiteboardElement.setPoints(points);
                
                int penWidth = 0;
                try {
                    penWidth = getIntAttribute(attributes, "stroke-width", 1);
                } catch (Exception e) {
                    // Empty catch
                }
                
                WhiteboardColor penColor = new WhiteboardColor(getAttributeOr(attributes, "stroke", "#000000"));
                WhiteboardColor brushColor = new WhiteboardColor(getAttributeOr(attributes, "fill", "#000000"));
                penColor.setAlpha(opacityToAlpha(getAttributeOr(attributes, "opacity", "1")));
                brushColor.setAlpha(opacityToAlpha(getAttributeOr(attributes, "fill-opacity", "1")));
                
                whiteboardElement.setPenColor(penColor);
                whiteboardElement.setBrushColor(brushColor);
                whiteboardElement.setID(getAttributeOr(attributes, "id", ""));
                getPayloadInternal().setElement(whiteboardElement);
                wbElement = whiteboardElement;
            }
            else if ("text".equals(element)) {
                int x = 0, y = 0;
                try {
                    x = getIntAttribute(attributes, "x", 0);
                    y = getIntAttribute(attributes, "y", 0);
                } catch (NumberFormatException e) {
                    // Empty Catch
                }
                
                WhiteboardTextElement whiteboardElement = new WhiteboardTextElement(x, y);
                
                actualIsText = true;
                WhiteboardColor color = new WhiteboardColor(getAttributeOr(attributes, "fill", "#000000"));
                color.setAlpha(opacityToAlpha(getAttributeOr(attributes, "opacity", "1")));
                whiteboardElement.setColor(color);
                
                int fontSize = 1;
                try {
                    fontSize = getIntAttribute(attributes, "font-size", 12);
                } catch (NumberFormatException e) {
                   // Empty Catch
                }
                
                whiteboardElement.setSize(fontSize);
                whiteboardElement.setID(getAttributeOr(attributes, "id", ""));
                getPayloadInternal().setElement(whiteboardElement);
                wbElement = whiteboardElement;
            }
            else if ("ellipse".equals(element)) {
                int cx = 0, cy = 0, rx = 0, ry = 0;
                try {
                    cx = getIntAttribute(attributes, "cx", 0);
                    cy = getIntAttribute(attributes, "cy", 0);
                    rx = getIntAttribute(attributes, "rx", 0);
                    ry = getIntAttribute(attributes, "ry", 0);
                } catch (NumberFormatException e) {
                    // Empty Catch
                }
                
                WhiteboardEllipseElement whiteboardElement = new WhiteboardEllipseElement(cx, cy, rx, ry);
                
                int penWidth = 1;
                try {
                    penWidth = getIntAttribute(attributes, "stroke-width", 1);
                } catch (NumberFormatException e) {
                    // Empty Catch
                }
                whiteboardElement.setPenWidth(penWidth);
                
                WhiteboardColor penColor = new WhiteboardColor(getAttributeOr(attributes, "stroke", "#000000"));
                WhiteboardColor brushColor  = new WhiteboardColor(getAttributeOr(attributes,"fill","#000000"));
                penColor.setAlpha(opacityToAlpha(getAttributeOr(attributes, "opacity", "1")));
                brushColor.setAlpha(opacityToAlpha(getAttributeOr(attributes, "fill-opacity", "1")));
                whiteboardElement.setPenColor(penColor);
                whiteboardElement.setBrushColor(brushColor);
                whiteboardElement.setID(getAttributeOr(attributes, "id", ""));
                getPayloadInternal().setElement(whiteboardElement);
                wbElement = whiteboardElement;
            }
        }
        ++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
        if (level_ == 0) {
            getPayloadInternal().setData(data_);
        } else if (level_ == 1) {
            if (operation instanceof WhiteboardInsertOperation) {
                WhiteboardInsertOperation insertOp = (WhiteboardInsertOperation) operation;
                insertOp.setElement(wbElement);
            }
            if (operation instanceof WhiteboardUpdateOperation) {
                WhiteboardUpdateOperation updateOp = (WhiteboardUpdateOperation) operation;
                updateOp.setElement(wbElement);
            }
            getPayloadInternal().setOperation(operation);
        } else if (level_ == 2) {
            if (element == "text") {
                actualIsText = false;
            }
        }
        
    }

    @Override
    public void handleCharacterData(String data) {
        if (level_ == 3 && actualIsText) {
            WhiteboardTextElement element = (WhiteboardTextElement) getPayloadInternal().getElement();
            element.setText(data);
        }
    }

    private WhiteboardPayload.Type stringToType(String type) {
        if (type == "data") {
            return Type.Data;
        } else if (type == "session-request") {
            return Type.SessionRequest;
        } else if (type == "session-accept") {
            return Type.SessionAccept;
        } else if (type == "session-terminate") {
            return Type.SessionTerminate;
        } else {
            return Type.UnknownType;
        }
    }
    
    private int opacityToAlpha(String opacity) {
        int value = 255;
        int location = opacity.indexOf('.');
        if (location != -1 && opacity.length() > (3+location)) {
            String stringValue = opacity.substring(location+1,location+3);
            try {
                value = Integer.parseInt(stringValue)*255/100;
            } catch (NumberFormatException nfe) {
                value = 255;
            }
        }
        return value;
    }
    
    /**
     * Gets the given attribute from a {@link AttributeMap} if it is set and none
     * {@code null}, otherwise returns a default value.
     * @param attributeMap An {@link AttributeMap}
     * @param attribute The name of the attribute to get from the map.
     * @param defaultValue Default value to return if the attribute is not set
     * (or is set to {@code null}) in the {@link AttributeMap}
     * @return The value of the attribute in the {@link AttributeMap} if it is
     * none {@code null} or {@code defaultValue}
     */
    private String getAttributeOr(AttributeMap attributeMap,String attribute,String defaultValue) {
        String value = attributeMap.getAttribute(attribute);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    
    /**
     * Gets an int value for a given attribute in an attirbute map, or a default value
     * if that attribute is not set.
     * @param attributeMap An {@link AttributeMap}
     * @param attribute The name of the attribute to get from the map
     * @param defaultValue The default value to return if the attribute is not set.
     * @throws NumberFormatException if the attribute value can not be passed into an integer.
     * @return The value of the attribute as an int or defaultValue if it was not set.
     */
    private int getIntAttribute(AttributeMap attributeMap,String attribute,int defaultValue) throws NumberFormatException {
        String stringValue = getAttributeOr(attributeMap, attribute, String.valueOf(defaultValue));
        return Integer.parseInt(stringValue);
    }
    
}
