/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Command;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.Command.Action;
import com.isode.stroke.elements.Command.Note;
import com.isode.stroke.elements.Command.Status;
import com.isode.stroke.elements.Command.Note.Type;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

/**
 * Parser for {@link Command} element.
 */
public class CommandParser extends GenericPayloadParser<Command> {
    private static final int TopLevel = 0;
    private static final int PayloadLevel = 1;
    private static final int FormOrNoteOrActionsLevel = 2;
    private static final int ActionsActionLevel = 3;

    private int level_;
    private boolean inNote_;
    private boolean inActions_;
    private Type noteType_;
    private FormParserFactory formParserFactory_;
    private FormParser formParser_;
    private String currentText_ = "";

    /**
     * Constructor
     */
    public CommandParser() {
        super(new Command());

        level_ = TopLevel;
        inNote_ = false;
        inActions_ = false;
        noteType_ = Type.INFO;
        formParser_ = null;

        formParserFactory_ = new FormParserFactory();
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

        ++level_;
        Command command = getPayloadInternal();

        if (level_ == PayloadLevel) {
            Action action = parseAction(attributes
                    .getAttribute(Command.COMMAND_ATTRIBUTE_ACTION));
            command.setAction(action);

            String status = attributes
                    .getAttribute(Command.COMMAND_ATTRIBUTE_STATUS);
            command.setStatus(Status.getStatus(status));

            command.setNode(attributes
                    .getAttribute(Command.COMMAND_ATTRIBUTE_NODE));
            command.setSessionID(attributes
                    .getAttribute(Command.COMMAND_ATTRIBUTE_SESSION_ID));
        } else if (level_ == FormOrNoteOrActionsLevel) {
            assert (formParser_ == null);
            if (formParserFactory_.canParse(element, ns, attributes)) {
                formParser_ = (FormParser) (formParserFactory_
                        .createPayloadParser());
                assert (formParser_ != null);
            } else if (element.equals(Command.COMMAND_ELEMENT_NOTE)) {
                inNote_ = true;
                currentText_ = "";
                String noteType = attributes
                        .getAttribute(Command.COMMAND_ATTRIBUTE_TYPE);
                noteType_ = Type.getType(noteType);
            } else if (element.equals(Command.COMMAND_ELEMENT_ACTIONS)) {
                inActions_ = true;
                Action action = parseAction(attributes
                        .getAttribute(Command.COMMAND_ATTRIBUTE_EXECUTE));
                command.setExecuteAction(action);
            }
        } else if (level_ == ActionsActionLevel) {
        }

        if (formParser_ != null) {
            formParser_.handleStartElement(element, ns, attributes);
        }
    }

    public void handleEndElement(String element, String ns) {
        if (element == null) {
            throw new NullPointerException("'element' must not be null");
        }
        if (ns == null) {
            throw new NullPointerException("'ns' must not be null");
        }

        if (formParser_ != null) {
            formParser_.handleEndElement(element, ns);
        }

        Command command = getPayloadInternal();

        if (level_ == FormOrNoteOrActionsLevel) {
            if (formParser_ != null) {
                Form form = (Form) (formParser_.getPayload());
                assert (form != null);
                command.setForm(form);
                formParser_ = null;
            } else if (inNote_) {
                inNote_ = false;
                command.addNote(new Note(currentText_, noteType_));
            } else if (inActions_) {
                inActions_ = false;
            }
        } else if ((level_ == ActionsActionLevel) && inActions_) {
            Action action = parseAction(element);
            command.addAvailableAction(action);
        }

        --level_;
    }

    public void handleCharacterData(String data) {
        if (data == null) {
            throw new NullPointerException("'data' must not be null");
        }

        if (formParser_ != null) {
            formParser_.handleCharacterData(data);
        } else {
            currentText_ += data;
        }
    }

    private static Action parseAction(String action) {
        if (action == null) {
            throw new NullPointerException("'action' must not be null");
        }

        return Action.getAction(action);
    }

    @Override
    public String toString() {
        return CommandParser.class.getSimpleName() + "\nlevel: " + level_
                + "\ncurrent text: " + currentText_ + "\nnote: " + inNote_
                + "\nactions: " + inActions_ + "\nnote type: " + noteType_;
    }
}
