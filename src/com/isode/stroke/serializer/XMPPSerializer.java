/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.StreamType;
import java.util.Vector;

public class XMPPSerializer {

    private final Vector<ElementSerializer> serializers_ = new Vector<ElementSerializer>();
    private final StreamType type_;

    public XMPPSerializer(PayloadSerializerCollection payloadSerializers, StreamType type) {
        type_ = type;
        serializers_.add(new PresenceSerializer(payloadSerializers));
	serializers_.add(new IQSerializer(payloadSerializers));
	serializers_.add(new MessageSerializer(payloadSerializers));
	serializers_.add(new CompressRequestSerializer());
	serializers_.add(new CompressFailureSerializer());
	serializers_.add(new AuthRequestSerializer());
	serializers_.add(new AuthFailureSerializer());
	serializers_.add(new AuthSuccessSerializer());
	serializers_.add(new AuthChallengeSerializer());
	serializers_.add(new AuthResponseSerializer());
	serializers_.add(new StartTLSRequestSerializer());
	serializers_.add(new StartTLSFailureSerializer());
	serializers_.add(new TLSProceedSerializer());
	//serializers_.add(new StreamFeaturesSerializer()); //TODO: Port
        //serializers_.add(new StreamErrorSerializer()); //FIXME!!!: Port
	serializers_.add(new EnableStreamManagementSerializer());
	serializers_.add(new StreamManagementEnabledSerializer());
	serializers_.add(new StreamManagementFailedSerializer());
	serializers_.add(new StreamResumeSerializer());
	serializers_.add(new StreamResumedSerializer());
	serializers_.add(new StanzaAckSerializer());
	serializers_.add(new StanzaAckRequestSerializer());
	//serializers_.add(new ComponentHandshakeSerializer());
    }

    public String serializeHeader(ProtocolHeader header) {
        String result = "<?xml version=\"1.0\"?><stream:stream xmlns=\"" + getDefaultNamespace() + "\" xmlns:stream=\"http://etherx.jabber.org/streams\"";
        if (header.getFrom().length() != 0) {
            result += " from=\"" + header.getFrom() + "\"";
        }
        if (header.getTo().length() != 0) {
            result += " to=\"" + header.getTo() + "\"";
        }
        if (header.getID().length() != 0) {
            result += " id=\"" + header.getID() + "\"";
        }
        if (header.getVersion().length() != 0) {
            result += " version=\"" + header.getVersion() + "\"";
        }
        result += ">";
        return result;
    }

    public String serializeFooter() {
        return "</stream:stream>";
    }

    public String getDefaultNamespace() {
        switch (type_) {
            case ClientStreamType:
                return "jabber:client";
            case ServerStreamType:
                return "jabber:server";
            case ComponentStreamType:
                return "jabber:component:accept";
        }
        assert false;
        return "";
    }

    public String serializeElement(Element element) {
        for (ElementSerializer serializer : serializers_) {
            if (serializer.canSerialize(element)) {
                return serializer.serialize(element);
            }
        }
        throw new IllegalStateException("Trying to send an unknown element");
        //assert false; /* UNKNOWN ELEMENT */
        //return "";
    }
}
