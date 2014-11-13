/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.elements;

public class MAMFin extends Payload {

    private boolean isComplete_;
    
    private boolean isStable_;
    
    private ResultSet resultSet_;
    
    private String queryID_;
    
    public MAMFin() {
        isComplete_ = false;
        isStable_ = true;
    }

    public boolean isComplete() {
        return isComplete_;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete_ = isComplete;
    }

    public boolean isStable() {
        return isStable_;
    }

    public void setStable(boolean isStable) {
        this.isStable_ = isStable;
    }

    public ResultSet getResultSet() {
        return resultSet_;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet_ = resultSet;
    }

    public String getQueryID() {
        return queryID_;
    }

    public void setQueryID(String queryID) {
        this.queryID_ = queryID;
    }
    
}
