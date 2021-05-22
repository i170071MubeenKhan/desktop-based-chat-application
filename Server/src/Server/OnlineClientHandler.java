package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import Server.GroupChat_Server.ChatParticipant;
import Server.GroupChat_Server.MessagesBuffer;
import Shared.Chat;
import Shared.GroupChat_Client;
import Shared.Message_Client;
import Shared.NetworkMessage;
import Shared.TextMessage_Client;
import Shared.AttachmentMessage_Client;

public class OnlineClientHandler extends Thread {
	protected Socket clientSocket;
	private OutputStream outputStream;
	private ObjectOutputStream objectOutputStream;
	private InputStream inputStream;
	private ObjectInputStream objectInputStream;
	protected User_Server user;
	private DomainController domainController;
	private NetworkMessage receivingBuffer;
	private NetworkMessage sendingBuffer;
	private boolean waitForClientReply;
	private int clientReply;

	public OnlineClientHandler(Socket clientSocket, DomainController domainController) {
		this.user = null;
		this.clientSocket = clientSocket;
		try {
			this.outputStream = this.clientSocket.getOutputStream();
			this.objectOutputStream = new ObjectOutputStream(this.outputStream);
			this.inputStream = this.clientSocket.getInputStream();
			this.objectInputStream = new ObjectInputStream(this.inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		synchronized (domainController) {
			this.domainController = domainController;
		}
		this.receivingBuffer = new NetworkMessage();
		this.sendingBuffer = new NetworkMessage();
		this.waitForClientReply = false;
		this.clientReply = 0;
	}

	private void sendMessage() {
		try {
			objectOutputStream.writeObject(sendingBuffer);
			objectOutputStream.flush();
			objectOutputStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void recvMessage() {
		try {
			receivingBuffer = (NetworkMessage) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			ShutdownProtocol();
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized void SignUpHandler() {
		ArrayList<Object> verification = domainController.RegisterNewUser((ArrayList<String>) receivingBuffer.message,
				this);
		user = (User_Server) verification.get(1);
		if ((boolean) verification.get(0) == true) { // failure case
			sendingBuffer.makePacket(verification, 0);
		} else {// success case
			verification.remove(1);
			verification.add(Converter.to_User_Client(user));
			sendingBuffer.makePacket(verification, 1);
		}
		sendMessage();
	}

	@SuppressWarnings("unchecked")
	private synchronized void LoginHandler() {
		ArrayList<Object> verification = domainController.VerifyLogin((ArrayList<String>) receivingBuffer.message,
				this);
		user = (User_Server) verification.get(3);
		if ((boolean) verification.get(0) == false || (boolean) verification.get(1) == false
				|| (boolean) verification.get(2) == true) { // failure case
			sendingBuffer.makePacket(verification, 0);
			sendMessage();
		} else {// success case
			user.isOnline = true;
			verification.remove(3);
			verification.add(Converter.to_User_Client(user));
			sendingBuffer.makePacket(verification, 1);
			sendMessage();
			for (User_Server i : user.contacts) {
				if (i.isOnline)
					i.onlineClientHandlerReference.sendMessageToMyClient(new NetworkMessage(user.email, 9));
			}
		}
	}

	private synchronized void AddContact() {
		String targetEmail = (String) receivingBuffer.message;
		ArrayList<Object> verification = new ArrayList<Object>();
		boolean emailExists = false;
		User_Server targetUserReference = null;
		PersonalChat_Server newPersonalChat = null;
		boolean alreadyContact = false;
		for (User_Server i : domainController.allUsers) {
			if (i.email.equals(targetEmail)) {
				emailExists = true;
				targetUserReference = i;
				for (User_Server j : user.contacts) {
					if (j.email.equals(targetEmail)) {
						alreadyContact = true;
					}
				}
				if (!alreadyContact) { // if not a contact
					user.contacts.add(i);
					i.contacts.add(user);
					newPersonalChat = new PersonalChat_Server(domainController.allChats.size() + 1); // chatID as
																										// paramter
					newPersonalChat.addParticipant(user);
					newPersonalChat.addParticipant(i);
					domainController.allChats.add(newPersonalChat);
					i.allChats.add(newPersonalChat);
					user.allChats.add(newPersonalChat);
				}
				break;
			}
		}
		if (emailExists && !alreadyContact) { // success case
			sendingBuffer.makePacket(Converter.to_PersonalChat_Client(newPersonalChat, user), 4);
			sendMessage();
			for (OnlineClientHandler i : domainController.onlineClients) {
				if (i.user == targetUserReference) { // is online
					i.sendMessageToMyClient(new NetworkMessage(
							Converter.to_PersonalChat_Client(newPersonalChat, targetUserReference), 7));
				}
			}
		} else {// failure case
			verification.add(emailExists);
			verification.add(alreadyContact);
			sendingBuffer.makePacket(verification, 6);
			sendMessage();
		}
	}

	private synchronized void sendMessageToMyClient(NetworkMessage message) {
		sendingBuffer.makePacket(message.message, message.messageType);
		sendMessage();
		sendingBuffer.reset();
	}

	@SuppressWarnings("unchecked")
	private synchronized void RelayMessage() {
		ArrayList<Object> envelopedMessage = (ArrayList<Object>) receivingBuffer.message;
		for (Chat i : user.allChats) {
			if (i.chatID.equals((Integer) envelopedMessage.get(0))) {
				if (i.chatType == 0) {
					if (((Message_Client) envelopedMessage.get(1)).messageType == 0) {// tet attachments
						((PersonalChat_Server) i).allMessages.add(
								new TextMessage_Server(user, ((TextMessage_Client) envelopedMessage.get(1)).content));
					} else {// attachments
						((PersonalChat_Server) i).allMessages.add(new AttachmentMessage_Server(user,
								((AttachmentMessage_Client) envelopedMessage.get(1)).content,
								((AttachmentMessage_Client) envelopedMessage.get(1)).fileName));
					}
					for (User_Server j : ((PersonalChat_Server) i).allParticipants) {
						if (j != user && j.isOnline) {
							j.onlineClientHandlerReference
									.sendMessageToMyClient(new NetworkMessage(envelopedMessage, 8));
						}
					}
					break;
				} else if (i.chatType == 1) {
					if (((Message_Client) envelopedMessage.get(1)).messageType == 0) {
						for (MessagesBuffer j : ((GroupChat_Server) i).allBuffers) {
							String translated = GoogleTranslate.Translate(j.language,
									((TextMessage_Client) envelopedMessage.get(1)).content);
							j.allMessages.add(new TextMessage_Server(user, translated));
						}
					}
					for (ChatParticipant j : ((GroupChat_Server) i).allParticipants) {
						if (j.messagesBufferReference != null && j.participantReference.isOnline
								&& j.participantReference != user) {
							envelopedMessage.remove(1);
							envelopedMessage.add(Converter
									.to_TextMessage_Client((TextMessage_Server) j.messagesBufferReference.allMessages
											.get(j.messagesBufferReference.allMessages.size() - 1)));
							j.participantReference.onlineClientHandlerReference
									.sendMessageToMyClient(new NetworkMessage(envelopedMessage, 8));
						}
					}
				}
			}
		}
	}

	private synchronized void ShutdownProtocol() {
		if (user != null) {
			user.isOnline = false;
			for (User_Server i : user.contacts) {
				if (i.isOnline)
					i.onlineClientHandlerReference.sendMessageToMyClient(new NetworkMessage(user.email, 9));
			}
		}
		domainController.KillOnlineClient(this);
		try {
			objectOutputStream.close();
			objectInputStream.close();
			outputStream.close();
			inputStream.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void ConnectAudioCall() {
		ArrayList<Object> targetClientResponse = null;
		for (Chat i : user.allChats) {
			if (i.chatType == 0) {
				if (i.chatID.equals((Integer) receivingBuffer.message)) {
					User_Server targetClient = (User_Server) (((PersonalChat_Server) i).allParticipants.get(0) == user
							? ((PersonalChat_Server) i).allParticipants.get(1)
							: ((PersonalChat_Server) i).allParticipants.get(0));
					if (targetClient != user && targetClient.isOnline) {
						targetClientResponse = targetClient.onlineClientHandlerReference
								.RouteCallToMyClient(clientSocket.getInetAddress());
						sendingBuffer.makePacket(targetClientResponse, 10);
						sendMessage();
						sendingBuffer.reset();
					} else {
						targetClientResponse = new ArrayList<Object>();
						targetClientResponse.add(false);
						sendingBuffer.makePacket(targetClientResponse, 10);
						sendMessage();
						sendingBuffer.reset();
					}
					break;
				}
			}
		}
	}

	private synchronized ArrayList<Object> RouteCallToMyClient(InetAddress requestingClient) {
		waitForClientReply = true;
		sendingBuffer.makePacket(requestingClient, 11);
		sendMessage();
		while (waitForClientReply != false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ArrayList<Object> reaction = new ArrayList<Object>();
		if (clientReply == 1) {
			reaction.add(true);
			reaction.add(clientSocket.getInetAddress());
		} else
			reaction.add(false);
		waitForClientReply = false;
		return reaction;
	}

	@SuppressWarnings("unchecked")
	private synchronized void MakeGroupChat() {
		ArrayList<Object> groupChatRequestCredentials = (ArrayList<Object>) receivingBuffer.message;
		String chatName = (String) groupChatRequestCredentials.get(0);
		ArrayList<String> targetEmails = (ArrayList<String>) groupChatRequestCredentials.get(1);
		boolean batchExists = true;
		ArrayList<User_Server> participantReferences = new ArrayList<User_Server>();
		for (String i : targetEmails) {
			boolean emailExists = false;
			for (User_Server j : domainController.allUsers) {
				if (j.email.equals(i)) {
					emailExists = true;
					participantReferences.add(j);
					break;
				}
			}
			if (emailExists == false) {
				batchExists = false;
				break;
			}
		}
		if (batchExists) {// success case
			GroupChat_Server newGroupChat = new GroupChat_Server(domainController.allChats.size() + 1, chatName);
			newGroupChat.AddParticipant(user);
			user.allChats.add(newGroupChat);
			newGroupChat.UpdateLanguage(user, "en");
			for (User_Server i : participantReferences) {
				newGroupChat.AddParticipant(i);
				newGroupChat.UpdateLanguage(i, "en");
				i.allChats.add(newGroupChat);
			}
			domainController.allChats.add(newGroupChat);
			sendingBuffer.makePacket(Converter.to_GroupChat_Client(newGroupChat, user), 12);
			sendMessage();
			for (User_Server i : participantReferences) {
				if (i.isOnline && i != user) {
					i.onlineClientHandlerReference.sendMessageToMyClient(
							new NetworkMessage(Converter.to_GroupChat_Client(newGroupChat, i), 7));
				}
			}
		} else {// failure case
			sendingBuffer.makePacket(null, 13);
			sendMessage();
		}
		sendingBuffer.reset();
		receivingBuffer.reset();
	}

	@SuppressWarnings("unchecked")
	private synchronized void ChangeGroupChatLanguage() {
		ArrayList<Object> groupChatLanguageChangeRequestCredentials = (ArrayList<Object>) receivingBuffer.message;
		GroupChat_Server groupChatReference = null;
		for (Chat i : user.allChats) {
			if (i.chatID.equals((Integer) groupChatLanguageChangeRequestCredentials.get(0))) {
				((GroupChat_Server) i).UpdateLanguage(user, (String) groupChatLanguageChangeRequestCredentials.get(1));
				groupChatReference = (GroupChat_Server) i;
				break;
			}
		}
		GroupChat_Client temp = Converter.to_GroupChat_Client(groupChatReference, user);
		sendingBuffer.makePacket(temp, 14);
		sendMessage();
		sendingBuffer.reset();
		receivingBuffer.reset();
	}

	@SuppressWarnings("unchecked")
	private synchronized void AddGroupChatMember() {
		ArrayList<Object> groupChatRequestCredentials = (ArrayList<Object>) receivingBuffer.message;
		Integer chatID = (Integer) groupChatRequestCredentials.get(0);
		String targetEmail = (String) groupChatRequestCredentials.get(1);
		boolean emailExists = false;
		boolean alreadyMember = false;
		User_Server targetParticipantReference = null;
		ArrayList<Object> reply = new ArrayList<Object>();
		GroupChat_Server groupChatReference = null;
		for (User_Server j : domainController.allUsers) {
			if (j.email.equals(targetEmail)) {
				emailExists = true;
				targetParticipantReference = j;
				break;
			}
		}
		if (emailExists) {
			for (Chat i : user.allChats) {
				if (i.chatID.equals(chatID)) {
					groupChatReference = (GroupChat_Server) i;
					for (ChatParticipant j : groupChatReference.allParticipants) {
						if (j.participantReference == targetParticipantReference) {
							alreadyMember = true;
						}
					}
					break;
				}
			}
		}
		if (emailExists && !alreadyMember) {// success case
			groupChatReference.AddParticipant(targetParticipantReference);
			targetParticipantReference.allChats.add(groupChatReference);
			targetParticipantReference.onlineClientHandlerReference.sendMessageToMyClient(new NetworkMessage(
					Converter.to_GroupChat_Client(groupChatReference, targetParticipantReference), 7));
			for (ChatParticipant i : groupChatReference.allParticipants) {
				if (i.participantReference != user && i.participantReference != targetParticipantReference
						&& i.participantReference.isOnline)
					i.participantReference.onlineClientHandlerReference.sendMessageToMyClient(new NetworkMessage(
							Converter.to_GroupChat_Client(groupChatReference, i.participantReference), 16));
			}
			reply.add(emailExists);
			reply.add(alreadyMember);
			reply.add(Converter.to_GroupChat_Client(groupChatReference, user));
			sendingBuffer.makePacket(reply, 15);
			sendMessage();
		} else { // failure case
			reply.add(emailExists);
			reply.add(alreadyMember);
			sendingBuffer.makePacket(reply, 15);
			sendMessage();
		}
		sendingBuffer.reset();
		receivingBuffer.reset();
	}

	@Override
	public void run() {
		while (clientSocket.isClosed() == false) {
			recvMessage();
			if (clientSocket.isClosed() == false) {
				if (receivingBuffer.messageType == 5) {
					ShutdownProtocol();
					return;
				}
				if (user == null) {
					if (receivingBuffer.messageType == 2) {
						SignUpHandler();
					} else if (receivingBuffer.messageType == 3) {
						LoginHandler();
					}
				} else {
					if (receivingBuffer.messageType == 4) {
						AddContact();
					} else if (receivingBuffer.messageType == 8) {
						RelayMessage();
					} else if (receivingBuffer.messageType == 10) {
						ConnectAudioCall();
					} else if (receivingBuffer.messageType == 11) {
						if (waitForClientReply) {
							boolean incomingCallPicked = (boolean) receivingBuffer.message;
							if (incomingCallPicked)
								clientReply = 1;
							else
								clientReply = 0;
							waitForClientReply = false;
						}
					} else if (receivingBuffer.messageType == 12) {
						MakeGroupChat();
					} else if (receivingBuffer.messageType == 14) {
						ChangeGroupChatLanguage();
					} else if (receivingBuffer.messageType == 15) {
						AddGroupChatMember();
					}
				}
				receivingBuffer.reset();
				sendingBuffer.reset();
			}
		}
	}
}
