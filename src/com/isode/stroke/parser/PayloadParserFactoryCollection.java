/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import java.util.Vector;

/**
 * Collection of parser factories.
 */
public class PayloadParserFactoryCollection {

    private final Vector<PayloadParserFactory> factories_ = new Vector<PayloadParserFactory>();
    private PayloadParserFactory defaultFactory_ = null;

    public void addFactory(PayloadParserFactory factory) {
        synchronized (factories_) {
            factories_.add(factory);
        }
    }

    public void setDefaultFactory(PayloadParserFactory factory) {
        defaultFactory_ = factory;
    }

    public PayloadParserFactory getPayloadParserFactory(String element, String ns, AttributeMap attributes) {
        synchronized(factories_) {
            for (PayloadParserFactory factory : factories_) {
                if (factory.canParse(element, ns, attributes)) {
                    return factory;
                }
            }
        }
        return defaultFactory_;
    }
}
