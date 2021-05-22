package Server;

import java.sql.Timestamp;
import java.util.Date;

public abstract class Message_Server {
	public Timestamp timeStamp;
	/**
	 * @apiNote <strong>Message Type Specifications:</strong><br/>
	 *          0 ==> Text Message<br/>
	 */
	public Integer messageType;
	public User_Server sender;

	/**
	 * 
	 * @param messageType
	 * @apiNote <strong>Message Type Specifications:</strong><br/>
	 *          0 ==> Text Message<br/>
	 */
	public Message_Server(User_Server sender, Integer messageType) {
		timeStamp = new Timestamp(new Date().getTime());
		this.sender = sender;
		this.messageType = messageType;
	}
}
