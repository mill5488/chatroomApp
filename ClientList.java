// ClientList Class for Multithreaded Chat Application 
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// ClientList class. This class creates the list of registerd users. 
public class ClientList {

	// Choose ArrayList because they're easy to manipulate. Can Easily iterate over.
	// Why not a Vector like the Active Clients and Operating Clients? 
	// Well, use the Vectors for Clients since those are always in use, 
	// Use ArrayList for registered users because most probably not in use. 
	private ArrayList<ClientInfo> registeredList; 

	// Default constructor.
	ClientList() { 
			registeredList = null;

	}

	// Constructor from file.
	ClientList(String inputFile) throws FileNotFoundException {
		registeredList = new ArrayList<ClientInfo>();

		try { 
			registeredList = createUsersList(inputFile);

		}

		catch (Exception ex) {
			System.out.println(ex.getMessage());	
	
		}

	}

	// Add user routine
	public void addUser(ClientInfo newUser) {

		registeredList.add(newUser);

	}

	// Routine to get size
	public int getSize() {

		return registeredList.size();

	}

	// Routine to get the ArrayList from the ClientList. Easy to operate on. 
	public ArrayList<ClientInfo> getList() {

		return registeredList;

	}

	// Routine to get a specific client
	public ClientInfo getClient(int i) {

		return registeredList.get(i);
	
	}

	// Build the list of registered users for the Server (Handler class) to use
	public ArrayList<ClientInfo> createUsersList(String input) throws FileNotFoundException {
		ClientInfo tmpUser = null;
		ArrayList<ClientInfo> usersList = new ArrayList<ClientInfo>();
		int size;

		try {
			BufferedReader in = new BufferedReader(new FileReader(input)); // Buffer Reader to read from file
			String Str;
			List<String> list = new ArrayList<String>();

			while ((Str = in.readLine()) != null) { // While there still unprocessed lines in the file,
				list.add(Str); // Put them into a list

			}

			// Convert that list to an array, easy to operate on.
			String[] lines = list.toArray(new String[0]);
			size = lines.length; // Need size of the array. 
			String[] tmpInfo = new String[2];

			for (int m = 0; m < size; m++) {  // For each registered user in the file, 
				tmpInfo = lines[m].split(","); // Extract their info
				usersList.add(new ClientInfo(tmpInfo[0], tmpInfo[1], null, null, null, false)); // add them to the list.

			}
		
		}

		catch (Exception ex) {
			System.out.println(ex.getMessage());	
	
		}

		return usersList;


	}

}
