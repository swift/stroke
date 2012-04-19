/*
 * Copyright (c) 2010, 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

public class GenericPayloadParserFactory<T extends PayloadParser> implements PayloadParserFactory {

    private final String tag_;
    private final String xmlns_;
    private final Class payloadClass_;

    public GenericPayloadParserFactory(final String tag, final Class<? extends PayloadParser> payloadClass) {
        this(tag, "", payloadClass);
    }

    public GenericPayloadParserFactory(final String tag, final String xmlns, final Class<? extends PayloadParser> payloadClass) {
        tag_ = tag;
        xmlns_ = xmlns;
        payloadClass_ = payloadClass;
    }

    public boolean canParse(final String element, final String ns, final AttributeMap attributes) {
        return (tag_.isEmpty() ? true : tag_.equals(element)) && (xmlns_.isEmpty() ? true : xmlns_.equals(ns));
    }

    public final PayloadParser createPayloadParser() {
        try {
            return (PayloadParser) payloadClass_.newInstance();
        } catch (InstantiationException ex) {
            /* Fatal */
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            /* Fatal */
            throw new RuntimeException(ex);
        }
    }
}
