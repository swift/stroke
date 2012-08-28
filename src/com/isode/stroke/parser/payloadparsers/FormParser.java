/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import java.util.List;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.Form.Type;
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
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import java.util.ArrayList;

/**
 * Parser for {@link Form} element.
 */
public class FormParser extends GenericPayloadParser<Form> {
    private static abstract class FieldParseHelper<T> {
        protected GenericFormField<T> field;

        public void addValue(String s) {
            if (s == null) {
                throw new NullPointerException("'s' must not be null");
            }

            field.addRawValue(s);
        }

        public GenericFormField<T> getField() {
            return field;
        }
    }

    private static class BoolFieldParseHelper extends FieldParseHelper<Boolean> {
        public void addValue(String s) {
            super.addValue(s);
            field.setValue(((s.equals("1")) || (s.equals("true"))));
        }
    }

    private static class StringFieldParseHelper extends
            FieldParseHelper<String> {
        public void addValue(String s) {
            super.addValue(s);
            if (field.getValue().isEmpty()) {
                field.setValue(s);
            } else {
                field.setValue(field.getValue() + "\n" + s);
            }
        }
    };

    private static class JIDFieldParseHelper extends FieldParseHelper<JID> {
        public void addValue(String s) {
            super.addValue(s);
            field.setValue(new JID(s));
        }
    };

    private static class StringListFieldParseHelper extends
            FieldParseHelper<List<String>> {
        public void addValue(String s) {
            super.addValue(s);
            List<String> l = field.getValue();
            l.add(s);
            field.setValue(l);
        }
    };

    private static class JIDListFieldParseHelper extends
            FieldParseHelper<List<JID>> {
        public void addValue(String s) {
            super.addValue(s);
            List<JID> l = field.getValue();
            l.add(new JID(s));
            field.setValue(l);
        }
    };

    private static class BooleanFormFieldParseHelper extends
            BoolFieldParseHelper {
        public BooleanFormFieldParseHelper() {
            field = BooleanFormField.create();
        }

        public static BooleanFormFieldParseHelper create() {
            return new BooleanFormFieldParseHelper();
        }
    }

    private static class FixedFormFieldParseHelper extends
            StringFieldParseHelper {
        public FixedFormFieldParseHelper() {
            field = FixedFormField.create();
        }

        public static FixedFormFieldParseHelper create() {
            return new FixedFormFieldParseHelper();
        }
    }

    private static class HiddenFormFieldParseHelper extends
            StringFieldParseHelper {
        public HiddenFormFieldParseHelper() {
            field = HiddenFormField.create();
        }

        public static HiddenFormFieldParseHelper create() {
            return new HiddenFormFieldParseHelper();
        }
    }

    private static class ListSingleFormFieldParseHelper extends
            StringFieldParseHelper {
        public ListSingleFormFieldParseHelper() {
            field = ListSingleFormField.create();
        }

        public static ListSingleFormFieldParseHelper create() {
            return new ListSingleFormFieldParseHelper();
        }
    }

    private static class TextMultiFormFieldParseHelper extends
            StringFieldParseHelper {
        public TextMultiFormFieldParseHelper() {
            field = TextMultiFormField.create();
        }

        public static TextMultiFormFieldParseHelper create() {
            return new TextMultiFormFieldParseHelper();
        }
    }

    private static class TextPrivateFormFieldParseHelper extends
            StringFieldParseHelper {
        public TextPrivateFormFieldParseHelper() {
            field = TextPrivateFormField.create();
        }

        public static TextPrivateFormFieldParseHelper create() {
            return new TextPrivateFormFieldParseHelper();
        }
    }

    private static class TextSingleFormFieldParseHelper extends
            StringFieldParseHelper {
        public TextSingleFormFieldParseHelper() {
            field = TextSingleFormField.create();
        }

        public static TextSingleFormFieldParseHelper create() {
            return new TextSingleFormFieldParseHelper();
        }
    }

    private static class JIDSingleFormFieldParseHelper extends
            JIDFieldParseHelper {
        public JIDSingleFormFieldParseHelper() {
            field = JIDSingleFormField.create();
        }

        public static JIDSingleFormFieldParseHelper create() {
            return new JIDSingleFormFieldParseHelper();
        }
    }

    private static class JIDMultiFormFieldParseHelper extends
            JIDListFieldParseHelper {
        public JIDMultiFormFieldParseHelper() {
            field = JIDMultiFormField.create();
        }

        public static JIDMultiFormFieldParseHelper create() {
            return new JIDMultiFormFieldParseHelper();
        }
    }

    private static class ListMultiFormFieldParseHelper extends
            StringListFieldParseHelper {
        public ListMultiFormFieldParseHelper() {
            field = ListMultiFormField.create();
        }

        public static ListMultiFormFieldParseHelper create() {
            return new ListMultiFormFieldParseHelper();
        }
    }

    private static final int TopLevel = 0;
    private static final int PayloadLevel = 1;
    private static final int FieldLevel = 2;

