package Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import Shared.NetworkMessage;
import Shared.TextMessage_Client;
import Shared.PersonalChat_Client;
import Shared.AttachmentMessage_Client;
import Shared.Chat;
import Shared.GroupChat_Client;
import Shared.Message_Client;
import Shared.PersonalChat_Client.ChatParticipant;
import Shared.User_Client;
import application.universalController;

public class Client extends Thread {

	public Socket socket;
	private OutputStream outputStream;
	private ObjectOutputStream objectOutputStream;
	private InputStream inputStream;
	private ObjectInputStream objectInputStream;
	public User_Client user;
	private NetworkMessage receivingBuffer;
	public NetworkMessage sendingBuffer;
	private boolean waitForServerReply;
	private int serverReply;
	private Scanner input;
	private AudioCall audioCall;
	public boolean incomingCall;
	public boolean incomingCallPicked;
	private ArrayList<Object> targetClientAudioCallReaction;
	private InetAddress incomingCallSourceAddress;
	private boolean onCall;
	public universalController uc;
	public File attachments;

	public Client() {
		try {
			socket = new Socket("192.168.100.14", 8080);
			outputStream = socket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(outputStream);
			inputStream = socket.getInputStream();
			objectInputStream = new ObjectInputStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		receivingBuffer = new NetworkMessage();
		sendingBuffer = new NetworkMessage();
		user = new User_Client();
		waitForServerReply = false;
		serverReply = 0;
		input = new Scanner(System.in);
		audioCall = null;
		incomingCall = false;
		targetClientAudioCallReaction = null;
		incomingCallSourceAddress = null;
		onCall = false;
	}

	@SuppressWarnings("unchecked")
	public int SignUp(ArrayList<String> signUpCredentials) {
		System.out.println("Attempting Sign Up");
		int tmp = -1;
		sendingBuffer.makePacket(signUpCredentials, 2);
		sendMessage();
		recvMessage();
		if (receivingBuffer.messageType == 1) {
			user = (User_Client) ((ArrayList<Object>) receivingBuffer.message).get(1);
			System.out.println("Sign Up successful");
			this.start();
			tmp = 1;
		} else if (receivingBuffer.messageType == 0) {
			System.out.println("Sign Up Failed");
			tmp = 0;
		} else if (receivingBuffer.messageType == -1) {
			System.out.println("Unrecognizable Request");
			tmp = -1;
		}
		sendingBuffer.reset();
		receivingBuffer.reset();
		return tmp;
	}

	@SuppressWarnings("unchecked")
	public int Login(ArrayList<String> loginCredentials) {
		System.out.println("Attempting Login");
		sendingBuffer.makePacket(loginCredentials, 3);
		int tmp = -1;
		sendMessage();
		recvMessage();
		if (receivingBuffer.messageType == 1) { // success
			System.out.println("Login successful");
			user = (User_Client) ((ArrayList<Object>) receivingBuffer.message).get(3);
			this.start();
			tmp = 1;
		} else if (receivingBuffer.messageType == 0) {// error //email=0 or
														// password=1 incorrect
														// or alreadyloggedIn=2
			System.out.println("Login Failed");
			tmp = 0;
		} else if (receivingBuffer.messageType == -1) {
			System.out.println("Unrecognizable Request");
			tmp = -1;
		}
		sendingBuffer.reset();
		receivingBuffer.reset();
		return tmp;
	}

	public synchronized void SendSuicideNote() {
		sendingBuffer.makePacket(null, 5);
		sendMessage();
		CloseResources();
		sendingBuffer.reset();
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
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Shutting Down");
			System.exit(0);
		}
	}

