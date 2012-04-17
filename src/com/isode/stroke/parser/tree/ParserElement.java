/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.tree;

import java.util.Vector;
import com.isode.stroke.parser.AttributeMap;

/**
 * Class representing a parser for nodes in the stanza
 *
 */
public class ParserElement {    
    private Vector<ParserElement> children_ = new Vector<ParserElement>();
    private String name_ = "";
    private String xmlns_ ="";
    private AttributeMap attributes_ = new AttributeMap();
    private String text_ = "";

    /**
     * Create the parser.
     * @param name name of the parser node, e.g. query, reason, not null
     * @param xmlns XML namespace, e.g. http://jabber.org/protocol/muc#user, not null
     * @param attributes attributes of the node, not null
     */
    public ParserElement(String name, String xmlns, AttributeMap attributes){
        this.name_ = name;
        this.xmlns_ = xmlns;
        this.attributes_ = attributes;
    }

    /**
     * Get the text data of the node
     * @return not null, can be empty
     */
    public String getText() { 
        return text_; 
    }

    /**
     * Get the name of the node, not null
     * @return name of node
     */
    public String getName() { 
        return name_; 
    }

    /**
     * Get the name space of the XML node, not null
     * @return xml namespace
     */
    public String getNamespace() { 
        return xmlns_; 
    }

    /**
     * Get the attributes of the XML node
     * @return XMP node attributes, not null
     */
    public AttributeMap getAttributes() { 
        return attributes_; 
    }

    /**
     * Add child of the XML node
     * @param name name of child node, not null
     * @param xmlns XML namespace, not null
     * @param attributes attributes, not null
     * @return child node, not null
     */
    public ParserElement addChild(String name, String xmlns, AttributeMap attributes){
        ParserElement child = new ParserElement(name, xmlns, attributes);
        children_.add(child);
        return child;
    }

    /**
     * Append data to the text of the node
     * @param data data to be appended, not null
     */
    public void appendCharacterData(String data) {
        text_ += data;
    }

    /**
     * Get the children of the node which have the given name
     * and namespace value
     * @param name name of node, not null
     * @param xmlns namespace of node, not null
     * @return list of children, not null but can be empty
     */
    public Vector<ParserElement> getChildren(String name, String xmlns) {
        Vector<ParserElement> result = new Vector<ParserElement>();
        for(ParserElement child : children_) {
            if(child.name_.equals(name) && child.xmlns_.equals(xmlns)) {
                result.add(child);
            }            
        }
        return result;
    }

    /** 
     * Get all the child nodes of XML element
     * @return list of all children, not null but can be empty
     */
    public Vector<ParserElement> getAllChildren() {
        return children_;
    }

    /**
     * Get the first child of the node which have the given name
     * and namespace value
     * @param name name of node, not null
     * @param xmlns namespace of node, not null
     * @return child node if exists or a parser element {@link NullParserElement} 
     *         representing null value
     */
    public ParserElement getChild(String name, String xmlns) {
        Vector<ParserElement> results = getChildren(name, xmlns);
        ParserElement result = results.isEmpty() ? NullParserElement.element : results.get(0);
        return result;
    }    
}
