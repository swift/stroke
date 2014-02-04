/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.parser;

import java.lang.reflect.InvocationTargetException;

public class GenericPayloadParserFactory2<T extends PayloadParser> implements PayloadParserFactory {
    
    /**
     * Construct a parser factory that can parse the given top-level tag in the given namespace.
     */
    public GenericPayloadParserFactory2(String tag, String xmlns, PayloadParserFactoryCollection parsers, final Class<? extends PayloadParser> payloadClass) {
        class_ = payloadClass;
        tag_ = tag;
        xmlns_ = xmlns;
        parsers_ = parsers;
    }
    
    public boolean canParse(String element, String ns, AttributeMap attributes) {
        return (tag_.isEmpty() ? true : element == tag_) && (xmlns_.isEmpty() ? true : xmlns_ == ns);
    }
    
    public PayloadParser createPayloadParser() {
        try {
            return (PayloadParser)class_.getConstructor(PayloadParserFactoryCollection.class).newInstance(parsers_);
        } catch (InstantiationException e) {
            
        } catch (IllegalAccessException e) {
            
        } catch (IllegalArgumentException e) {
            
        } catch (InvocationTargetException e) {
            
        } catch (NoSuchMethodException e) {
            
        } catch (SecurityException e) {
            
        }
        return null;
    }
    
    String tag_;
    String xmlns_;
    PayloadParserFactoryCollection parsers_;
    Class<? extends PayloadParser> class_;
}
