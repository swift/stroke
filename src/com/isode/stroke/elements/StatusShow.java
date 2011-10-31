/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class StatusShow extends Payload {

    private Type type_;

    public enum Type {

        Online, Away, FFC, XA, DND, None
    };

    public StatusShow() {
        type_ = Type.Online;
    }

    public StatusShow(Type type) {
        type_ = type;
    }

    void setType(Type type) {
        type_ = type;
    }

    Type getType() {
        return type_;
    }

    static String typeToFriendlyName(Type type) {
        switch (type) {
            case Online:
                return "Available";
            case FFC:
                return "Available";
            case Away:
                return "Away";
            case XA:
                return "Away";
            case DND:
                return "Busy";
            case None:
                return "Offline";
        }
        return "Unknown";
    }
}
