// Handler Class for Multithreaded Chat Application 
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// Handler Class, this is the Server's work Thread or the Master Thread for the server
public class Handler extends Thread {

	// Error Codes.
	String Success = "Server: " + "0x00";
	String badInfo = "Server: " + "0x01";
	String dupl = "Server: " + "0x02";
	String badFID = "Server: " + "0x03";
	String badPort = "Server: " + "0x04";
	String badFormat = "Server: " + "0xFF";
	
	private ClientList regList = null; // Declaration of list of registered Users
	ArrayList<ClientInfo> list = null; // Declaration of arrayList for registered users

	Handler(String s) throws FileNotFoundException { // Get filename to build registered users list from

		try {
			regList = new ClientList(s); // Build the list of Registered users
			list = regList.getList(); // Transform it into an arraylist

		}
	

		catch (Exception el) { // This means the user screwed up, no file.
			System.out.println(el.getMessage());

		}

	}

    	private Vector myMessages = new Vector(); // Vector for message queue (Strings)
    	private Vector myClients = new Vector(); // Vector for Operating Clients (Threads)
	private Vector myActives = new Vector(); // Vector for Active Clients (User is logged in)

	private Vector myFileList = new Vector(); // Vector for FileList

	private int nextID = 1; // the file_ID for the next file that is put up.

	// Method to add a File to the FileList
	private synchronized boolean addFile(String fname, String addr, String pn, ClientInfo rInfo) {

		Socket sock = rInfo.mySock;
        	String senderIP = sock.getInetAddress().getHostAddress();
        	String senderPort = "" + sock.getPort();

		if (addr.equals(senderIP) == false) {
			singleMsg(badPort, rInfo); // Send to the Requesting Client
			System.out.println("your IP is: " + addr);
			System.out.println("senderIP is: " + senderIP);
			
			return false;

		}

		else {

			FileInfo fileToAdd = new FileInfo(null, 0, null, null, null);

			fileToAdd.filename = fname;
			fileToAdd.fileID = nextID;
			fileToAdd.IP_addr = addr;
			fileToAdd.port = pn;
			fileToAdd.owner = rInfo.username;
			myFileList.add(fileToAdd);

			nextID = nextID + 1; // Increment to keep file ID's unique.
		
			return true;

		}
		
	}

	// Method to remove a file from the FileList
	public synchronized void removeFile(FileInfo aFile) {
		int idx = myFileList.indexOf(aFile); // Grab the index of the file

		if (idx != -1) { // Error Checking
			myFileList.removeElementAt(idx); // Remove the file from the list.

		}
	
	}


	// Removes all the files a given Client has up.
	public synchronized boolean removeFiles(ClientInfo client) {
		if (myFileList.isEmpty()) { // Error checking
			System.out.println("FLIST empty."); // Server log.			

			return true;

		}

		int counter = 0;
		String rname = client.username;
		String oname = "";
		int count = 0;
		
		boolean anyFiles = false;

		for (int i = 0; i < myFileList.size(); i++) { // For each Logged in Client,
			Object o = myFileList.elementAt(i); // Grab it,

			if (o instanceof FileInfo) { // cast to FileInfo, so we can extract data
				FileInfo b = (FileInfo)o;
				int item2 = b.fileID; // Extract file ID
				String i2 = Integer.toString(item2);
				oname = b.owner;

				if (oname.equals(rname)) {
					count = count + 1;
					anyFiles = true;
	
				}
			
			}
		
		}

		if (anyFiles == true) {

			System.out.println(); // Server log.
		

			while (counter != count) {

				for (int i = 0; i < myFileList.size(); i++) { // For each Logged in Client,
					Object o = myFileList.elementAt(i); // Grab it,

					if (o instanceof FileInfo) { // cast to FileInfo, so we can extract data
						FileInfo b = (FileInfo)o;
						int item2 = b.fileID; // Extract file ID
						String i2 = Integer.toString(item2);
						oname = b.owner;

						if (oname.equals(rname)) {
							counter = counter + 1;
							int index = myFileList.indexOf(b);

							if (index != -1) {
								myFileList.removeElementAt(index);

							}				
						
						}
			
					}
		
				}

			}

		}

		else {
			System.out.println("User: " + rname + "had no files to remove."); // Server log.

			return true;

		}
		
		return true;

	}

