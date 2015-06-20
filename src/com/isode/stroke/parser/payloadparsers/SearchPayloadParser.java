/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.SearchPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.payloadparsers.FormParserFactory;
import com.isode.stroke.parser.payloadparsers.FormParser;

public class SearchPayloadParser extends GenericPayloadParser<SearchPayload> {

    private static final int TopLevel = 0;
    private static final int PayloadLevel = 1;
    private static final int ItemLevel = 2;

    private int level = 0;
    private String currentText = "";
    private FormParserFactory formParserFactory = new FormParserFactory();
    private FormParser formParser;
    private SearchPayload.Item currentItem;

    public SearchPayloadParser() {
        super(new SearchPayload());
        formParserFactory = new FormParserFactory();
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level == TopLevel) {

		}
		else if (level == PayloadLevel) {
			if (element.equals("x") && ns.equals("jabber:x:data")) {
				assert (formParser == null);
				formParser = (FormParser)(formParserFactory.createPayloadParser());
			}
			else
            	if (element.equals("item")) {
				assert currentItem == null;
				currentItem = new SearchPayload.Item();
				currentItem.jid = JID.fromString(attributes.getAttribute("jid"));
			}
			else {
				currentText = "";
			}
		}
		else if (level == ItemLevel && currentItem != null) {
			currentText = "";
		}

		if (formParser != null) {
			formParser.handleStartElement(element, ns, attributes);
		} 

		++level;
    }

    public void handleEndElement(String element, String ns) {
        --level;

	if (formParser != null) {
		formParser.handleEndElement(element, ns);
	}

	if (level == TopLevel) {
	}
	else if (level == PayloadLevel) {
		if (formParser != null) {
			getPayloadInternal().setForm(formParser.getPayloadInternal());
			formParser = null;
		}
		else if (element.equals("item")) {
			assert currentItem != null;
			getPayloadInternal().addItem(currentItem);
			currentItem = null;
		}
		else if (element.equals("instructions")) {
			getPayloadInternal().setInstructions(currentText);
		}
		else if (element.equals("nick")) {
			getPayloadInternal().setNick(currentText);
		}
		else if (element.equals("first")) {
			getPayloadInternal().setFirst(currentText);
		}
		else if (element.equals("last")) {
			getPayloadInternal().setLast(currentText);
		}
		else if (element.equals("email")) {
			getPayloadInternal().setEMail(currentText);
		}
	}
	else if (level == ItemLevel && currentItem != null) {
		if (element.equals("nick")) {
			currentItem.nick = currentText;
		}
		else if (element.equals("first")) {
			currentItem.first = currentText;
		}
		else if (element.equals("last")) {
			currentItem.last = currentText;
		}
		else if (element.equals("email")) {
			currentItem.email = currentText;
		}
	}
    }

    public void handleCharacterData(String data) {
        if (formParser != null) {
		formParser.handleCharacterData(data);
	}
	else {
		currentText += data;
	}
    }

}
