package Shared;

import java.io.Serializable;

public class TextMessage_Client extends Message_Client implements Serializable {
	private static final long serialVersionUID = 1339029955021634240L;
	public String content;

	public TextMessage_Client(String senderName, String senderEmail, String content) {
		super(senderName, senderEmail, 0);
		this.content = content;
	}

	@Override
	public String toString() {
		return senderName + ":" + content;
	}
}