	// Adds a new operating client to its vector.
    	public synchronized void addClient(ClientInfo aClientsInfo) {
        	myClients.add(aClientsInfo);

	}
 
	// Removes an Operating (And possibly Active) Client, usually from DISCONNECT request from client. 
    	public synchronized void removeClient(ClientInfo aClientsInfo) {

		System.out.println("Attempting to remove User: " + aClientsInfo.username); // Server log		

		String rname = "";
		String oname = "";
        	int index = myClients.indexOf(aClientsInfo); // Find where it is in the operating Vector
		int activeIndex = myActives.indexOf(aClientsInfo); // Find where it is in the Active vector
		rname = aClientsInfo.username;

        	if (index != -1) {

			System.out.println("Attempting to remove User: " + aClientsInfo.username + "'s files."); // Server log
			boolean rvm_suc = removeFiles(aClientsInfo);

			if (rvm_suc = true) {
				System.out.println("Successfully removed User: " + aClientsInfo.username + "'s files."); // Server log.

			}

			System.out.println("Proceeding to remove User: " + aClientsInfo.username); // Server log

			aClientsInfo.active = false; // Set Client to inactive, need this to relogin after logging out
			myActives.removeElementAt(activeIndex); // remove them from the active vector
           		myClients.removeElementAt(index); // remove them from the operating vector
			aClientsInfo.setInactive(); // Double sure to set them to inactive.

		}

		// Kill the removed Client's threads
		aClientsInfo.mySender.interrupt();
		aClientsInfo.myListener.interrupt();

    	}

	// Method to get retrive a file from the designated Client's corresponding socket.
	public synchronized String[] getFile(String fID, ClientInfo rInfo) {
		String[] rs = new String[2];
		rs[0] = ""; // IP
		rs[1] = ""; // Port
		int ifID = 0; // We don't ever use 0 for file ids, mine start at 1.

		Scanner s = new Scanner(fID); //Grab the int from the string
		ifID = s.nextInt();

		if (ifID >= nextID || ifID <= 0) { // Error Checking
			singleMsg(badFID, rInfo);	
			
			return rs;

		}

		else {

			for (int i = 0; i < myFileList.size(); i++) { // For each file, 
				FileInfo fInfo = (FileInfo) myFileList.get(i); // Grab it from the vector

				if (fInfo.fileID == ifID) { // If it's the file we want, 
					rs[0] = fInfo.IP_addr; // return the information
					rs[1] = fInfo.port;

					return rs;

				}

			}

		}

		System.out.println("Couldn't Retrieve the file: " + fID); // Debugging, should never happen

		return rs;

	}

	// Prints list of files, this is the subroutine for FLIST.
	public synchronized void printFLIST(ClientInfo rInfo) {
		String fList = ""; // String for File List output

			// The fun stuff, for loop through whatever I use for this... 
			for (int i = 0; i < myFileList.size(); i++) { // For each Logged in Client, 
				Object o = myFileList.elementAt(i); // Grab it,

				if (o instanceof FileInfo) { // cast to FileInfo, so we can extract data
					FileInfo b = (FileInfo)o;
					String item = b.filename; // Extract username
					int item2 = b.fileID; // Extract file ID
					String id = Integer.toString(item2);

					fList = fList + id + ","+ item; // Add username to the list

					// This just determines if we need a comma to separate
					if (i < myFileList.size() - 1) {
						fList = fList + ", ";

					}
			
				}
		
			}

		if (fList.equals("")) { // Don't want comma

			// fList = Success + fList;
			singleMsg(Success, rInfo); // Send to the Requesting Client

		}

		else {

			fList = Success + ", " + fList;
			singleMsg(fList, rInfo); // Send to the Requesting Client

		}

	}
	
