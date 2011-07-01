/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class ProtocolHeader {

    public ProtocolHeader() {
        version = "1.0";
    }

    public String getTo() {
        return to;
    }

    public void setTo(String a) {
        to = a;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String a) {
        from = a;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String a) {
        version = a;
    }

    public String getID() {
        return id;
    }

    public void setID(String a) {
        id = a;
    }
    private String to = "";
    private String from = "";
    private String id = "";
    private String version = "";
}
