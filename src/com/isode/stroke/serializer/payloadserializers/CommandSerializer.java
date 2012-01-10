/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Kevin Smith
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Command;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.Command.Action;
import com.isode.stroke.elements.Command.Note;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLTextNode;

/**
 * Serializer for {@link Command} element.
 */
public class CommandSerializer extends GenericPayloadSerializer<Command> {
    /**
     * Constructor
     */
    public CommandSerializer() {
        super(Command.class);
    }

    public String serializePayload(Command command) {
        if (command == null) {
            throw new NullPointerException("'command' must not be null");
        }

        XMLElement commandElement = new XMLElement("command",
                "http://jabber.org/protocol/commands");
        commandElement.setAttribute(Command.COMMAND_ATTRIBUTE_NODE, command
                .getNode());

        if (!command.getSessionID().isEmpty()) {
            commandElement.setAttribute(Command.COMMAND_ATTRIBUTE_SESSION_ID,
                    command.getSessionID());
        }

        String action = actionToString(command.getAction());
        if (!action.isEmpty()) {
            commandElement.setAttribute(Command.COMMAND_ATTRIBUTE_ACTION,
                    action);
        }

        String status = command.getStatus().getStringForm();
        if (!status.isEmpty()) {
            commandElement.setAttribute(Command.COMMAND_ATTRIBUTE_STATUS,
                    status);
        }

        if (command.getAvailableActions().size() > 0) {
            String actions = "<" + Command.COMMAND_ELEMENT_ACTIONS;
            String executeAction = actionToString(command.getExecuteAction());
            if (!executeAction.isEmpty()) {
                actions += " " + Command.COMMAND_ATTRIBUTE_EXECUTE + "='"
                        + executeAction + "'";
            }
            actions += ">";
            for (Action act : command.getAvailableActions()) {
                actions += "<" + actionToString(act) + "/>";
            }
            actions += "</" + Command.COMMAND_ELEMENT_ACTIONS + ">";
            commandElement.addNode(new XMLRawTextNode(actions));
        }

        for (Note note : command.getNotes()) {
            XMLElement noteElement = new XMLElement(
                    Command.COMMAND_ELEMENT_NOTE);
            String type = note.type.getStringForm();
            noteElement.setAttribute(Command.COMMAND_ATTRIBUTE_TYPE, type);
            noteElement.addNode(new XMLTextNode(note.note));
            commandElement.addNode(noteElement);
        }

        Form form = command.getForm();
        if (form != null) {
            FormSerializer formSerializer = new FormSerializer();
            commandElement.addNode(new XMLRawTextNode(formSerializer
                    .serialize(form)));
        }
        return commandElement.serialize();
    }

    private String actionToString(Action action) {
        if (action == null) {
            throw new NullPointerException("'action' must not be null");
        }

        return action.getStringForm();
    }

    @Override
    public String toString() {
        return CommandSerializer.class.getSimpleName();
    }
}
