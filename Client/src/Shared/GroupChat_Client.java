package Shared;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupChat_Client extends Chat implements Serializable {
	private static final long serialVersionUID = 6132836707611650229L;

	public class ChatParticipant implements Serializable {
		private static final long serialVersionUID = 2341149243445816565L;
		public String participantName;
		public String participantEmail;

		public ChatParticipant(String participantName, String participantEmail) {
			this.participantName = participantName;
			this.participantEmail = participantEmail;
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

	public String chatName;
	public String sourceLanguage;
	public ArrayList<ChatParticipant> allParticipants;
	public ArrayList<Message_Client> allMessages;

	public GroupChat_Client(Integer chatID, String chatName) {
		super(chatID, 1);
		this.chatName = chatName;
		this.allParticipants = new ArrayList<GroupChat_Client.ChatParticipant>();
		this.allMessages = new ArrayList<Message_Client>();
	}

	public void AddParticipant(String participantName, String participantEmail) {
		this.allParticipants.add(new ChatParticipant(participantName, participantEmail));
	}

	public void SetLanguage(String language) {
		this.sourceLanguage = language;
	}

	@Override
	public String toString() {
		return allParticipants.get(0).participantName;
	}

	public String getChatName() {
		System.out.println("getChat");
		return chatName;
		// return this;
	}

	public GroupChat_Client getGroupChat_Client() {
		return this;
	}

	@Override
	public boolean getOnlineStatus() {
		// TODO Auto-generated method stub
		System.out.println("The online status");
		return true;

	}

}
