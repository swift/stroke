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

public class WhiteboardPayload extends Payload {

    private String data_;
    private Type type_;
    private WhiteboardElement element_;
    private WhiteboardOperation operation_;

    public enum Type {
        UnknownType, 
        Data, 
        SessionRequest, 
        SessionAccept, 
        SessionTerminate;
    }
    
    public WhiteboardPayload() {
        this(Type.Data);
    }
    
    public WhiteboardPayload(Type type) {
        type_ = type;
    }
    
    public void setData(String data) {
        data_ = data;
    }

    public String getData() {
        return data_;
    }

    public Type getType() {
        return type_;
    }

    public void setType(Type type) {
        type_ = type;
    }

    public WhiteboardElement getElement() {
        return element_;
    }

    public void setElement(WhiteboardElement element) {
        element_ = element;
    }

    public WhiteboardOperation getOperation() {
        return operation_;
    }

    public void setOperation(WhiteboardOperation operation) {
        operation_ = operation;
    }
    
}
