/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.elements;

public class MAMResult extends ContainerPayload<Forwarded> {
    public void setID(String id) {
        id_ = id;
    }
    
    public String getID() {
        return id_;
    }
    
    public void setQueryID(String queryID) {
        queryID_ = queryID;
    }
    
    public String getQueryID() {
        return queryID_;
    }

    private String id_ = "";
    private String queryID_;
}
