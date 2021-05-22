package Shared;

import java.io.Serializable;

public abstract class Chat implements Serializable {
	private static final long serialVersionUID = -371858320437186954L;
	public Integer chatID;
	public int chatType;// 0 for personal 1 for group

	public Chat(Integer chatID, int chatType) {
		this.chatID = chatID;
		this.chatType = chatType;
	}

	public abstract boolean getOnlineStatus();

	public abstract String getChatName();
}
