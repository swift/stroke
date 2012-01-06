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

/**
 * Parser for {@link Form} element.
 */
public class FormParser extends GenericPayloadParser<Form> {
    private static abstract class FieldParseHelper<T> {
        protected GenericFormField<T> field;

        public void addValue(String s) {
            field.addRawValue(s);
        }

        public GenericFormField<T> getField() {
            return field;
        }
    }

    private static class BoolFieldParseHelper extends FieldParseHelper<Boolean> {
        public void addValue(String s) {
            field.setValue(((s.equals("1")) || (s.equals("true"))));
            super.addValue(s);
        }
    }

    private static class StringFieldParseHelper extends
            FieldParseHelper<String> {
        public void addValue(String s) {
            if (field.getValue().isEmpty()) {
                field.setValue(s);
            } else {
                field.setValue(field.getValue() + "\n" + s);
            }
            super.addValue(s);
        }
    };

    private static class JIDFieldParseHelper extends FieldParseHelper<JID> {
        public void addValue(String s) {
            field.setValue(new JID(s));
            super.addValue(s);
        }
    };

    private static class StringListFieldParseHelper extends
            FieldParseHelper<List<String>> {
        public void addValue(String s) {
            List<String> l = field.getValue();
            l.add(s);
            field.setValue(l);
            super.addValue(s);
        }
    };

    private static class JIDListFieldParseHelper extends
            FieldParseHelper<List<JID>> {
        public void addValue(String s) {
            List<JID> l = field.getValue();
            l.add(new JID(s));
            field.setValue(l);
            super.addValue(s);
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
    private String currentText_ = "";
    private String currentOptionLabel_;
    private FieldParseHelper<?> currentFieldParseHelper_ = null;

    /**
     * Constructor
     */
    public FormParser() {
        super(new Form());

        level_ = TopLevel;
    }

    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        Form form = getPayloadInternal();

        if (level_ == TopLevel) {
            String type = attributes.getAttribute(Form.FORM_ATTRIBUTE_TYPE);
            form.setType(Type.getType(type));
        } else if (level_ == PayloadLevel) {
            if (element.equals(Form.FORM_ELEMENT_TITLE)) {
                currentText_ = "";
            } else if (element.equals(Form.FORM_ELEMENT_INSTRUCTIONS)) {
                currentText_ = "";
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
            currentText_ = "";
            if (element.equals(FormField.FORM_FIELD_ELEMENT_OPTION)) {
                currentOptionLabel_ = attributes
                        .getAttribute(FormField.FORM_FIELD_ATTRIBUTE_OPTION_LABEL);
            }
        }

        ++level_;
    }

    public void handleEndElement(String element, String ns) {
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
            } else if (element.equals(Form.FORM_ELEMENT_INSTRUCTIONS)) {
                String currentInstructions = form.getInstructions();
                if (currentInstructions.isEmpty()) {
                    form.setInstructions(currentText_);
                } else {
                    form.setInstructions(currentInstructions + "\n"
                            + currentText_);
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
                        .setDescription(currentText_);
            } else if (element.equals(FormField.FORM_FIELD_ELEMENT_OPTION)) {
                currentFieldParseHelper_.getField().addOption(
                        new Option(currentOptionLabel_, currentText_));
            } else if (element.equals(FormField.FORM_FIELD_ELEMENT_VALUE)) {
                currentFieldParseHelper_.addValue(currentText_);
            }
        }
    }

    public void handleCharacterData(String text) {
        currentText_ += text;
    }

    @Override
    public String toString() {
        return FormParser.class.getSimpleName() + "\nlevel: " + level_
                + "\ncurrent text: " + currentText_
                + "\ncurrent option label: " + currentOptionLabel_;
    }
}
