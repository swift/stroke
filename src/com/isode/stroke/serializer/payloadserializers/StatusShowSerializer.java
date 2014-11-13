/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.StatusShow;
import com.isode.stroke.serializer.GenericPayloadSerializer;

public class StatusShowSerializer extends GenericPayloadSerializer<StatusShow> {

	public StatusShowSerializer() {
		super(StatusShow.class);
	}

    protected String serializePayload(StatusShow statusShow) {
        if (statusShow.getType () == StatusShow.Type.Online || statusShow.getType() == StatusShow.Type.None) {
            return "";
        }
        else {
            String result = "<show>";
            switch (statusShow.getType()) {
                case Away: result += "away"; break;
                case XA: result += "xa"; break;
                case FFC: result += "chat"; break;
                case DND: result += "dnd"; break;
                case Online: assert(false); break;
                case None: assert(false); break;
            }
            result += "</show>";
            return result;
        }
    }

}
