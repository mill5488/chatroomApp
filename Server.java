// Server Class for Multithreaded Chat Application
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
 
// Server Class, Everything in this class and in the Handler is considered Server work. 
public class Server {

    	public static final int myPort = 2005; // Arbitarily chosen port #. 
	public ClientInfo ServerInfo; // Server's info. 

    	public static void main(String[] args) throws IOException {

		// Initialize necessary defaults for Clients (and sockets)
		String tmpName = "guest"; // guest can login and register, cannot see list of logged in users, read messages, or send messages. 
		String tmpPW = "guestPassword"; // filler
		Socket tmpSock = null;
		Listener tmpListener = null;
		Sender tmpSender = null;

        	// Open server socket for listening
        	ServerSocket serverSocket = null;

        	try {
           		serverSocket = new ServerSocket(myPort); 
           		System.out.println("Server started on port: " + myPort); // Server log. 

        	} 

		catch (IOException ex) {
           		System.err.println("Can not start listening on port: " + myPort); // Server log. 
           		ex.printStackTrace();
           		System.exit(-1);

        	}
 
        	// Start Handler thread < This is still the Server doing work>
		// (and nearly all of it!)
        	Handler Handler = new Handler(args[0]); // Handler thread needs the name of the registered users file to build the ArrayList.
        	Handler.start();
 
        	while (true) {
           		try {
               			Socket sock = serverSocket.accept(); // Accept Incoming Connections,
               			ClientInfo clientInfo = new ClientInfo(tmpName, tmpPW, tmpSock, tmpListener, tmpSender, true); // Give a client guest info so they can login or register
               			clientInfo.mySock = sock; // socket for communication
               			Listener clientListener = new Listener(clientInfo, Handler); // Start Listener Thread for the respective client. 
               			Sender clientSender = new Sender(clientInfo, Handler); // Start Sender Thread for the respective client. 
               			clientInfo.myListener = clientListener;
               			clientInfo.mySender = clientSender;
              			clientListener.start();
               			clientSender.start();
               			Handler.addClient(clientInfo); // Add the newly connected, guest user to the server.

           		} 

			catch (IOException ex) {
               			ex.printStackTrace();

           		}

        	}

    	}

}

