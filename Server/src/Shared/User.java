package Shared;

import java.io.Serializable;

public abstract class User implements Serializable {
	private static final long serialVersionUID = 8863546714961524356L;
	public String name;
	public String email;

	public User() {
		name = null;
		email = null;
	}

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}
}