	private synchronized void CloseResources() {
		try {
			objectOutputStream.close();
			objectInputStream.close();
			outputStream.close();
			inputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized String getClientName() {
		return user.name;
	}

	/**
	 *
	 * @param targetEmail
	 * @return<br/>
	 * 				0 ==> Null<br/>
	 *              1 ==> Success<br/>
	 *              2 ==> Email Does Not Exist<br/>
	 *              3 ==> Already A Contact<br/>
	 */
	public synchronized int SendAddContactRequest(String targetEmail) {
		System.out.println("Sending AddContact Request");
		sendingBuffer.makePacket(targetEmail, 4);
		sendMessage();
		waitForServerReply = true;
		while (waitForServerReply != false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitForServerReply = false;
		int toReturn = serverReply;
		serverReply = 0;
		sendingBuffer.reset();
		receivingBuffer.reset();
		return toReturn;
	}

	public synchronized void SendTextMessage(Chat targetChat, String textMessage) {
		System.out.println("Sending Text Message");
		if (textMessage == null)
			return;
		if (textMessage.equals(""))
			return;
		TextMessage_Client newTextMessage = new TextMessage_Client(user.name, user.email, textMessage);
		ArrayList<Object> envelopedMessage = new ArrayList<Object>();
		envelopedMessage.add(targetChat.chatID);
		envelopedMessage.add(newTextMessage);
		sendingBuffer.makePacket(envelopedMessage, 8);
		sendMessage();
		if (targetChat.chatType == 0) {
			((PersonalChat_Client) targetChat).allMessages.add(newTextMessage);
		} else if (targetChat.chatType == 1) {
			((GroupChat_Client) targetChat).allMessages.add(newTextMessage);
		}
		sendingBuffer.reset();
		receivingBuffer.reset();
		System.out.println("Sent");
	}

	public synchronized void FlipOnlineStatus() {
		for (Chat i : user.allChats) {
			if (i.chatType == 0) {
				for (ChatParticipant j : ((PersonalChat_Client) i).allParticipants) {
					if (j.participantEmail.equals((String) receivingBuffer.message)) {
						j.participantIsOnline = !j.participantIsOnline;
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void HandleIncomingMessage() {
		System.out.println("Received Incoming Message");
		ArrayList<Object> envelopedMessage = (ArrayList<Object>) receivingBuffer.message;
		for (Chat i : user.allChats) {
			if (i.chatID.equals((Integer) envelopedMessage.get(0))) {
				if (i.chatType == 0) {
					if (((Message_Client) envelopedMessage.get(1)).messageType == 0) {
						((PersonalChat_Client) i).allMessages.add((TextMessage_Client) envelopedMessage.get(1));
					} else {
						((PersonalChat_Client) i).allMessages.add((AttachmentMessage_Client) envelopedMessage.get(1));

						String path = "Attachments From " + ((Message_Client) envelopedMessage.get(1)).senderName + "/"
								+ ((AttachmentMessage_Client) envelopedMessage.get(1)).getContent();
						this.attachments = new File(path);
						if (!attachments.exists()) {
							File directory = new File(
									"Attachments From " + ((Message_Client) envelopedMessage.get(1)).senderName);
							if (!directory.exists()) {
								directory.mkdir();
							}
							try {
								attachments.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							try (FileOutputStream stream = new FileOutputStream(path)) {
								stream.write(((AttachmentMessage_Client) envelopedMessage.get(1)).content);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else if (i.chatType == 1) {
					if (((Message_Client) envelopedMessage.get(1)).messageType == 0) {
						((GroupChat_Client) i).allMessages.add((TextMessage_Client) envelopedMessage.get(1));
					} else {
						((GroupChat_Client) i).allMessages.add((AttachmentMessage_Client) envelopedMessage.get(1));
					}
				}
			}
		}
		sendingBuffer.reset();
		receivingBuffer.reset();
	}

	private void StartAudioCall(InetAddress targetClientIP) {
		System.out.println("Starting Audio Call");
		audioCall = new AudioCall(targetClientIP, 5000);
		audioCall.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void EndAudioCall() {
		if (audioCall != null) {
			audioCall.end();
			audioCall = null;
			onCall = false;
		}
	}

	public synchronized int SendAudioCallRequest(PersonalChat_Client targetChat) {
		sendingBuffer.makePacket(targetChat.chatID, 10);
		sendMessage();
		waitForServerReply = true;
		while (waitForServerReply != false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("1");
		if ((boolean) targetClientAudioCallReaction.get(0)) {
			System.out.println("andar");
			onCall = true;
			StartAudioCall((InetAddress) targetClientAudioCallReaction.get(1));
			System.out.println("2");
			targetClientAudioCallReaction = null;
			waitForServerReply = false;
			sendingBuffer.reset();
			receivingBuffer.reset();
			return 1;
		}
		System.out.println("bahir");
		targetClientAudioCallReaction = null;
		waitForServerReply = false;
		sendingBuffer.reset();
		receivingBuffer.reset();
		return 0;
	}

	public synchronized void HandleIncomingCall() {
		while (incomingCall != false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// incomingCallPicked = true;
		sendingBuffer.makePacket(incomingCallPicked, 11);
		sendMessage();
		if (incomingCallPicked == true) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			onCall = true;
			StartAudioCall(incomingCallSourceAddress);
		}
		incomingCallSourceAddress = null;
		sendingBuffer.reset();
		receivingBuffer.reset();
	}

	public synchronized int SendGroupChatRequest(String chatName, ArrayList<String> targetEmails) {
		System.out.println("Sending Group Chat Request");
		ArrayList<Object> groupChatRequestCredentials = new ArrayList<Object>();
		groupChatRequestCredentials.add(chatName);
		groupChatRequestCredentials.add(targetEmails);
		sendingBuffer.makePacket(groupChatRequestCredentials, 12);
		sendMessage();
		waitForServerReply = true;
		while (waitForServerReply != false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitForServerReply = false;
		int toReturn = serverReply;
		serverReply = 0;
		sendingBuffer.reset();
		receivingBuffer.reset();
		return toReturn;
	}

	public void ChangeGroupChatLanguage(GroupChat_Client chatReference, String language) {
		System.out.println("Changing Group Chat Language");
		ArrayList<Object> groupChatLanguageChangeRequestCredentials = new ArrayList<Object>();
		groupChatLanguageChangeRequestCredentials.add(chatReference.chatID);
		groupChatLanguageChangeRequestCredentials.add(language);
		sendingBuffer.makePacket(groupChatLanguageChangeRequestCredentials, 14);
		sendMessage();
		waitForServerReply = true;
		while (waitForServerReply != false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitForServerReply = false;
		sendingBuffer.reset();
		receivingBuffer.reset();
	}

	public int AddGroupChatMember(GroupChat_Client chatReference, String targetEmail) {
		System.out.println("Add Member to Group Chat ");
		ArrayList<Object> groupChatRequestCredentials = new ArrayList<Object>();
		groupChatRequestCredentials.add(chatReference.chatID);
		groupChatRequestCredentials.add(targetEmail);
		sendingBuffer.makePacket(groupChatRequestCredentials, 15);
		sendMessage();
		waitForServerReply = true;
		while (waitForServerReply != false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitForServerReply = false;
		int toReturn = serverReply;
		serverReply = 0;
		sendingBuffer.reset();
		receivingBuffer.reset();
		return toReturn;
	}

	public void setController(universalController a) {
		this.uc = a;
	}

	public synchronized void SendAttachmentMessage(Chat targetChat, File file) {
		System.out.println("Sending Attachment Message");
		if (file == null)
			return;
		if (file.equals(""))
			return;
		byte[] content = null;
		try {
			content = Files.readAllBytes(file.toPath());
			System.out.println("The bytes of the content is" + content);
			System.out.println("the name of the file is" + file.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		AttachmentMessage_Client newAttachmentMessage = new AttachmentMessage_Client(user.name, user.email, content,
				file);
		ArrayList<Object> envelopedMessage = new ArrayList<Object>();
		envelopedMessage.add(targetChat.chatID);
		envelopedMessage.add(newAttachmentMessage);
		sendingBuffer.makePacket(envelopedMessage, 8);
		sendMessage();
		if (targetChat.chatType == 0) {
			((PersonalChat_Client) targetChat).allMessages.add(newAttachmentMessage);
		} else if (targetChat.chatType == 1) {
			((GroupChat_Client) targetChat).allMessages.add(newAttachmentMessage);
		}
		sendingBuffer.reset();
		receivingBuffer.reset();
		System.out.println("Sent");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		System.out.println("Thread Started");
		while (true) {
			recvMessage();
			System.out.println("Message Received, Type: " + receivingBuffer.messageType);
			if (receivingBuffer.messageType == 4) {
				if (waitForServerReply) {
					user.allChats.add((PersonalChat_Client) (receivingBuffer.message));
					serverReply = 1;
					waitForServerReply = false;
				}
			} else if (receivingBuffer.messageType == 6) {
				if (waitForServerReply) {
					boolean emailExists = (boolean) (((ArrayList<Object>) receivingBuffer.message)).get(0);
					boolean alreadyContact = (boolean) (((ArrayList<Object>) receivingBuffer.message)).get(0);
					if (emailExists == false)
						serverReply = 2;
					else if (alreadyContact == false) {
						serverReply = 3;
					}
					waitForServerReply = false;
				}
			} else if (receivingBuffer.messageType == 7) {
				user.allChats.add(((Chat) receivingBuffer.message));
				uc.detectChange(7);

				System.out.println("New Chat Added");
			} else if (receivingBuffer.messageType == 8) {
				HandleIncomingMessage();
				uc.detectChange(8);
			} else if (receivingBuffer.messageType == 9) {
				FlipOnlineStatus();
				uc.detectChange(9);
			} else if (receivingBuffer.messageType == 10) {
				if (waitForServerReply) {
					targetClientAudioCallReaction = (ArrayList<Object>) receivingBuffer.message;
					waitForServerReply = false;
				}
				uc.detectChange(10);
			} else if (receivingBuffer.messageType == 11) {
				System.out.println("handling detect call");
				if (!onCall) {
					incomingCallSourceAddress = (InetAddress) receivingBuffer.message;
					incomingCall = true;
					uc.detectChange(11);
					HandleIncomingCall();
				} else {
					boolean temp = false;
					sendingBuffer.makePacket(temp, 11);
					sendMessage();
					incomingCallSourceAddress = null;
					sendingBuffer.reset();
					receivingBuffer.reset();
				}
			} else if (receivingBuffer.messageType == 12) {
				if (waitForServerReply) {
					user.allChats.add((GroupChat_Client) (receivingBuffer.message));
					serverReply = 1;
					waitForServerReply = false;

				}
			} else if (receivingBuffer.messageType == 13) {
				if (waitForServerReply) {
					serverReply = 2;
					waitForServerReply = false;
				}
			} else if (receivingBuffer.messageType == 14) {
				if (waitForServerReply) {
					for (Chat i : user.allChats) {
						if (i.chatID.equals(((GroupChat_Client) receivingBuffer.message).chatID)) {
							user.allChats.set(user.allChats.indexOf(i), (GroupChat_Client) receivingBuffer.message);
							System.out.println("Changed");
							break;
						}
					}
					waitForServerReply = false;
				}
			} else if (receivingBuffer.messageType == 15) {
				if (waitForServerReply) {
					ArrayList<Object> reply = (ArrayList<Object>) receivingBuffer.message;
					if ((boolean) reply.get(0) && !(boolean) reply.get(1)) { // success
						for (Chat i : user.allChats) {
							if (i.chatID.equals(((GroupChat_Client) reply.get(2)).chatID)) {
								user.allChats.set(user.allChats.indexOf(i), ((GroupChat_Client) reply.get(2)));
								System.out.println("Added and updated");
								break;
							}
						}
						serverReply = 1;
					} else { // failure
						serverReply = 0;
					}
					waitForServerReply = false;

					uc.detectChange(15);// show error if failure
				}
			} else if (receivingBuffer.messageType == 16) {
				for (Chat i : user.allChats) {
					if (i.chatID.equals(((GroupChat_Client) receivingBuffer.message).chatID)) {
						user.allChats.set(user.allChats.indexOf(i), (GroupChat_Client) receivingBuffer.message);
						System.out.println("Updated. New participant ADDED");
						break;
					}
				}
			}
			receivingBuffer.reset();
			sendingBuffer.reset();
		}
	}

	public void CLIMenu() {
		Thread menu = new Thread() {
			@Override
			public void run() {
				System.out.println("\t\t||==================Client Side==================||");
				System.out.println("\t\t\t\tPort Number: " + socket.getLocalPort());
				try {
					System.out.println("\t\t\t\tIP Address: " + InetAddress.getLocalHost().getHostAddress());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				System.out.println("\t\t||===============================================||\n\n");
				String choice = "";
				while (!choice.equals("0")) {
					printMainMenu();
					choice = input.nextLine();
					System.out.println();
					if (choice.equals("1")) {
						System.out.println("\n\t\tSIGN UP\n");
						ArrayList<String> arr = new ArrayList<String>();
						System.out.print("Name: ");
						arr.add(input.nextLine());
						System.out.print("Email: ");
						arr.add(input.nextLine());
						System.out.print("Password: ");
						arr.add(input.nextLine());
						int x = SignUp(arr);
						if (x == 1)
							System.out.println("Success");
						else
							System.out.println("Fail");
					} else if (choice.equals("2")) {
						System.out.println("\n\t\tLOG IN\n");
						ArrayList<String> arr = new ArrayList<String>();
						System.out.print("Email: ");
						arr.add(input.nextLine());
						System.out.print("Password: ");
						arr.add(input.nextLine());
						int x = Login(arr);
						if (x == 1)
							System.out.println("Success");
						else
							System.out.println("Fail");
					} else if (choice.equals("3")) {
						System.out.println("\n\t\tADD CONTACT\n");
						System.out.print("Email: ");
						int x = SendAddContactRequest(input.nextLine());
						if (x == 1)
							System.out.println("Success");
						else
							System.out.println("Fail");
					} else if (choice.equals("4")) {
						System.out.println("\n\t\tVIEW CHATS\n");
						for (Chat i : user.allChats) {
							System.out.println("ChatID:\t" + i.chatID);
							System.out.println("ChatType:\t" + i.chatType);
							if (i.chatType == 0) {
								System.out.println("Participants:");
								for (ChatParticipant j : ((PersonalChat_Client) i).allParticipants) {
									System.out.println("Email:\t" + j.participantEmail);
									System.out.println("IsOnline:\t" + j.participantIsOnline);
								}
								System.out.println("Messages: ");
								for (Message_Client j : ((PersonalChat_Client) i).allMessages) {
									System.out.println("Sender Name:\t" + j.senderName);
									System.out.println("Sender Email:\t" + j.senderEmail);
									System.out.println("TimeStamp:\t" + j.timeStamp);
									System.out.println("Message Type:\t" + j.messageType);
									if (j.messageType == 0) {
										System.out.println("Message:\t" + ((TextMessage_Client) j).content);
									}
									System.out.println();
								}
								System.out.println();
							} else if (i.chatType == 1) {
								System.out.println("Participants:");
								for (Shared.GroupChat_Client.ChatParticipant j : ((GroupChat_Client) i).allParticipants) {
									System.out.println("Name:\t" + j.participantName);
									System.out.println("Email:\t" + j.participantEmail);
									System.out.println();
								}
								System.out.println("Messages: ");
								System.out.println(((GroupChat_Client) i).sourceLanguage);
								System.out.println(((GroupChat_Client) i).allMessages.size());
								for (Message_Client j : ((GroupChat_Client) i).allMessages) {
									System.out.println("Sender Name:\t" + j.senderName);
									System.out.println("Sender Email:\t" + j.senderEmail);
									System.out.println("TimeStamp:\t" + j.timeStamp);
									System.out.println("Message Type:\t" + j.messageType);
									if (j.messageType == 0) {
										System.out.println("Message:\t" + ((TextMessage_Client) j).content);
									}
									System.out.println();
								}
								System.out.println();
							}
						}
					} else if (choice.equals("5")) {
						System.out.println("\n\t\tSEND MESSAGE\n");
						System.out.print("Chat ID: ");
						String chatID = input.nextLine();
						Chat chatReference = null;
						boolean chatFound = false;
						for (Chat i : user.allChats) {
							if (i.chatID == Integer.parseInt(chatID)) {
								chatReference = i;
								chatFound = true;
							}
						}
						if (chatFound == true) {
							System.out.print("Message: ");
							SendTextMessage(chatReference, input.nextLine());
						} else {
							System.out.println("Chat Not Found");
						}
					} else if (choice.equals("6")) {
						System.out.println("\n\t\tSTART AUDIO CALL\n");
						System.out.print("Chat ID: ");
						String chatID = input.nextLine();
						Chat chatReference = null;
						boolean chatFound = false;
						for (Chat i : user.allChats) {
							if (i.chatID == Integer.parseInt(chatID)) {
								chatReference = i;
								chatFound = true;
							}
						}
						if (chatFound == true) {
							System.out.print("Starting Call: ");
							SendAudioCallRequest((PersonalChat_Client) chatReference);
						} else {
							System.out.println("Chat Not Found");
						}
					} else if (choice.equals("7")) {
						System.out.println("\n\t\tEND AUDIO CALL\n");
					} else if (choice.equals("8")) {
						System.out.println("\n\t\tMAKE GROUP CHAT\n");
						System.out.print("Chat Name: ");
						String chatName = input.nextLine();
						ArrayList<String> targetEmails = new ArrayList<String>();
						System.out.print("Email-2: ");
						targetEmails.add(input.nextLine());
						System.out.print("Email-3: ");
						targetEmails.add(input.nextLine());
						int x = SendGroupChatRequest(chatName, targetEmails);
						if (x == 1)
							System.out.println("Success");
						else
							System.out.println("Fail");
					} else if (choice.equals("9")) {
						System.out.println("\n\t\tCHANGE GROUP CHAT LANGUAGE\n");
						System.out.print("Chat ID: ");
						String chatID = input.nextLine();
						Chat chatReference = null;
						boolean chatFound = false;
						for (Chat i : user.allChats) {
							if (i.chatID == Integer.parseInt(chatID)) {
								chatReference = i;
								chatFound = true;
							}
						}
						if (chatFound == true) {
							System.out.print("Language: ");
							ChangeGroupChatLanguage((GroupChat_Client) chatReference, input.nextLine());
						} else {
							System.out.println("Chat Not Found");
						}
					} else if (choice.equals("10")) {
						System.out.println("\n\t\tCHANGE GROUP CHAT LANGUAGE\n");
						System.out.print("Chat ID: ");
						String chatID = input.nextLine();
						Chat chatReference = null;
						boolean chatFound = false;
						for (Chat i : user.allChats) {
							if (i.chatID == Integer.parseInt(chatID)) {
								chatReference = i;
								chatFound = true;
							}
						}
						if (chatFound == true) {
							System.out.print("Member Email: ");
							int x = AddGroupChatMember((GroupChat_Client) chatReference, input.nextLine());
							if (x == 0) {
								System.out.println("Success");
							} else {
								System.out.println("Failed");
							}
						} else {
							System.out.println("Chat Not Found");
						}
					}
				}
				SendSuicideNote();
			}
		};
		menu.start();
	}

	private void printMainMenu() {
		System.out.println("\t\t\tMAIN MENU\n");
		System.out.println("1.   Sign Up");
		System.out.println("2.   Log In");
		System.out.println("3.   Add Contact");
		System.out.println("4.   View Chats");
		System.out.println("5.   Send Message");
		System.out.println("6.   Start Audio Call");
		System.out.println("7.   End Audio Call");
		System.out.println("8.   Make a Group Chat");
		System.out.println("9.   Change Group Chat Language");
		System.out.println("10.  Add participant in Group Chat");
		System.out.println("0.   Exit.");
		System.out.print("Your Choice: ");
	}

}
