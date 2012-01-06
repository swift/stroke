/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010-2011 Kevin Smith
 * All rights reserved.
 */

package com.isode.stroke.adhoc;

import java.util.HashMap;
import java.util.List;

import com.isode.stroke.elements.Command;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Command.Action;
import com.isode.stroke.elements.Command.Status;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot2;

/**
 * This class maintains the session between the client and the server for an
 * Ad-Hoc command.
 */
public class OutgoingAdHocCommandSession {
    /**
     * Availability of action.
     */
    public enum ActionState {
        /**
         * Action isn't applicable to this command
         */
        ABSENT,
        /**
         * Action is applicable to this command but not currently available
         */
        PRESENT,
        /**
         * Action is currently available (not used in
         * {@link OutgoingAdHocCommandSession})
         */
        ENABLED,
        /**
         * Action is applicable and currently available
         */
        ENABLED_AND_PRESENT
    };

    /**
     * Emitted when the form for the next stage is available. The client should
     * add a listener to this signal which will be called when the server sends
     * a form.
     */
    public final Signal1<Command> onNextStageReceived = new Signal1<Command>();

    /**
     * Emitted on error. The client should add a listener to this signal which
     * will be called when the server sends an error.
     */
    public final Signal1<ErrorPayload> onError = new Signal1<ErrorPayload>();

    private JID to_;
    private String commandNode_;
    private IQRouter iqRouter_;
    private boolean isMultiStage_;
    private String sessionID_;
    private HashMap<Action, ActionState> actionStates_ = new HashMap<Action, ActionState>();

    /**
     * Create an Ad-Hoc command session. The initial command will be sent to the
     * server on calling {@link #start()}.
     * 
     * @param to JID of the user for which the Ad-Hoc command is executed, must
     *            not be null
     * @param commandNode Node part of the Ad-Hoc command as published by the
     *            server (e.g. "http://isode.com/xmpp/commands#test"), must not
     *            be null
     * @param iqRouter TODO: not sure how to explain this, must not be null
     */
    public OutgoingAdHocCommandSession(JID to, String commandNode,
            IQRouter iqRouter) {
        if (to == null) {
            throw new NullPointerException("'to' must not be null");
        }
        if (commandNode == null) {
            throw new NullPointerException("'commandNode' must not be null");
        }
        if (iqRouter == null) {
            throw new NullPointerException("'iqRouter' must not be null");
        }

        to_ = to;
        commandNode_ = commandNode;
        iqRouter_ = iqRouter;
        isMultiStage_ = false;
    }

    private void handleResponse(Command payload, ErrorPayload error) {
        if (error != null) {
            onError.emit(error);
        } else {
            List<Action> actions = payload.getAvailableActions();
            actionStates_.clear();
            if (payload.getStatus() == Status.EXECUTING) {
                actionStates_.put(Action.CANCEL,
                        ActionState.ENABLED_AND_PRESENT);
                actionStates_.put(Action.COMPLETE, ActionState.PRESENT);
                if (actions.contains(Action.COMPLETE)) {
                    actionStates_.put(Action.COMPLETE,
                            ActionState.ENABLED_AND_PRESENT);
                }

                if (getIsMultiStage()) {
                    actionStates_.put(Action.NEXT, ActionState.PRESENT);
                    actionStates_.put(Action.PREV, ActionState.PRESENT);
                }

                if (actions.contains(Action.NEXT)) {
                    actionStates_.put(Action.NEXT,
                            ActionState.ENABLED_AND_PRESENT);
                }

                if (actions.contains(Action.PREV)) {
                    actionStates_.put(Action.PREV,
                            ActionState.ENABLED_AND_PRESENT);
                }
            }

            sessionID_ = payload.getSessionID();
            if (actions.contains(Action.NEXT) || actions.contains(Action.PREV)) {
                isMultiStage_ = true;
            }
            onNextStageReceived.emit(payload);
        }
    }

    /**
     * @return true if the form is multi-stage. Will return a valid result only
     *         after the first response is received from the server so should be
     *         called only after the listener for {@link #onNextStageReceived}
     *         has been called at least once.
     */
    public boolean getIsMultiStage() {
        return isMultiStage_;
    }

    /**
     * Send initial request to the target.
     */
    public void start() {
        Action action = null;
        GenericRequest<Command> commandRequest = new GenericRequest<Command>(
                IQ.Type.Set, to_, new Command(commandNode_, null, action),
                iqRouter_);
        commandRequest.onResponse.connect(new Slot2<Command, ErrorPayload>() {
            public void call(Command payload, ErrorPayload error) {
                handleResponse(payload, error);
            }
        });
        commandRequest.send();
    }

    /**
     * Cancel command session with the target.
     */
    public void cancel() {
        if (sessionID_.length() != 0) {
            submitForm(null, Action.CANCEL);
        }
    }

    /**
     * Return to the previous stage.
     */
    public void goBack() {
        submitForm(null, Action.PREV);
    }

    /**
     * Send the form to complete the command.
     * 
     * @param form Form for submission - if null, the command will be submitted
     *            with no form.
     */
    public void complete(Form form) {
        submitForm(form, Action.COMPLETE);
    }

    /**
     * Send the form to advance to the next stage of the command.
     * 
     * @param form Form for submission - if null, the command will be submitted
     *            with no form.
     */
    public void goNext(Form form) {
        submitForm(form, Action.NEXT);
    }

    private void submitForm(Form form, Action action) {
        Command command = new Command(commandNode_, sessionID_, action);
        command.setForm(form);

        GenericRequest<Command> commandRequest = new GenericRequest<Command>(
                IQ.Type.Set, to_, command, iqRouter_);
        commandRequest.onResponse.connect(new Slot2<Command, ErrorPayload>() {
            public void call(Command payload, ErrorPayload error) {
                handleResponse(payload, error);
            }
        });
        commandRequest.send();
    }

    /**
     * Get the state of a given action. This is useful for a UI to determine
     * which buttons should be visible, and which enabled. If no actions are
     * {@link ActionState#ENABLED_AND_PRESENT}, the command has completed.
     * 
     * <p>
     * Will return a valid result for the current stage only after the response
     * is received from the server so should be called only after the listener
     * for {@link #onNextStageReceived} has been called for that stage.
     * 
     * @param action Action for which the state needs to be determined for the
     *            current form. Useful for Next, Prev, Cancel and Complete only,
     *            for other values and null, {@link ActionState#ABSENT} will be
     *            returned.
     * @return state of the requested action, will never be null
     */
    public ActionState getActionState(Action action) {
        ActionState actionState = actionStates_.get(action);
        if (actionState == null) {
            actionState = ActionState.ABSENT;
        }

        return actionState;
    }

    @Override
    public String toString() {
        return OutgoingAdHocCommandSession.class.getSimpleName() + "\nto: "
                + to_ + "\ncommand node: " + commandNode_ + "\nsession ID: "
                + sessionID_ + "\nis multi-stage: " + isMultiStage_;
    }
}
