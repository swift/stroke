/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Kevin Smith
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import java.util.List;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.FormField.BooleanFormField;
import com.isode.stroke.elements.FormField.FixedFormField;
import com.isode.stroke.elements.FormField.GenericFormField;
import com.isode.stroke.elements.FormField.HiddenFormField;
import com.isode.stroke.elements.FormField.JIDMultiFormField;
import com.isode.stroke.elements.FormField.JIDSingleFormField;
import com.isode.stroke.elements.FormField.ListMultiFormField;
import com.isode.stroke.elements.FormField.ListSingleFormField;
import com.isode.stroke.elements.FormField.Option;
import com.isode.stroke.elements.FormField.TextMultiFormField;
import com.isode.stroke.elements.FormField.TextPrivateFormField;
import com.isode.stroke.elements.FormField.TextSingleFormField;
import com.isode.stroke.jid.JID;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;

/**
 * Serializer for {@link Form} element.
 */
public class FormSerializer extends GenericPayloadSerializer<Form> {
    /**
     * Constructor
     */
    public FormSerializer() {
        super(Form.class);
    }

    public String serializePayload(Form form) {
        if (form == null) {
            throw new NullPointerException("'form' must not be null");
        }

        XMLElement formElement = new XMLElement("x", "jabber:x:data");
        String type = form.getType().getStringForm();
        formElement.setAttribute(Form.FORM_ATTRIBUTE_TYPE, type);
        if (!form.getTitle().isEmpty()) {
            multiLineify(form.getTitle(), Form.FORM_ELEMENT_TITLE, formElement);
        }
        if (!form.getInstructions().isEmpty()) {
            multiLineify(form.getInstructions(),
                    Form.FORM_ELEMENT_INSTRUCTIONS, formElement);
        }
        for (FormField field : form.getFields()) {
            formElement.addNode(fieldToXML(field));
        }
        return formElement.serialize();
    }

    private XMLElement fieldToXML(FormField field) {
        if (field == null) {
            throw new NullPointerException("'field' must not be null");
        }

        XMLElement fieldElement = new XMLElement(Form.FORM_ELEMENT_FIELD);
        if (!field.getName().isEmpty()) {
            fieldElement.setAttribute(FormField.FORM_FIELD_ATTRIBUTE_VAR, field
                    .getName());
        }
        if (!field.getLabel().isEmpty()) {
            fieldElement.setAttribute(FormField.FORM_FIELD_ATTRIBUTE_LABEL,
                    field.getLabel());
        }
        if (field.getRequired()) {
            fieldElement.addNode(new XMLElement(
                    FormField.FORM_FIELD_ELEMENT_REQUIRED));
        }
        if (!field.getDescription().isEmpty()) {
            XMLElement descriptionElement = new XMLElement(
                    FormField.FORM_FIELD_ELEMENT_DESC);
            descriptionElement.addNode(new XMLTextNode(field.getDescription()));
            fieldElement.addNode(descriptionElement);
        }

        // Set the value and type
        String fieldType = "";
        if (field instanceof BooleanFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_BOOLEAN;
            XMLElement valueElement = new XMLElement(
                    FormField.FORM_FIELD_ELEMENT_VALUE);
            valueElement.addNode(XMLTextNode.create(((BooleanFormField) field)
                    .getValue() ? "1" : "0"));
            fieldElement.addNode(valueElement);
        } else if (field instanceof FixedFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_FIXED;
            serializeValueAsString((FixedFormField) field, fieldElement);
        } else if (field instanceof HiddenFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_HIDDEN;
            serializeValueAsString((HiddenFormField) field, fieldElement);
        } else if (field instanceof ListSingleFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_LIST_SINGLE;
            serializeValueAsString((ListSingleFormField) field, fieldElement);
        } else if (field instanceof TextPrivateFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_TEXT_PRIVATE;
            serializeValueAsString((TextPrivateFormField) field, fieldElement);
        } else if (field instanceof TextSingleFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_TEXT_SINGLE;
            serializeValueAsString((TextSingleFormField) field, fieldElement);
        } else if (field instanceof JIDMultiFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_JID_MULTI;
            List<JID> jids = ((JIDMultiFormField) (field)).getValue();
            for (JID jid : jids) {
                XMLElement valueElement = new XMLElement(
                        FormField.FORM_FIELD_ELEMENT_VALUE);
                valueElement.addNode(XMLTextNode.create(jid.toString()));
                fieldElement.addNode(valueElement);
            }
        } else if (field instanceof JIDSingleFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_JID_SINGLE;
            XMLElement valueElement = new XMLElement(
                    FormField.FORM_FIELD_ELEMENT_VALUE);
            JIDSingleFormField jidSingleFormField = (JIDSingleFormField) field;
            valueElement.addNode(XMLTextNode.create(jidSingleFormField
                    .getValue().toString()));
            fieldElement.addNode(valueElement);
        } else if (field instanceof ListMultiFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_LIST_MULTI;
            List<String> lines = ((ListMultiFormField) (field)).getValue();
            for (String line : lines) {
                XMLElement valueElement = new XMLElement(
                        FormField.FORM_FIELD_ELEMENT_VALUE);
                valueElement.addNode(XMLTextNode.create(line));
                fieldElement.addNode(valueElement);
            }
        } else if (field instanceof TextMultiFormField) {
            fieldType = FormField.FORM_FIELD_TYPE_TEXT_MULTI;
            multiLineify(((TextMultiFormField) field).getValue(),
                    FormField.FORM_FIELD_ELEMENT_VALUE, fieldElement);
        } else {
            assert (false);
        }

        if (!fieldType.isEmpty()) {
            fieldElement.setAttribute(FormField.FORM_FIELD_ATTRIBUTE_TYPE,
                    fieldType);
        }

        for (Option option : field.getOptions()) {
            XMLElement optionElement = new XMLElement(
                    FormField.FORM_FIELD_ELEMENT_OPTION);
            if (!option.label.isEmpty()) {
                optionElement.setAttribute(
                        FormField.FORM_FIELD_ATTRIBUTE_OPTION_LABEL,
                        option.label);
            }

            XMLElement valueElement = new XMLElement(
                    FormField.FORM_FIELD_ELEMENT_OPTION_VALUE);
            valueElement.addNode(XMLTextNode.create(option.value));
            optionElement.addNode(valueElement);

            fieldElement.addNode(optionElement);
        }

        return fieldElement;
    }

    private void multiLineify(String text, String elementName,
            XMLElement element) {
        if (text == null) {
            throw new NullPointerException("'text' must not be null");
        }
        if (elementName == null) {
            throw new NullPointerException("'elementName' must not be null");
        }

        String unRdText = text.replaceAll("\r", "");
        String[] lines = unRdText.split("\n");
        for (String line : lines) {
            XMLElement lineElement = new XMLElement(elementName);
            lineElement.addNode(new XMLTextNode(line));
            element.addNode(lineElement);
        }
    }

    private static void serializeValueAsString(GenericFormField<String> field,
            XMLElement parent) {
        String value = field.getValue();
        // FIXME with the proper fix after Swiften is fixed: if (!value.isEmpty()) {
            XMLElement valueElement = new XMLElement(
                    FormField.FORM_FIELD_ELEMENT_VALUE);
            valueElement.addNode(XMLTextNode.create(value));
            parent.addNode(valueElement);
        // }
    }

    @Override
    public String toString() {
        return FormSerializer.class.getSimpleName();
    }
}
