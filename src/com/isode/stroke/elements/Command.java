/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Kevin Smith
 * All rights reserved.
 */

package com.isode.stroke.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Ad-Hoc Command (XEP-0050).
 */
public class Command extends Payload {
    /**
     * Attribute "node"
     */
    public static final String COMMAND_ATTRIBUTE_NODE = "node";

    /**
     * Attribute "sessionid"
     */
    public static final String COMMAND_ATTRIBUTE_SESSION_ID = "sessionid";

    /**
     * Attribute "status"
     */
    public static final String COMMAND_ATTRIBUTE_STATUS = "status";

    /**
     * Attribute "action"
     */
    public static final String COMMAND_ATTRIBUTE_ACTION = "action";

    /**
     * Element "actions"
     */
    public static final String COMMAND_ELEMENT_ACTIONS = "actions";

    /**
     * Attribute "execute"
     */
    public static final String COMMAND_ATTRIBUTE_EXECUTE = "execute";

    /**
     * Element "note"
     */
    public static final String COMMAND_ELEMENT_NOTE = "note";

    /**
     * Attribute "type"
     */
    public static final String COMMAND_ATTRIBUTE_TYPE = "type";

    /**
     * Current status of the command.
     */
    public enum Status {
        /**
         * The command is being executed.
         */
        EXECUTING("executing"),
        /**
         * The command has completed. The command session has ended.
         */
        COMPLETED("completed"),
        /**
         * The command has been cancelled. The command session has ended.
         */
        CANCELED("canceled"),
        /**
         * No status has been set for the command.
         */
        NO_STATUS("");

        private String stringForm_;

        private Status(String stringForm) {
            stringForm_ = stringForm;
        }

        /**
         * Get status from its string form.
         *
         * @param stringForm String form of status, can be null
         *
         * @return Corresponding status if match found, otherwise
         *         {@link #NO_STATUS}. Will never be null.
         */
        public static Status getStatus(String stringForm) {
            if (stringForm != null) {
                for (Status status : Status.values()) {
                    if (status.stringForm_.equals(stringForm)) {
                        return status;
                    }
                }
            }

            return NO_STATUS;
        }

        /**
         * @return String form of status, will never be null
         */
        public String getStringForm() {
            return stringForm_;
        }
    };

    /**
     * The action to undertake with the given command.
     */
    public enum Action {
        /**
         * The command should be cancelled.
         */
        CANCEL("cancel"),
        /**
         * The command should be executed or continue to be executed.
         */
        EXECUTE("execute"),
        /**
         * The command should be completed (if possible).
         */
        COMPLETE("complete"),
        /**
         * The command should digress to the previous stage of execution.
         */
        PREV("prev"),
        /**
         * The command should progress to the next stage of execution.
         */
        NEXT("next"),
        /**
         * No action is available for the command.
         */
        NO_ACTION("");

        private String stringForm_;

        private Action(String stringForm) {
            stringForm_ = stringForm;
        }

        /**
         * Get action from its string form.
         *
         * @param stringForm String form of action, can be null
         *
         * @return Corresponding action if match found, otherwise
         *         {@link Action#NO_ACTION}. Will never be null.
         */
        public static Action getAction(String stringForm) {
            if (stringForm != null) {
                for (Action action : Action.values()) {
                    if (action.stringForm_.equals(stringForm)) {
                        return action;
                    }
                }
            }

            return NO_ACTION;
        }

        /**
         * @return String form of action, will never be null
         */
        public String getStringForm() {
            return stringForm_;
        }
    };

    /**
     * This class contains information about the current status of the command.
     * This is an immutable class.
     */
    public static class Note {
        /**
         * Severity of the note.
         */
        public enum Type {
            /**
             * The note is informational only. This is not really an exceptional
             * condition.
             */
            INFO("info"),
            /**
             * The note indicates a warning. Possibly due to illogical (yet
             * valid) data.
             */
            WARN("warn"),
            /**
             * The note indicates an error. The text should indicate the reason
             * for the error.
             */
            ERROR("error");

            private String stringForm_;

            private Type(String stringForm) {
                stringForm_ = stringForm;
            }

            /**
             * Get type from its string form.
             *
             * @param stringForm String form of type, can be null
             *
             * @return Corresponding type if match found, otherwise
             *         {@link Type#INFO}. Will never be null.
             */
            public static Type getType(String stringForm) {
                if (stringForm != null) {
                    for (Type type : Type.values()) {
                        if (type.stringForm_.equals(stringForm)) {
                            return type;
                        }
                    }
                }

                return INFO;
            }

            /**
             * @return String form of type, will never be null
             */
            public String getStringForm() {
                return stringForm_;
            }
        };

        /**
         * Create a note element for the Ad-Hoc command.
         *
         * @param note user-readable text, must not be null
         * @param type Severity of the note, must not be null
         */
        public Note(String note, Type type) {
            if (note == null) {
                throw new NullPointerException("'note' must not be null");
            }
            if (type == null) {
                throw new NullPointerException("'type' must not be null");
            }
            this.note = note;
            this.type = type;
        }

        /**
         * User-readable text, will never be null
         */
        public final String note;

        /**
         * Severity of the note, will never be null
         */
        public final Type type;
    };

    private String node_;
    private String sessionID_;
    private Action action_;
    private Status status_;
    private Action executeAction_;
    private List<Action> availableActions_ = new ArrayList<Action>();
    private List<Note> notes_ = new ArrayList<Note>();
    private Form form_ = null;

