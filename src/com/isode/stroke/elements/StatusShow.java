/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
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

    public void setType(Type type) {
        type_ = type;
    }

    public Type getType() {
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

    /**
	 * Can be used for rough ordering of Types.
	 * Greater magnitude = more available.
	 */
	public static int typeToAvailabilityOrdering(Type type) {
		switch (type) {
			case Online: return 4;
			case FFC: return 5;
			case Away: return 2;
			case XA: return 1;
			case DND: return 3;
			case None: return 0;
		}
		assert(false);
		return 0;
	}
	
    @Override
    public String toString() {
        return "StatusShow : " + type_.toString();
    }

}