    private int level_;
    //private String currentText_ = "";
    private ArrayList<String> texts_ = new ArrayList<String>();
    private String currentOptionLabel_ = "";
    private FieldParseHelper<?> currentFieldParseHelper_ = null;

    /**
     * Constructor
     */
    public FormParser() {
        super(new Form());

        level_ = TopLevel;
    }

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
        } else if (level_ == PayloadLevel) {
            if (element.equals(Form.FORM_ELEMENT_TITLE)) {
                texts_ = new ArrayList();
            } else if (element.equals(Form.FORM_ELEMENT_INSTRUCTIONS)) {
                texts_ = new ArrayList();
            } else if (element.equals(Form.FORM_ELEMENT_FIELD)) {
                String type = attributes
                        .getAttribute(FormField.FORM_FIELD_ATTRIBUTE_TYPE);
                if (type == null) {
                    type = "";
                }
                if (type.equals(FormField.FORM_FIELD_TYPE_BOOLEAN)) {
                    currentFieldParseHelper_ = BooleanFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_FIXED)) {
                    currentFieldParseHelper_ = FixedFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_HIDDEN)) {
                    currentFieldParseHelper_ = HiddenFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_JID_MULTI)) {
                    currentFieldParseHelper_ = JIDMultiFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_JID_SINGLE)) {
                    currentFieldParseHelper_ = JIDSingleFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_LIST_MULTI)) {
                    currentFieldParseHelper_ = ListMultiFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_LIST_SINGLE)) {
                    currentFieldParseHelper_ = ListSingleFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_TEXT_MULTI)) {
                    currentFieldParseHelper_ = TextMultiFormFieldParseHelper
                            .create();
                } else if (type.equals(FormField.FORM_FIELD_TYPE_TEXT_PRIVATE)) {
                    currentFieldParseHelper_ = TextPrivateFormFieldParseHelper
                            .create();
                } else {
                    /*
                     * if (type == FormField.FORM_FIELD_TYPE_TEXT_SINGLE) ||
                     * undefined
                     */
                    currentFieldParseHelper_ = TextSingleFormFieldParseHelper
                            .create();
                }

                if (currentFieldParseHelper_ != null) {
                    String name = attributes
                            .getAttribute(FormField.FORM_FIELD_ATTRIBUTE_VAR);
                    currentFieldParseHelper_.getField().setName(name);

                    String label = attributes
                            .getAttribute(FormField.FORM_FIELD_ATTRIBUTE_LABEL);
                    currentFieldParseHelper_.getField().setLabel(label);
                }
            }
        } else if ((level_ == FieldLevel) && (currentFieldParseHelper_ != null)) {
            texts_ = new ArrayList();
            if (element.equals(FormField.FORM_FIELD_ELEMENT_OPTION)) {
                currentOptionLabel_ = attributes
                        .getAttribute(FormField.FORM_FIELD_ATTRIBUTE_OPTION_LABEL);
            }
        }

        ++level_;
    }

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
                    form.setTitle(getCurrentText());
                } else {
                    form.setTitle(currentTitle + "\n" + getCurrentText());
                }
            } else if (element.equals(Form.FORM_ELEMENT_INSTRUCTIONS)) {
                String currentInstructions = form.getInstructions();
                if (currentInstructions.isEmpty()) {
                    form.setInstructions(getCurrentText());
                } else {
                    form.setInstructions(currentInstructions + "\n"
                            + getCurrentText());
                }
            } else if (element.equals(Form.FORM_ELEMENT_FIELD)) {
                if (currentFieldParseHelper_ != null) {
                    form.addField(currentFieldParseHelper_.getField());
                    currentFieldParseHelper_ = null;
                }
            }
        } else if ((level_ == FieldLevel) && (currentFieldParseHelper_ != null)) {
            if (element.equals(FormField.FORM_FIELD_ELEMENT_REQUIRED)) {
                currentFieldParseHelper_.getField().setRequired(true);
            } else if (element.equals(FormField.FORM_FIELD_ELEMENT_DESC)) {
                currentFieldParseHelper_.getField()
                        .setDescription(getCurrentText());
            } else if (element.equals(FormField.FORM_FIELD_ELEMENT_OPTION)) {
                currentFieldParseHelper_.getField().addOption(
                        new Option(currentOptionLabel_, getCurrentText()));
            } else if (element.equals(FormField.FORM_FIELD_ELEMENT_VALUE)) {
                currentFieldParseHelper_.addValue(getCurrentText());
            }
        }
    }

    private String getCurrentText() {
        int size = 0;
        for (String text : texts_) {
            size += text.length();
        }
        StringBuilder builder = new StringBuilder(size);
        for (String text : texts_) {
            builder.append(text);
        }
        return builder.toString();
    }

    public void handleCharacterData(String text) {
        if (text == null) {
            throw new NullPointerException("'text' must not be null");
        }

        texts_.add(text);
    }

    @Override
    public String toString() {
        return FormParser.class.getSimpleName() + "\nlevel: " + level_
                + "\ncurrent text: " + getCurrentText()
                + "\ncurrent option label: " + currentOptionLabel_;
    }
}
