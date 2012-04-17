/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.tree;

import java.util.LinkedList;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

/**
 * Class for parsing elements of a parser element
 *
 */
public class TreeReparser {
    /**
     * Class representing an element and its handling state
     *
     */
    private static class ElementState {
        ParserElement pe;
        boolean state;
        public ElementState(ParserElement pe, boolean state) {
            super();
            this.pe = pe;
            this.state = state;
        }        
    }

    /**
     * Parse the elements under the given root node
     * @param root root node, not null
     * @param collection payload parser factories, not null
     * @return payload created from the elements of the given node, not null
     */
    public static Payload parseTree(ParserElement root, PayloadParserFactoryCollection collection) {
        PayloadParser parser = collection.getPayloadParserFactory(root.getName(), root.getNamespace(), 
                root.getAttributes()).createPayloadParser();
        LinkedList<ElementState> stack = new LinkedList<ElementState>();
        stack.addLast(new ElementState(root, true));
        while (!stack.isEmpty()) {
            ElementState current = stack.getLast();
            stack.removeLast();
            if (current.state) {
                stack.addLast(new ElementState(current.pe, false));
                parser.handleStartElement(current.pe.getName(), current.pe.getNamespace(), current.pe.getAttributes());
                for(ParserElement child : current.pe.getAllChildren()) {
                    stack.addLast(new ElementState(child, true));
                }
            } else {
                parser.handleCharacterData(current.pe.getText());
                parser.handleEndElement(current.pe.getName(), current.pe.getNamespace());
            }
        }

        Payload payload = parser.getPayload();
        return payload;
    }
}
