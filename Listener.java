// Listener thread Class for Multithreaded Chat Application 
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// Listener thread Class 
public class Listener extends Thread {
    	private Handler myHandler; // Grab my handler
    	private ClientInfo myInfo; // Get my Client's ClientInfo
    	private BufferedReader input; // buffer reader to read from socket
	private Socket sock; // socket to communicate through (Listen from)
 
	// Constructor for Listener Class. 
    	public Listener(ClientInfo aClientsInfo, Handler aHandler) throws IOException {
       		myInfo = aClientsInfo;
        	myHandler = aHandler;
        	sock = aClientsInfo.mySock;
        	input = new BufferedReader(new InputStreamReader(sock.getInputStream()));

    	}
 
	// Until cancelled, get messages from Client's socket and forwards them to the Server's message queue (Handler class)
    	public void run() {
        	try {
           		while (!isInterrupted()) {
               			String message = input.readLine();

               			if (message == null) {

                   			break;

				}

				else {
               				myHandler.sendMessage(myInfo, message);

				} 
          		
			}

			// If Interrupted, close socket so terminal operates normally.
			sock.close();

        	} 

		catch (RuntimeException ex) {
			System.out.println();
			cancel();
			ex.printStackTrace();

        	}

		catch (IOException iox) {
			// Problem reading from sock (communication is broken)
			System.out.println();
			cancel();
			iox.printStackTrace();

        	}
 
        // Communication is broken. Interrupt both listener and sender threads
        	myInfo.mySender.interrupt();
		cancel();

    	}

	// Routine for Thread interruption, exit cleanly. 
	public void cancel() {
		interrupt();

	}

}