	// Prints list of Logged-In users. This is the subroutine for CLIST.
	public synchronized void printActives(ClientInfo rInfo) {
		String cList = ""; // String for Client List output

			for (int i = 0; i < myActives.size(); i++) { // For each Logged in Client, 
				Object o = myActives.elementAt(i); // Grab it,

				if (o instanceof ClientInfo) { // cast to ClientInfo, so we can extract data
					ClientInfo b = (ClientInfo)o;
					String item = b.username; // Extract username

					cList = cList + item; // Add username to the list

					// This just determines if we need a comma to separate
					if (i < myActives.size() - 1) {
						cList = cList + ",";

					}
			
				}
		
			}

		cList = Success + ", " + cList; // Don't need to worry about 0 users, has to be 1 active user to call this method.

		singleMsg(cList, rInfo); // Send to the Requesting Client

	}

	// Prints list of Operating Clients. This is used for Debugging. 
	public synchronized void printUsers(ClientInfo rInfo) {
		String cList = "";

		for (int i = 0; i < myClients.size(); i++) {
			Object o = myClients.elementAt(i);

			if (o instanceof ClientInfo) {
				ClientInfo b = (ClientInfo)o;
				String item = b.username;
				cList = cList + item;
				if (i < myClients.size() - 1) { 
					cList = cList + ",";

				}
			
			}
		
		}

		cList = Success + ", " + cList;

		singleMsg(cList, rInfo); // Return to requesting client

	}

	// Prints list of all Registered Clients, both from file and registered during session. This is used for Debugging. 
	public synchronized void regClients(ClientInfo rInfo) {
		String cList = "";

		for (int i = 0; i < list.size(); i++) {
			cList = cList + list.get(i).username;
			if (i < list.size() - 1) { 
				cList = cList + ",";

			}
		
		}

		cList = Success + ", " + cList;

		singleMsg(cList, rInfo); // Return to requesting client

	}

	// Prints socket info for the calling Client.
	public synchronized void printSock(ClientInfo rInfo) {
		String cList = "";

		Socket sock = rInfo.mySock;
        	String senderIP = sock.getInetAddress().getHostAddress();
        	String senderPort = "" + sock.getPort();
        	String messageToSend = senderIP + " : " + senderPort + " : " + cList;


		cList = Success + ", " + cList;

		singleMsg(cList, rInfo); // Return to requesting client


	}

	// Log a user in! This works by converting an operating client to an active client. 
	public synchronized boolean login(ClientInfo info, String name, String pd) {

		if (isLoggedIn(name, info) == true) { // If we're already logged in, 
			System.out.println("User: " + name + " is already logged in, aborting."); // Server log.
			singleMsg(dupl, info);

			return false; // Exit.

		}

		else { // Otherwise, if we're not logged in: 

			for (int u = 0; u < list.size(); u++) { // For each registered user, 

				if (list.get(u).username.equals(name) && list.get(u).password.equals(pd)) { // Make sure our credentials match.
					// Set Operating client to active client. 
					info.username = name;
					info.password = pd;
					info.active = true; // user is logged in.
					myActives.add(info); // Client is active. 
					info.setActive();
					
					return true;

				}

			}

		}

		singleMsg(badInfo, info); // Entered wrong information

		return false;

	}

	// Check if we're already logged in. Subroutine for login and register. 
	public synchronized boolean isLoggedIn(String name, ClientInfo info) {
		for (int i = 0; i < myActives.size(); i++) { // For each active client,
			Object o = myActives.elementAt(i); // Grab their info... 

			if (o instanceof ClientInfo) {
				ClientInfo b = (ClientInfo)o; // Cast to ClientInfo so we can extract data. 
				String item = b.username; // Grab username
				if (item.equals(name)) {
					
					return true;

				}
			
			}
		
		}

		System.out.println("User: " + name + " is not logged in"); // Server log. 

		return false;

	}

