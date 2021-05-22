package Shared;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonalChat_Client extends Chat implements Serializable {
	private static final long serialVersionUID = 2822805078462727092L;

	public class ChatParticipant implements Serializable {
		private static final long serialVersionUID = -8623810568038054367L;
		public String participantName;
		public String participantEmail;
		public boolean participantIsOnline;

		public ChatParticipant(String participantName, String participantEmail, boolean participantIsOnline) {
			this.participantName = participantName;
			this.participantEmail = participantEmail;
			this.participantIsOnline = participantIsOnline;
		}

		@Override
		public String toString() {
			return participantName;
		}

		public String getParticipantName() {
			// TODO Auto-generated method stub
			return participantName;
		}
	}

	public ArrayList<ChatParticipant> allParticipants;
	public ArrayList<Message_Client> allMessages;

	public PersonalChat_Client(Integer chatID) {
		super(chatID, 0);
		this.allParticipants = new ArrayList<PersonalChat_Client.ChatParticipant>();
		this.allMessages = new ArrayList<Message_Client>();
	}

	public void AddParticipant(String participantName, String participantEmail, boolean participantIsOnline) {
		this.allParticipants.add(new ChatParticipant(participantName, participantEmail, participantIsOnline));
	}

	@Override
	public String toString() {
		return allParticipants.get(0).participantName;
	}

	public String getChatName() {
		System.out.println("getChat");
		return allParticipants.get(0).participantName;
		// return this;
	}

	public PersonalChat_Client getPersonalChat_Client() {
		return this;
	}

}
