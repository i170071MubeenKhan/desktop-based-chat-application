package Shared;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
	private static final long serialVersionUID = 3972523787808405401L;
	public Object message;
	/**
	 *
	 * @apiNote <strong>Message Type Specifications:</strong><br/>
	 *          <br/>
	 *          <strong><em>Messages from Server:</em></strong><br/>
	 *          -1 ==> Unrecognized Request<br/>
	 *          0 ==> Login/SignUp Failed<br/>
	 *          1 ==> Login/SignUp Successful<br/>
	 *          4 ==> Add Contact Successful<br/>
	 *          6 ==> Add Contact Failed<br/>
	 *          7 ==> You have been added to a new chat!<br/>
	 *          8 ==> You have received a new Message<br/>
	 *          9 ==> A contact just went online/offline<br/>
	 *          10 => Your call request has been answered. Please check the
	 *          answer and proceed accordingly<br/>
	 *          11 => You have an Incoming Call<br/>
	 *          12 => Make Group Chat Successful<br/>
	 *          13 => Make Group Chat Failed<br/>
	 *          14 => Change Group Chat Language Complete<br/>
	 *          15 => Your add participant request has been answered<br/>
	 *          16 => A new participant has been added in one of your group
	 *          chats<br/>
	 *          <strong><em>Messages from Clients:</em></strong><br/>
	 *          2 ==> SignUp<br/>
	 *          3 ==> Login<br/>
	 *          4 ==> Add Contact<br/>
	 *          5 ==> Suicide Note<br/>
	 *          8 ==> Please forward my Message<br/>
	 *          10 => I want to make an AudioCall<br/>
	 *          11 => My Reaction to Incoming Call. 12 => I want to make a group
	 *          Chat<br/>
	 *          14 => I want to change language for this group chat<br/>
	 *          15 => Add participant in group chat<br/>
	 */
	public Integer messageType;

	public NetworkMessage() {
		message = null;
		messageType = null;
	}

	/**
	 *
	 * @apiNote <strong>Message Type Specifications:</strong><br/>
	 *          <br/>
	 *          <strong><em>Messages from Server:</em></strong><br/>
	 *          -1 ==> Unrecognized Request<br/>
	 *          0 ==> Login/SignUp Failed<br/>
	 *          1 ==> Login/SignUp Successful<br/>
	 *          4 ==> Add Contact Successful<br/>
	 *          6 ==> Add Contact Failed<br/>
	 *          <strong><em>Messages from Clients:</em></strong><br/>
	 *          2 ==> SignUp<br/>
	 *          3 ==> Login<br/>
	 *          4 ==> Add Contact<br/>
	 *          5 ==> Suicide Note<br/>
	 */
	public NetworkMessage(Object message, Integer messageType) {
		this.message = message;
		this.messageType = messageType;
	}

	/**
	 *
	 * @apiNote <strong>Message Type Specifications:</strong><br/>
	 *          <br/>
	 *          <strong><em>Messages from Server:</em></strong><br/>
	 *          -1 ==> Unrecognized Request<br/>
	 *          0 ==> Login/SignUp Failed<br/>
	 *          1 ==> Login/SignUp Successful<br/>
	 *          4 ==> AddContact Request Successful<br/>
	 *          6 ==> Add Contact Failed<br/>
	 *          7 ==> You have made a new Contact!<br/>
	 *          8 ==> <strong><em>Messages from Clients:</em></strong><br/>
	 *          2 ==> SignUp<br/>
	 *          3 ==> Login<br/>
	 *          4 ==> Add Contact<br/>
	 *          5 ==> Suicide Note<br/>
	 */
	public void makePacket(Object message, Integer messageType) {
		this.message = message;
		this.messageType = messageType;
	}

	public void reset() {
		this.message = null;
		this.messageType = null;
	}
}
