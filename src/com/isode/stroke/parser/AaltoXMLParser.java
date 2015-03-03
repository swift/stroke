/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.fasterxml.aalto.AsyncInputFeeder;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.isode.stroke.base.ByteArray;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

/**
 * Parser based around the Aalto XML parser
 */
class AaltoXMLParser extends XMLParser {

    private final Logger logger_ = Logger.getLogger(this.getClass().getName());
    private boolean error_ = false;
    private final AsyncXMLStreamReader xmlReader_ = new InputFactoryImpl().createAsyncXMLStreamReader();

    public AaltoXMLParser(XMLParserClient client) {
        super(client);
    }


    /**
     * Cause the parser thread to parse these data.
     */
    @Override
    public boolean parse(String data) {
        if (data.isEmpty()) {
            return false;
        }
        final AsyncInputFeeder inputFeeder = xmlReader_.getInputFeeder();
        final byte[] xmlBytes = new ByteArray(data).getData();
        int type = 0;
        boolean error = false;

        try {
            inputFeeder.feedInput(xmlBytes, 0, xmlBytes.length);
        } catch (XMLStreamException ex) {
            error = true;
            /* This is an unexpected error */
            throw new IllegalStateException(ex);
        }

        try {
            while ((type = xmlReader_.next()) != XMLStreamConstants.END_DOCUMENT && type != AsyncXMLStreamReader.EVENT_INCOMPLETE) {
                QName name;
                switch (type) {
                    case XMLStreamConstants.START_ELEMENT:
                        name = xmlReader_.getName();
                        AttributeMap attributes = new AttributeMap();
                        for (int i = 0; i < xmlReader_.getAttributeCount(); i++) {
                            QName attributeName = xmlReader_.getAttributeName(i);
                            attributes.addAttribute(attributeName.getLocalPart(), attributeName.getNamespaceURI(), xmlReader_.getAttributeValue(i));
                        }
                        getClient().handleStartElement(name.getLocalPart(), name.getNamespaceURI(), attributes);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        name = xmlReader_.getName();
                        getClient().handleEndElement(name.getLocalPart(), name.getNamespaceURI());
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        getClient().handleCharacterData(xmlReader_.getText());
                        break;
                }

            }
        } catch (XMLStreamException e) {
            error = true;
            /* This type of error (illegal XML) is ~expected */
        }
        if (type == XMLStreamConstants.END_DOCUMENT) {
            try {
                xmlReader_.close();
            } catch (XMLStreamException ex) {
                /* If the parser errors while we're shutting down, it's not much of an error.*/
            }
        }
        return !error;
    }

    
}
