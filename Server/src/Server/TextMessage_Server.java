package Server;

import java.sql.Timestamp;

public class TextMessage_Server extends Message_Server {
	public String content;

	public TextMessage_Server(User_Server sender, String content) {
		super(sender, 0);
		this.content = content;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
}