    private void assignData(String node, String sessionID, Action action,
            Status status) {
        setNode(node);
        setSessionID(sessionID);
        setAction(action);
        setStatus(status);
        setExecuteAction(Action.NO_ACTION);
    }

    /**
     * Create an Ad-Hoc command with the given node, session ID, status and
     * {@link Action#NO_ACTION} action.
     *
     * @param node Node, must not be null. Each command is identified by its
     *            'node' attribute. This matches its 'node' attribute from the
     *            service discovery.
     * @param sessionID The ID of the session within which the command exists,
     *            must not be null but can be empty if this is the first stage
     *            of the command and the client does not know it yet
     * @param status Status of the command, must not be null
     */
    public Command(String node, String sessionID, Status status) {
        assignData(node, sessionID, Action.NO_ACTION, status);
    }

    /**
     * Create an Ad-Hoc command with the given node, session ID, action and
     * {@link Status#NO_STATUS} status.
     *
     * @param node Node, must not be null. Each command is identified by its
     *            'node' attribute. This matches its 'node' attribute from the
     *            service discovery.
     * @param sessionID The ID of the session within which the command exists,
     *            must not be null but can be empty if this is the first stage
     *            of the command and the client does not know it yet
     * @param action action of the command, must not be null
     */
    public Command(String node, String sessionID, Action action) {
        assignData(node, sessionID, action, Status.NO_STATUS);
    }

    /**
     * Create an Ad-Hoc command with an empty node, empty session ID,
     * {@link Action#EXECUTE} action and {@link Status#NO_STATUS} status.
     */
    public Command() {
        this("", "", Action.EXECUTE);
    }

    /**
     * @return Node, will never be null
     */
    public String getNode() {
        return node_;
    }

    /**
     * Set command node.
     *
     * @param node Node, must not be null
     */
    public void setNode(String node) {
        if (node == null) {
            throw new NullPointerException("'node' must not be null");
        }

        node_ = node;
    }

    /**
     * @return The ID of the session within which the command exists, can be
     *         empty if this is the first stage of the command and the client
     *         does not know it yet, will never be null
     */
    public String getSessionID() {
        return sessionID_;
    }

    /**
     * Set session ID.
     *
     * @param sessionID The ID of the session within which the command exists,
     *            must not be null but can be empty if this is the first stage
     *            of the command and the client does not know it yet
     */
    public void setSessionID(String sessionID) {
        if (sessionID == null) {
            throw new NullPointerException("'sessionID' must not be null");
        }

        sessionID_ = sessionID;
    }

    /**
     * @return action of the command, will never be null
     */
    public Action getAction() {
        return action_;
    }

    /**
     * Set action of the command.
     *
     * @param action action of the command, must not be null
     */
    public void setAction(Action action) {
        if (action == null) {
            throw new NullPointerException("'action' must not be null");
        }

        action_ = action;
    }

    /**
     * @return execute action of the command, will never be null
     */
    public Action getExecuteAction() {
        return executeAction_;
    }

    /**
     * Set execute action of the command.
     *
     * @param action execute action of the command, must not be null
     */
    public void setExecuteAction(Action action) {
        if (action == null) {
            throw new NullPointerException("'action' must not be null");
        }

        executeAction_ = action;
    }

    /**
     * @return status of the command, will never be null
     */
    public Status getStatus() {
        return status_;
    }

    /**
     * Set status of the command.
     *
     * @param status Status of the command, must not be null
     */
    public void setStatus(Status status) {
        if (status == null) {
            throw new NullPointerException("'status' must not be null");
        }

        status_ = status;
    }

    /**
     * @return List of allowed actions for this stage of execution, will never
     *         be null. The instance of the list stored in the object is not
     *         returned, a copy is made.
     */
    public List<Action> getAvailableActions() {
        return new ArrayList<Action>(availableActions_);
    }

    /**
     * Add to the list of allowed actions for this stage of execution.
     *
     * @param action Action to add, must not be null
     */
    public void addAvailableAction(Action action) {
        if (action == null) {
            throw new NullPointerException("'action' must not be null");
        }

        availableActions_.add(action);
    }

    /**
     * @return List of information elements for the current status of the
     *         command, will never be null. The instance of the list stored in
     *         the object is not returned, a copy is made.
     */
    public List<Note> getNotes() {
        return new ArrayList<Note>(notes_);
    }

    /**
     * Add to the list of information elements for the current status of the
     * command.
     *
     * @param note Note to add, must not be null
     */
    public void addNote(Note note) {
        if (note == null) {
            throw new NullPointerException("'note' must not be null");
        }

        notes_.add(note);
    }

    /**
     * @return form of the command, can be null. The instance of the form stored
     *         in the object is returned, a copy is not made.
     */
    public Form getForm() {
        return form_;
    }

    /**
     * Set form for the command.
     *
     * @param payload Form for the command, can be null. The instance of the
     *            form is stored in the object, a copy is not made.
     */
    public void setForm(Form payload) {
        form_ = payload;
    }

    @Override
    public String toString() {
        return Command.class.getSimpleName() + "\nnode: " + node_
                + "\nsession ID: " + sessionID_ + "\naction: " + action_
                + "\nstatus: " + status_ + "\nexecute action: "
                + executeAction_;
    }
}
