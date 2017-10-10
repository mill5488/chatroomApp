// ClientInfo Class for Multithreaded Chat Application 
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// ClientInfo class. This is the information for a specific user and Client. 
public class ClientInfo {

	public String username = null; // username of the user
	public String password = null; // Password of the user 
	public Socket mySock = null; // Socket of the Client
  	public Listener myListener = null; // Listener Thread for the Client 
    	public Sender mySender = null; // Sender Thread for the Client
	public boolean active = false; // true means logged in, false means logged out.

	// Constructor for ClientInfo
	public ClientInfo(String a, String b, Socket c, Listener d, Sender e, Boolean f) {
		username = a;
		password = b;
		mySock = c;
		myListener = d;
		mySender = e;
		active = f;

	} 

	// Grabs Username of this Client's ClientInfo
	public String getUsername() {

		return username;

	}

	// Grabs password of this Client's ClientInfo
	public String getPassword() {

		return password;

	}

	// Grabs the socket of this Client's ClientInfo
	public Socket getSocket() {

		return mySock;

	}

	// Grabs the Listener Thread of this Client's ClientInfo
	public Listener getListener() {

		return myListener;

	}

	// Grabs the Sender Thread of this Client's ClientInfo
	public Sender getSender() {

		return mySender;
	
	}

	// returns true if Client is active (user logged in), false if not. 
	public boolean is_active() {

		return active;
	
	}

	// The good old Override for printing classes. 
	@Override
	public String toString() {

		return "Username: " + username + " " + "Password: " + password + " ";

	}

	// Routines for changing the logging in/logging out status this Client's user.
	public void setActive() {
		active = true;

	}

	public void setInactive() {
		active = false;

	}

}

