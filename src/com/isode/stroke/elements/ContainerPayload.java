/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.elements;

public class ContainerPayload<T> extends Payload {
    public ContainerPayload() {
    }
    
    public ContainerPayload(T payload) {
        payload_ = payload;
    }
    
    public void setPayload(T payload) {
        payload_ = payload;
    }
    
    public T getPayload() {
        return payload_;
    }
    
    T payload_;
}
