/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.RawXMLPayload;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.SerializingParser;

public class RawXMLPayloadParser extends GenericPayloadParser<RawXMLPayload> {

    private int level_;
    private final SerializingParser serializingParser_ = new SerializingParser();

    public RawXMLPayloadParser() {
        super(new RawXMLPayload());
    }



    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        ++level_;
	serializingParser_.handleStartElement(element, ns, attributes);
    }

    public void handleEndElement(String element, String ns) {
        serializingParser_.handleEndElement(element, ns);
	--level_;
	if (level_ == 0) {
		getPayloadInternal().setRawXML(serializingParser_.getResult());
	}
    }

    public void handleCharacterData(String data) {
        serializingParser_.handleCharacterData(data);
    }

}