	// Register a client. This does NOT write to the file, so it only works for the current session of this Server.
	// However, registries will remain through Disconnects. 
	public synchronized boolean register(ClientInfo info, String name, String pd) {

		if (isRegistered(name, info) == true) { // Check if we're already registered. 
			singleMsg(dupl, info);

			return false;

		}

		else {
			// Create desired credentials
			ClientInfo toAdd = new ClientInfo("guest","guestPassword",info.mySock,info.myListener,info.mySender,false);
			toAdd.username = name;
			toAdd.password = pd;
			toAdd.active = false; // Registering does NOT log you in! 
			list.add(toAdd); // Add them to the list of Registered Users. 

			if (isRegistered(name, info) == true) { // Double check they were properly registered. 

				return true;
			
			}

		}

		// This should never occur. 
		System.out.println("Something has gone horribly awry in method: register"); // Server log. 

		return false;

	}

	// Check if user is already registered. Subroutine for register. 
	public synchronized boolean isRegistered(String name, ClientInfo info) {
		if (isLoggedIn(name, info) == true) { // Must already be registered if we're logged in

			return true;

		}

		else {
			for (int u = 0; u < list.size(); u++) { // For every registered user, 
				if (list.get(u).username.equals(name)) { // Check if they're us. 
			System.out.println("User: " + name + " is already Registered"); // Server log.

					return true;

				}

			}

		}

		return false;

	}

