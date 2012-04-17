/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser;

import java.util.LinkedList;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.parser.tree.ParserElement;

/** 
 * Generic parser offering something a bit like a DOM to work with
 * @param <T> Payload Type to parse
 */
public abstract class GenericPayloadTreeParser<T extends Payload> extends GenericPayloadParser<T> {

    private LinkedList<ParserElement> elementStack_ = new LinkedList<ParserElement>(); 
    private ParserElement root_;

    /**
     * Create the parser for the given payload type
     * @param payload payload type object, not null
     */
    public GenericPayloadTreeParser(T payload) {
        super(payload);
    }

    @Override
    public void handleCharacterData(String data) {
        ParserElement current = elementStack_.getLast();
        current.appendCharacterData(data);
    }

    @Override
    public void handleEndElement(String element, String xmlns) {
        elementStack_.removeLast();
        if (elementStack_.isEmpty()) {
            handleTree(root_);
        }
    }

    @Override
    public void handleStartElement(String element, String xmlns,
            AttributeMap attributes) {
        if (root_ == null) {
            root_ = new ParserElement(element, xmlns, attributes);
            elementStack_.addLast(root_);
        }else {            
            ParserElement current = elementStack_.getLast();
            elementStack_.addLast(current.addChild(element, xmlns, attributes));
        }
    }
    
    /**
     * Parse children of the root element. Subclasses should implement
     * this method to extract the items from child nodes
     * @param root root of the node whose children contains items
     */
    public abstract void handleTree(ParserElement root);
}
