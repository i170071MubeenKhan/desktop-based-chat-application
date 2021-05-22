package Server;

import Server.GroupChat_Server.ChatParticipant;
import Shared.AttachmentMessage_Client;
import Shared.Chat;
import Shared.GroupChat_Client;
import Shared.PersonalChat_Client;
import Shared.TextMessage_Client;
import Shared.User_Client;

public class Converter {
	public static User_Client to_User_Client(User_Server toConvert) {
		User_Client converted = null;
		if (toConvert != null) {
			converted = new User_Client(toConvert.name, toConvert.email);
			for (Chat i : toConvert.allChats) {
				if (i.chatType == 0) {
					converted.allChats.add(Converter.to_PersonalChat_Client((PersonalChat_Server) i, toConvert));
				} else if (i.chatType == 1) {
					converted.allChats.add(Converter.to_GroupChat_Client((GroupChat_Server) i, toConvert));
				}
			}
		}
		return converted;
	}

	public static PersonalChat_Client to_PersonalChat_Client(PersonalChat_Server toConvert, User_Server toExclude) {
		PersonalChat_Client converted = null;
		if (toConvert != null) {
			converted = new PersonalChat_Client(toConvert.chatID);
			for (User_Server i : toConvert.allParticipants) {
				if (!i.email.equals(toExclude.email))
					converted.AddParticipant(i.name, i.email, i.isOnline);
			}
			for (Message_Server i : toConvert.allMessages) {
				if (i.messageType == 0)
					converted.allMessages.add(Converter.to_TextMessage_Client((TextMessage_Server) i));
				else if (i.messageType == 1)
					converted.allMessages.add(Converter.to_AttachmentMessage_Client((AttachmentMessage_Server) i));
			}
		}
		return converted;
	}

	public static TextMessage_Client to_TextMessage_Client(TextMessage_Server toConvert) {
		TextMessage_Client converted = null;
		if (toConvert != null) {
			converted = new TextMessage_Client(toConvert.sender.name, toConvert.sender.email, toConvert.content);
		}
		return converted;
	}

	public static AttachmentMessage_Client to_AttachmentMessage_Client(AttachmentMessage_Server toConvert) {
		AttachmentMessage_Client converted = null;
		if (toConvert != null) {
			converted = new AttachmentMessage_Client(toConvert.sender.name, toConvert.sender.email, toConvert.content,
					toConvert.fileName);
		}
		return converted;
	}

	public static GroupChat_Client to_GroupChat_Client(GroupChat_Server toConvert, User_Server toExclude) {
		GroupChat_Client converted = null;
		if (toConvert != null) {
			converted = new GroupChat_Client(toConvert.chatID, toConvert.chatName);
			ChatParticipant participantReference = null;
			for (ChatParticipant i : toConvert.allParticipants) {
				if (i.participantReference.email.equals(toExclude.email)) {
					participantReference = i;
				} else
					converted.AddParticipant(i.participantReference.name, i.participantReference.email);
			}
			if (participantReference.messagesBufferReference != null) {
				converted.sourceLanguage = participantReference.messagesBufferReference.language;
				for (Message_Server i : participantReference.messagesBufferReference.allMessages) {
					if (i.messageType == 0)
						converted.allMessages.add(Converter.to_TextMessage_Client((TextMessage_Server) i));
				}
			}
		}
		return converted;
	}

}
