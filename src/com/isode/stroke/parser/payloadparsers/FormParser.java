/*
 * Copyright (c) 2012-2014 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import java.util.ArrayList;
import java.util.List;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.Form.Type;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.FormField.Option;
import com.isode.stroke.elements.FormItem;
import com.isode.stroke.elements.FormText;
import com.isode.stroke.elements.FormReportedRef;
import com.isode.stroke.elements.FormPage;
import com.isode.stroke.elements.FormSection;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

/**
 * Parser for {@link Form} element.
 */
public class FormParser extends GenericPayloadParser<Form> {
	
	private List<FormField> currentFields_ = new ArrayList<FormField>();
	private FormField currentField_ = null;
	private FormItem currentItem_ = null;
	private boolean parsingReported_ = false;
    private boolean parsingOption_ = false;
    private String currentOptionValue_ = "";
    private String currentText_ = "";
    private String currentFieldRef_ = "";
    private boolean parsingItem_ = false;
    private boolean hasReportedRef_ = false;
    private FormText currentTextElement_;
    private FormReportedRef currentReportedRef_;
    private FormPage currentPage_;
    private FormSection currentSection_;
    private List<FormPage> currentPages_ = new ArrayList<FormPage>();
    private List<FormSection> sectionStack_ = new ArrayList<FormSection>();
    private List<FormSection> currentSections_ = new ArrayList<FormSection>();
    private static final int TopLevel = 0; 
    private static final int PayloadLevel = 1;
    private static final int FieldLevel = 2;

    private int level_;
    private String currentOptionLabel_ = "";

    /**
     * Constructor
     */
    public FormParser() {
        super(new Form());
        level_ = TopLevel;
    }

    @Override
    public void handleStartElement(String element, String ns,
            final AttributeMap attributes) {
        if (element == null) {
            throw new NullPointerException("'element' must not be null");
        }
        if (ns == null) {
            throw new NullPointerException("'ns' must not be null");
        }
        if (attributes == null) {
            throw new NullPointerException("'attributes' must not be null");
        }
        Form form = getPayloadInternal();

        if (level_ == TopLevel) {
            String type = attributes.getAttribute(Form.FORM_ATTRIBUTE_TYPE);
            form.setType(Type.getType(type));
        } 
        
        else if (level_ == PayloadLevel) {
            if (element.equals(Form.FORM_ELEMENT_TITLE)) {
                currentText_ = "";
            } 
            
            else if (element.equals(Form.FORM_ELEMENT_INSTRUCTIONS)) {
            	currentText_ = "";
            } 
            
            else if (element.equals(Form.FORM_ELEMENT_REPORTED)) {
            	parsingReported_ = true;
            }
            
            else if (element.equals(Form.FORM_ELEMENT_ITEM)) {
                parsingItem_ = true;
            	currentItem_ = new FormItem();
            }
            else if (element == "page") {
                currentPage_ = new FormPage();
                currentPage_.setLabel(attributes.getAttribute("label"));
            }
        } 
        
        else if (level_ == FieldLevel && currentField_ != null) {
    		currentText_ = "";
            if (element.equals(FormField.FORM_FIELD_ELEMENT_OPTION)) {
                currentOptionLabel_ = attributes
                        .getAttribute(FormField.FORM_FIELD_ATTRIBUTE_OPTION_LABEL);
                currentOptionValue_ = "";
                parsingOption_ = true;
            }
        }
        
        if (level_ >= PayloadLevel) {
        	if (element.equals(Form.FORM_ELEMENT_FIELD)) {
            	currentField_ = new FormField(FormField.Type.UNKNOWN_TYPE);
                String type = attributes.getAttribute(FormField.FORM_FIELD_ATTRIBUTE_TYPE);
                if (type == null) {
                    type = "";
                }
                FormField.Type fieldType = FormField.Type.getTypeFromString(type);
                String name = attributes.getAttribute(FormField.FORM_FIELD_ATTRIBUTE_VAR);
                String label = attributes.getAttribute(FormField.FORM_FIELD_ATTRIBUTE_LABEL);
                currentField_.setType(fieldType);
                currentField_.setName(name);
                currentField_.setLabel(label);
            }
        	
            else if (element.equals(FormField.FORM_FIELD_ELEMENT_VALUE)) {
            	currentText_ = "";
            }
        }
        if (level_ > PayloadLevel) {
            if (element.equals("section")) {
                currentSection_ = new FormSection();
                currentSection_.setLabel(attributes.getAttribute("label"));
                sectionStack_.add(currentSection_);
                currentSections_.add(currentSection_);
            }
            if (element.equals("reportedref")) {
                currentReportedRef_ = new FormReportedRef();
            }
            if (element.equals("fieldref")) {
                currentText_ = "";
                currentFieldRef_ = attributes.getAttribute("var");
                if (sectionStack_.size() > 0) {
                    sectionStack_.get(sectionStack_.size()-1).addFieldRef(currentFieldRef_);
                } else if (currentPage_ != null) {
                    currentPage_.addFieldRef(currentFieldRef_);
                }
            }
            if (element.equals("text")) {
                currentText_ = "";
                currentTextElement_ = new FormText();
            }
        }
        ++level_;
    }
    