	// add a message to the server's message queue and notify it's thread to wake up - 
	// - the message queue reader for Listener threads.
    	public synchronized void sendMessage(ClientInfo aClientsInfo, String aMessage) {
		// Hold the message incase command is MSG
		String saveMessage = "";
		saveMessage = aMessage;

		// Grab Address information, mostly for debugging. 
        	Socket sock = aClientsInfo.mySock;
        	String senderIP = sock.getInetAddress().getHostAddress();
        	String senderPort = "" + sock.getPort();
        	// String messageToSend = senderIP + ":" + senderPort + " : " + aMessage;

		// Let's split the message up so we can determine the command.
		String[] messageArray = null;
		messageArray = new String [3];

		String[] tempArray = new String[3];
		List<String> items = Arrays.asList(aMessage.split("\\s*,\\s*")); // Don't be picky about whitespace.

		String[] lines = items.toArray(new String[0]);
		int size = lines.length;
		String[] argTerms = new String[size];

		for (int i = 0; i < size; i++) { // Want to use array for commands
			argTerms[i] = lines[i];

		}

		String command = argTerms[0]; // What command are they attempting?

		if (aMessage.equals("DISCONNECT")) { // Disconnect the user by calling removeClient. 
			System.out.println("Attemping to Disconnect User: " + senderPort); // Server log. 
			removeClient(aClientsInfo);
			System.out.println("Successfully Disconnected: " + senderIP + " : " + senderPort + " from the Server."); // Server log,

		}

		if (command.equals("LOGIN")) { // Log the user in for communicating purposes.
			if (argTerms.length != 3) { // Some error checking. 
				singleMsg(badFormat, aClientsInfo);

			}

			else {

				if (aClientsInfo.username.equals("guest") == false) {
					String rError = "";
					rError = "You are already logged in, cannot LOGIN while logged in.";

					singleMsg(rError, aClientsInfo);

				}

				else {

					System.out.println("Attemping to LOGIN: " + argTerms[1]); // Server log. 

					// Try to log them in. 
					boolean suc = login(aClientsInfo, argTerms[1], argTerms[2]);

					if (suc == true) {
						singleMsg(Success, aClientsInfo);
						System.out.println("User: " + argTerms[1] + " Successfully Logged in."); // Server log. 

					}

					else {
						System.out.println("User: " + argTerms[1] + " NOT Successfully Logged in."); // Server log. 

					}

				}

			}
			
		}

		
		if (command.equals("REGISTER")) { // Register the user.
			if (argTerms.length != 3) { // Error Checking
				singleMsg(badFormat, aClientsInfo);

			}
			
			else {
				if (aClientsInfo.username.equals("guest") == false) {
					String rError = "";
					rError = "You are already logged in, cannot Register while logged in.";

					singleMsg(rError, aClientsInfo);

				}

				else {
					System.out.println("Attemping to REGISTER: " + argTerms[1]); // Server log. 

					boolean reg_succeed = register(aClientsInfo, argTerms[1], argTerms[2]);

					if (reg_succeed == true) {
						singleMsg(Success, aClientsInfo);
						System.out.println("User: " + argTerms[1] + " Successfully Registered."); // Server log. 

					}

					else {
						System.out.println("User: " + argTerms[1] + " NOT Successfully Registered"); // Server log. 

					}

				}

			}

		}

		if (aMessage.equals("CLIST")) { // Print list of all the users that are logged in. (Who you can talk to)
			System.out.println("Attemping to access Client List for " + aClientsInfo.username + "..."); // Server log. 


			if (isLoggedIn(aClientsInfo.username, aClientsInfo) != true) { // Error checking, need to be logged in to see who all is logged in. Privacy & Protection.
				singleMsg("ERROR, LOGIN before trying to CLIST!", aClientsInfo);

			}

			else {
				printActives(aClientsInfo); // If we're logged in, get that list! 
				System.out.println("User: " + aClientsInfo.username + " Successfully retrieved the Client List."); // Server log

			}

		}

		if (command.equals("MSG")) { // Broadcast messages to everybody (include sender) 
			if (isLoggedIn(aClientsInfo.username, aClientsInfo) != true) { // Error Checking. Need to be logged in to send (and recieve) messages. 
				singleMsg("ERROR, LOGIN before trying to MSG", aClientsInfo);

			}

			else {
				if (size != 1) {
					// singleMsg(Success, aClientsInfo);
					String sende = "";
					sende = aClientsInfo.username + ": ";
					String[] tmpArray = new String[2];

					tmpArray = saveMessage.split("\\s*,\\s*", 2);
					sende = sende + tmpArray[1];
        				myMessages.add(sende);
        				notify();
				}
				
				else { // This should never happen, Error occured
					System.out.println("Please do not do that.");

				}

			}

    		}

//End of P1 Commands ---------------------------------------------------------------------------------------//------------

		if(command.equals("FPUT")) {

			if (argTerms.length != 4) { // Error Checking
				singleMsg(badFormat, aClientsInfo);
					
			}
			
			else {
				System.out.println("Attempting FPUT for " + aClientsInfo.username + "..."); // Server log

				if (isLoggedIn(aClientsInfo.username, aClientsInfo) != true) { // Error Checking, need to be logged in to see the File List
					singleMsg("ERROR, LOGIN before trying FPUT!", aClientsInfo);

				}

				else {

					// boolean fput_succeed = addFile(argTerms[1], argTerms[2], argTerms[3], aClientsInfo);
					boolean fput_succeed = addFile(argTerms[1], senderIP, argTerms[3], aClientsInfo);

					if (fput_succeed == true) {
						//"FPUT_OK"
						String fPut = "";
						fPut = Success + ", " + "FPUT_OK" + ", " + argTerms[1] + ", " + argTerms[2] + ", " + argTerms[3];
					
						singleMsg(fPut, aClientsInfo);
						System.out.println("User: " + aClientsInfo.username + " Successfully Placed their file: " + argTerms[2]); // Server log. 
						System.out.println(fPut);

					}

					else {

						System.out.println("User: " + aClientsInfo.username + " Did NOT Put their file up! "); // Server log. 

					}

				}

			}

		}

		if(aMessage.equals("FLIST")) {
			System.out.println("Attempting to access File List for " + aClientsInfo.username + "..."); // Server log

			if (isLoggedIn(aClientsInfo.username, aClientsInfo) != true) { // Error Checking, need to be logged in to see the File List
				singleMsg("ERROR, LOGIN before trying FLIST!", aClientsInfo);

			}

			else {
				printFLIST(aClientsInfo);
				System.out.println("User: " + aClientsInfo.username + " Successfully retrieved the File List."); // Server log

			}

		}

		if(command.equals("FGET")) {

			String retList = "";

			if (argTerms.length != 2) { // Error Checking
				singleMsg(badFormat, aClientsInfo);

			}
			
			else {
				System.out.println("Attempting to get the host info for file: " + argTerms[1] + " for: " + aClientsInfo.username); // Server log

				if (isLoggedIn(aClientsInfo.username, aClientsInfo) != true) { // Error Checking, need to be logged in to see the File List
					singleMsg("ERROR, LOGIN before trying FGET!", aClientsInfo);

				}

				else {

					String[] tst = new String[2];
					tst = getFile(argTerms[1], aClientsInfo);

					if ( (tst[0].equals("") == false) && (tst[1].equals("") == false) ) {
						System.out.println("User: " + aClientsInfo.username + " Successfully Got their file host's info."); // Server log
						retList = Success + ", " + "FGET_OK" + ", " + tst[0] + ", " + tst[1];
						singleMsg(retList, aClientsInfo);

					}

					else {
						System.out.println("User: " + aClientsInfo.username + " Did NOT  Get their file Host's info."); // Server log

					}

				}

			}

		}


	} // Close sendMessage() method

