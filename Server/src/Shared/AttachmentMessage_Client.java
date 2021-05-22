package Shared;

import java.io.File;
import java.io.Serializable;

public class AttachmentMessage_Client extends Message_Client implements Serializable {
	private static final long serialVersionUID = 1339029955021634240L;
	public File fileName;
	public byte[] content;

	public AttachmentMessage_Client(String senderName, String senderEmail, byte[] content, File file) {
		super(senderName, senderEmail, 1);
		this.content = content;
		this.fileName = file;
	}

	@Override
	public String toString() {
		return senderName + ":" + content;
	}

	public String getContent() {
		return fileName.getName();
	}

}