    @Override
    public void handleEndElement(String element, String ns) {
        if (element == null) {
            throw new NullPointerException("'element' must not be null");
        }
        if (ns == null) {
            throw new NullPointerException("'ns' must not be null");
        }

        --level_;
        Form form = getPayloadInternal();

        if (level_ == PayloadLevel) {
            if (element.equals(Form.FORM_ELEMENT_TITLE)) {
                String currentTitle = form.getTitle();
                if (currentTitle.isEmpty()) {
                    form.setTitle(currentText_);
                } else {
                    form.setTitle(currentTitle + "\n" + currentText_);
                }
            } 
            
            else if (element.equals(Form.FORM_ELEMENT_INSTRUCTIONS)) {
                String currentInstructions = form.getInstructions();
                if (currentInstructions.isEmpty()) {
                    form.setInstructions(currentText_);
                } else {
                    form.setInstructions(currentInstructions + "\n"
                            + currentText_);
                }
            } 
            
            else if (element.equals(Form.FORM_ELEMENT_REPORTED)) {
            	parsingReported_ = false;
            }
            
            else if (element.equals(Form.FORM_ELEMENT_ITEM)) {
                parsingItem_ = false;                
            	currentItem_.addItemFields(currentFields_);
            	form.addItem(currentItem_);
            	currentFields_.clear();
            	currentItem_ = null;
            }
            else if (element.equals("page")) {
                getPayloadInternal().addPage(currentPage_);
                currentPages_.add(currentPage_);
            }
        }

        else if (currentField_ != null) {
            if (element.equals(FormField.FORM_FIELD_ELEMENT_REQUIRED)) {
            	currentField_.setRequired(true);
            } 
            else if (element.equals(FormField.FORM_FIELD_ELEMENT_DESC)) {
            	currentField_.setDescription(currentText_);
            } 
            else if (element.equals(FormField.FORM_FIELD_ELEMENT_OPTION)) {
            	currentField_.addOption(
                        new Option(currentOptionLabel_, currentOptionValue_));
            	parsingOption_ = false;
            }
            else if (element.equals(FormField.FORM_FIELD_ELEMENT_VALUE)) {
            	if (parsingOption_) {
            		currentOptionValue_ = currentText_;
            	}
            	else {
            		currentField_.addValue(currentText_);
            	}
            }
        }
        
        if (level_ >= PayloadLevel && currentField_ != null) {
            if (element.equals("field")) {
                if (parsingReported_) {
                    getPayloadInternal().addReportedField(currentField_);
                } 
                else if (parsingItem_) {
                    currentFields_.add(currentField_);
                } 
                else {
                    if (currentPages_.size() > 0) {
                        for (FormPage page : currentPages_) {
                            for (String pageRef : page.getFieldRefs()) {
                                if (pageRef.equals(currentField_.getName())) {
                                    page.addField(currentField_);
                                }
                            }
                        }
                        for (FormSection section : currentSections_) {
                            for (String sectionRef : section.getFieldRefs()) {
                                if (sectionRef.equals(currentField_.getName())) {
                                    section.addField(currentField_);
                                }
                            }
                        }
                    } else {
                        form.addField(currentField_);
                    }
                }
                currentField_ = null;
            }
        }
        if (level_ > PayloadLevel) {
            if (element.equals("section")) {
                if (sectionStack_.size() > 1) {
                    // Add the section at the top of the stack to the level below
                    sectionStack_.get(sectionStack_.size()-2).addChildSection(sectionStack_.get(sectionStack_.size()-1));
                    sectionStack_.remove(sectionStack_.size()-1);
                }
                else if (sectionStack_.size() == 1) {
                    // Add the remaining section on the stack to its parent page
                    currentPage_.addChildSection(sectionStack_.get(sectionStack_.size()-1));
                    sectionStack_.remove(sectionStack_.size()-1);
                }
            }
            if (currentReportedRef_ != null && !hasReportedRef_) {
                if (sectionStack_.size() > 0) {
                    sectionStack_.get(sectionStack_.size()-1).addReportedRef(currentReportedRef_);
                } else if (currentPage_ != null) {
                    currentPage_.addReportedRef(currentReportedRef_);
                }
                hasReportedRef_ = true;
                currentReportedRef_ = null;
            }
            if (currentTextElement_ != null) {
                if (element.equals("text")) {
                    currentTextElement_.setTextString(currentText_);
                }
                if (sectionStack_.size() > 0) {
                    sectionStack_.get(sectionStack_.size()-1).addTextElement(currentTextElement_);
                } else if (currentPage_ != null) {
                    currentPage_.addTextElement(currentTextElement_);
                }
                currentTextElement_ = null;
            }
        }
    }
    
    @Override
    public void handleCharacterData(String text) {
        if (text == null) {
            throw new NullPointerException("'text' must not be null");
        }
        currentText_ += text;
    }

    @Override
    public String toString() {
        return FormParser.class.getSimpleName() + "\nlevel: " + level_
                + "\ncurrent text: " + currentText_
                + "\ncurrent option label: " + currentOptionLabel_;
    }
}