	// Returns and deletes the message at the front of the queue. 
    	private synchronized String getNextMessage() throws InterruptedException {
        	while (myMessages.size() == 0) { // If queue is empty, sleep. 
           		wait();

		}

        	String message = (String) myMessages.get(0); // Grab next message

        	myMessages.removeElementAt(0); // remove it from the queue

        	return message;

    	}

	// Broadcasts to all Operating Clients, so all Active and some who have not logged in yet. 
	// Used for debugging, and playing around with the capabilities of the app. 
    	private synchronized void operatingBroadcast(String aMessage) {
        	for (int i = 0; i < myClients.size(); i++) { // For each Operating Client, 
           		ClientInfo clientInfo = (ClientInfo) myClients.get(i); // Grab their info

           		clientInfo.mySender.pushMessage(aMessage); // Send a message to their respective queue.

        	}

    	}

	// Broadcasts to all Active Clients, so all users who are currently logged in. 
	private synchronized void allActivebroadcast(String aMessage) {
        	for (int i = 0; i < myActives.size(); i++) {
           		ClientInfo clientInfo = (ClientInfo) myActives.get(i);

           		clientInfo.mySender.pushMessage(aMessage);

        	}

    	}

	// Broadcasts to all Active Clients, so all users who are logged in. Except the one who sent it. 
	private synchronized void broadcast(String aMessage) {
		String saveMessage = aMessage;

		String tempString = "";
		String senderName = "";
		String[] tmpArray = new String[2];

		tmpArray = aMessage.split("\\s*:\\s*", 2);
		senderName = tmpArray[0];
		tempString = tmpArray[1];

		for (int i = 0; i < myActives.size(); i++) {
			ClientInfo clientInfo = (ClientInfo) myActives.get(i);

			if (clientInfo.username.equals(senderName) == false) {
				clientInfo.mySender.pushMessage(saveMessage);

			}

		}
	
	}

	// Sends a message to a given client's message queue. 
	private synchronized void singleMsg(String aMessage, ClientInfo targetInfo) {
		targetInfo.mySender.pushMessage(aMessage);


	}

	// Server's Handler runs until interrupted, reading messages from clients and sending them out to clients.
    	public void run() {
        	try {
           		while (true) { // Until we get interrupted or killed. 
               			String message = getNextMessage(); // Grab the next message. 

               			broadcast(message); // Send to all Clients. 

           		}

        	} 

		catch (InterruptedException ie) { // Thread interrupted. Stop its execution
			cancel();

        	}	

    	}

	// Interrupt routine for threads.
	public void cancel() {
		interrupt();

	}
 
}
 
