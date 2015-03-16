package com.isode.stroke.chat;

import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.ChatState;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.base.NotNull;

public class ChatStateTracker {

	private ChatState.ChatStateType currentState_;
	public final Signal1<ChatState.ChatStateType> onChatStateChange = new Signal1<ChatState.ChatStateType>();

	/**
	* ChatStateTracker();
	*/
	public ChatStateTracker() {
		currentState_ = ChatState.ChatStateType.Gone;
	}

	/**
	* @param message, notnull
	*/
	public void handleMessageReceived(Message message) {
		NotNull.exceptIfNull(message, "message");
		if (message.getType() == Message.Type.Error) {
			return;
		}
		ChatState statePayload = message.getPayload(new ChatState());
		if (statePayload != null) {
			changeState(statePayload.getChatState());
		}
	}

	/**
	* @param newPresence, notnull
	*/
	public void handlePresenceChange(Presence newPresence) {
		NotNull.exceptIfNull(newPresence, "newPresence");
		if (newPresence.getType() == Presence.Type.Unavailable) {
			onChatStateChange.emit(ChatState.ChatStateType.Gone);
		}
	}

	private void changeState(ChatState.ChatStateType state) {
		if (state != currentState_) {
			currentState_ = state;
			onChatStateChange.emit(state);
		}
	}
}
