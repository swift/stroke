/*
 * Copyright (c) 2014 Kevin Smith and Remko Tronçon
 * All rights reserved.
 */

/*
 * Copyright (c) 2014, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.elements;

public class MAMQuery extends Payload {
    public void setQueryID(String queryID) {
        queryID_ = queryID;
    }
    
    public String getQueryID() {
        return queryID_;
    }
    
    public void setForm(Form form) {
        form_ = form;
    }
    
    public Form getForm() {
        return form_;
    }
    
    public void setResultSet(ResultSet resultSet) {
        resultSet_ = resultSet;
    }
    
    public ResultSet getResultSet() {
        return resultSet_;
    }
    
    private String queryID_;
    private Form form_;
    private ResultSet resultSet_;
}
