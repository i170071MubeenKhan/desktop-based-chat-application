package Server;

import java.util.ArrayList;

import Shared.Chat;

@SuppressWarnings("serial")
public class GroupChat_Server extends Chat {

	public class MessagesBuffer {
		public String language;
		public ArrayList<Message_Server> allMessages;

		public MessagesBuffer(String language) {
			this.language = language;
			this.allMessages = new ArrayList<Message_Server>();
		}
	}

	public class ChatParticipant {
		public User_Server participantReference;
		public MessagesBuffer messagesBufferReference;

		public ChatParticipant(User_Server participantReference, MessagesBuffer messagesBufferReference) {
			this.participantReference = participantReference;
			this.messagesBufferReference = messagesBufferReference;
		}
	}

	public String chatName;
	public ArrayList<MessagesBuffer> allBuffers;
	public ArrayList<ChatParticipant> allParticipants;

	public GroupChat_Server(Integer chatID, String chatName) {
		super(chatID, 1);
		this.chatName = chatName;
		this.allParticipants = new ArrayList<GroupChat_Server.ChatParticipant>();
		this.allBuffers = new ArrayList<GroupChat_Server.MessagesBuffer>();
	}

	public void AddParticipant(User_Server newParticipant) {
		allParticipants.add(new ChatParticipant(newParticipant, null));
	}

	public void UpdateLanguage(User_Server participant, String language) {
		MessagesBuffer bufferReference = null;
		for (MessagesBuffer i : allBuffers) {
			if (i.language.equals(language)) {
				bufferReference = i;
				break;
			}
		}
		if (bufferReference == null) {
			MessagesBuffer newBuffer = new MessagesBuffer(language);
			String translated;
			if (allBuffers.size() != 0) {
				for (Message_Server i : allBuffers.get(0).allMessages) {
					if (i.messageType == 0) {
						translated = GoogleTranslate.Translate(language, ((TextMessage_Server) i).content);
						newBuffer.allMessages.add(new TextMessage_Server(i.sender, translated));
					}
				}
			}
			allBuffers.add(newBuffer);
			bufferReference = newBuffer;
		}
		for (ChatParticipant i : allParticipants) {
			if (i.participantReference == participant) {
				i.messagesBufferReference = bufferReference;
				break;
			}
		}
	}

}
