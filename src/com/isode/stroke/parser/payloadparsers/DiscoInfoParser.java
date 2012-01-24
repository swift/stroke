/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;


public class DiscoInfoParser extends GenericPayloadParser<DiscoInfo> {
    public DiscoInfoParser() {
        super(new DiscoInfo());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == PayloadLevel) {
		if (element .equals("identity")) {
			getPayloadInternal().addIdentity(new DiscoInfo.Identity(attributes.getAttribute("name"), attributes.getAttribute("category"), attributes.getAttribute("type"), attributes.getAttribute("lang", "http://www.w3.org/XML/1998/namespace")));
		}
		else if (element.equals("feature")) {
			getPayloadInternal().addFeature(attributes.getAttribute("var"));
		}
		else if (element.equals("x") && ns.equals("jabber:x:data")) {
			assert(formParser_ == null);
			formParser_ = new FormParser();
		}
	}
	if (formParser_ != null) {
		formParser_.handleStartElement(element, ns, attributes);
	}
	++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
	if (formParser_ != null) {
		formParser_.handleEndElement(element, ns);
	}
	if (level_ == PayloadLevel && formParser_ != null) {
		getPayloadInternal().addExtension(formParser_.getPayloadInternal());
		formParser_ = null;
	}
    }

    public void handleCharacterData(String data) {
        if (formParser_ != null) {
            formParser_.handleCharacterData(data);
        }
    }

    private static final int TopLevel = 0;
    private static final int PayloadLevel = 1;
    private int level_ = 0;
    private FormParser formParser_ = null;
}
