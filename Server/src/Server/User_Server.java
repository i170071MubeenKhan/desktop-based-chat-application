package Server;

import java.util.ArrayList;

import Shared.Chat;
import Shared.User;

public class User_Server extends User {
	private static final long serialVersionUID = -7025540926714166558L;
	protected String password;
	protected boolean isOnline;
	protected ArrayList<User_Server> contacts;
	protected ArrayList<Chat> allChats;
	protected OnlineClientHandler onlineClientHandlerReference;

	public User_Server(String name, String email, String password, OnlineClientHandler onlineClientHandlerReference) {
		super(name, email);
		this.password = password;
		this.contacts = new ArrayList<User_Server>();
		this.allChats = new ArrayList<Chat>();
		this.isOnline = true;
		this.onlineClientHandlerReference = onlineClientHandlerReference;
	}

	// this parameterized constructor will be used when loading from database
	public User_Server(String name, String email, String password, boolean isOnline,
			OnlineClientHandler onlineClientHandlerReference) {
		super(name, email);
		this.password = password;
		this.contacts = new ArrayList<User_Server>();
		this.allChats = new ArrayList<Chat>();
		this.isOnline = isOnline;
		this.onlineClientHandlerReference = onlineClientHandlerReference;
	}

	public void setContacts(ArrayList<User_Server> contacts) {
		this.contacts = contacts;

	}

	public void setAllChats(ArrayList<Chat> allChats) {
		this.allChats = allChats;

	}
}
