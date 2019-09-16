// Sender thread Class for Multithreaded Chat Application 
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// Sender thread class
public class Sender extends Thread {
    	private Vector myMessages = new Vector(); // Message queue! 
    	private Handler myHandler; // The Server's Master Thread
    	private ClientInfo myInfo; // My Client's ClientInfo
    	private PrintWriter myOutput; // Buffer to write to socket with

	// Constructor for Sender
    	public Sender(ClientInfo aClientsInfo, Handler aHandler) throws IOException {
        	myInfo = aClientsInfo;
        	myHandler = aHandler;
        	Socket sock = aClientsInfo.mySock;
        	myOutput = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

	}

	// Adds message to the message queue, used by Handler thread.
	public synchronized void pushMessage(String aMessage) {
        		myMessages.add(aMessage);
        		notify(); // Wake up! 

    	}

	// Returns and returns the message at the front of the queue.
    	private synchronized String getNextMessage() throws InterruptedException {
        	while (myMessages.size() == 0) { // If the queue is empty, sleep.
           		wait();

		}

        	String message = (String) myMessages.get(0);

        	myMessages.removeElementAt(0);

        	return message;

    	}

	// Sends the message to the Client's socket.
    	private void send(String aMessage) {

        		myOutput.println(aMessage);
        		myOutput.flush(); // flush the buffer for next message

    	}

	// Until cancelled, read from the queue and send to the Client's socket.
    	public void run() {
        	try {
           		while (!isInterrupted()) {
               			String message = getNextMessage();

               				send(message);

           		}

        	} 

		catch (Exception ex) {
           	// Commuication problem
			cancel();

        	}
 
        // Communication is broken. Interrupt both listener and sender threads
        	myInfo.myListener.interrupt();
		cancel();

    	}

	// Routine for Thread Interruption
	public void cancel() {
		interrupt();

	}
 
}
