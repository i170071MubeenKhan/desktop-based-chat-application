package Shared;

import java.io.Serializable;
import java.util.ArrayList;

public class User_Client extends User implements Serializable {
	private static final long serialVersionUID = 4965378208197900267L;
	public ArrayList<Chat> allChats;

	public User_Client() {
		super();
		allChats = new ArrayList<Chat>();
	}

	public User_Client(String name, String email) {
		super(name, email);
		this.allChats = new ArrayList<Chat>();
	}
}
