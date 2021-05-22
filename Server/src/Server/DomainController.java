package Server;

import java.util.ArrayList;

import Shared.Chat;

public class DomainController {

	protected ArrayList<User_Server> allUsers;
	protected ArrayList<OnlineClientHandler> onlineClients;
	protected ArrayList<Chat> allChats;

	public DomainController() {
		this.allUsers = new ArrayList<User_Server>();
		this.allChats = new ArrayList<Chat>();
		this.onlineClients = new ArrayList<OnlineClientHandler>();
	}

	/**
	 * 
	 * @return <strong>ArrayList<String></strong><br/>
	 *         Index 0 ==> emailAlreadyExists: boolean<br/>
	 *         Index 1 ==> userReference: User_Server<br/>
	 */
	public ArrayList<Object> RegisterNewUser(ArrayList<String> signUpCredentials,
			OnlineClientHandler requestingClient) {
		ArrayList<Object> ret = new ArrayList<Object>();
		boolean emailAlreadyExists = false;
		User_Server userReference = null;
		for (User_Server i : allUsers) {
			if (i.email.equals(signUpCredentials.get(1))) {
				emailAlreadyExists = true;
				break;
			}
		}
		if (!emailAlreadyExists) {
			userReference = new User_Server(signUpCredentials.get(0), signUpCredentials.get(1),
					signUpCredentials.get(2), requestingClient);
			allUsers.add(userReference);
		}
		ret.add(emailAlreadyExists);
		ret.add(userReference);
		return ret;
	}

	/**
	 * 
	 * @return <strong>ArrayList<String></strong><br/>
	 *         Index 0 ==> userExists: boolean<br/>
	 *         Index 1 ==> correctPassword: boolean<br/>
	 *         Index 2 ==> alreadyLoggedIn: boolean<br/>
	 *         Index 3 ==> userReference: User_Server<br/>
	 */
	public ArrayList<Object> VerifyLogin(ArrayList<String> loginCredentials, OnlineClientHandler requestingClient) {
		ArrayList<Object> ret = new ArrayList<Object>();
		boolean userExists = false;
		boolean correctPassword = false;
		boolean alreadyLoggedIn = true;
		User_Server userReference = null;
		for (User_Server i : allUsers) {
			if (i.email.equals(loginCredentials.get(0))) {
				userExists = true;
				if (i.password.equals(loginCredentials.get(1))) {
					correctPassword = true;
					if (i.isOnline == false) {
						i.onlineClientHandlerReference = requestingClient;
						alreadyLoggedIn = false;
						userReference = i;
					}
				}
				break;
			}
		}
		ret.add(userExists);
		ret.add(correctPassword);
		ret.add(alreadyLoggedIn);
		ret.add(userReference);
		return ret;
	}

	public void KillOnlineClient(OnlineClientHandler toKill) {
		for (OnlineClientHandler i : onlineClients) {
			if (i == toKill) {
				onlineClients.remove(i);
				break;
			}
		}
	}

	public void DisplayAllUsers() {
		System.out.println("\tALL REGISTERED USERS\n");
		System.out.println("Count: " + allUsers.size());
		if (allUsers.size() > 0) {
			System.out.println();
			int x = 1;
			for (User_Server i : allUsers) {
				System.out.println(x + ". Name:\t" + i.name);
				System.out.println("   Email:\t" + i.email);
				System.out.println("   IsOnline:\t" + i.isOnline);
				System.out.println("   Contacts:");
				System.out.println("\tCount = " + i.contacts.size());
				System.out.println();
				int y;
				if (i.contacts.size() > 0) {
					y = 1;
					for (User_Server j : i.contacts) {
						System.out.println("\t" + y + ". Name:\t" + j.name);
						System.out.println("\t   Email:\t" + j.email);
						y++;
					}
				}
				System.out.println("   Chats:");
				System.out.println("\tCount = " + i.allChats.size());
				System.out.println();
				if (i.allChats.size() > 0) {
					y = 1;
					for (Chat j : i.allChats) {
						if (j.chatType == 0) {
							System.out.println("\t" + y + ". Chat Room ID:\t" + j.chatID);
							System.out.println("\t   Type: Personal Chat Room");
						} else {
							System.out.println("\t" + y + ". Chat Room ID:\t" + j.chatID);
							System.out.println("\t   Type: Group Chat Room");
						}
					}
				}
				x++;
			}
		}
	}

	public void DisplayOnlineClients() {
		System.out.println("\tALL ONLINE CLIENT MACHINES\n");
		System.out.println("Count: " + onlineClients.size());
		if (onlineClients.size() > 0) {
			System.out.println();
			int x = 1;
			for (OnlineClientHandler i : onlineClients) {
				System.out.println(x + ". IP Address:\t" + i.clientSocket.getLocalAddress().getHostAddress());
				System.out.println("   Port Number:\t" + i.clientSocket.getPort());
				System.out.print("   Logged In User: ");
				if (i.user == null) {
					System.out.println("None");
				} else {
					System.out.println();
					System.out.println("   \tName:\t" + i.user.name);
					System.out.println("   \tEmail:\t" + i.user.email);
					x++;
				}
			}
		}
	}

}
