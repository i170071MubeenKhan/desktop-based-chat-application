package Shared;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public abstract class Message_Client implements Serializable {
	private static final long serialVersionUID = 5397705378831126428L;
	public Timestamp timeStamp;
	/**
	 * @apiNote <strong>Message Type Specifications:</strong><br/>
	 *          0 ==> Text Message<br/>
	 */
	public Integer messageType;
	public String senderName;
	public String senderEmail;

	/**
	 * 
	 * @param messageType
	 * @apiNote <strong>Message Type Specifications:</strong><br/>
	 *          0 ==> Text Message<br/>
	 *          1 == Attachment message<br/>
	 */
	public Message_Client(String senderName, String senderEmail, Integer messageType) {
		timeStamp = new Timestamp(new Date().getTime());
		this.senderName = senderName;
		this.senderEmail = senderEmail;
		this.messageType = messageType;
	}
}
