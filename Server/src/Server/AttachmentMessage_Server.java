package Server;

import java.io.File;
import java.sql.Timestamp;

public class AttachmentMessage_Server extends Message_Server {
	public File fileName;
	public byte[] content;

	public AttachmentMessage_Server(User_Server sender, byte[] content, File file) {
		super(sender, 1);
		this.content = content;
		fileName = file;

	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;

	}
}
