package Server;

import java.util.ArrayList;

import Shared.Chat;

@SuppressWarnings("serial")
public class PersonalChat_Server extends Chat {
	public ArrayList<User_Server> allParticipants;
	public ArrayList<Message_Server> allMessages;

	public PersonalChat_Server(Integer chatID) {
		super(chatID, 0);
		this.allParticipants = new ArrayList<User_Server>();
		this.allMessages = new ArrayList<Message_Server>();
	}

	public void addParticipant(User_Server newParticipant) {
		allParticipants.add(newParticipant);
	}
}
