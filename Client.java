// Client Class for Multithreaded Chat Application 
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.*;

// The Client Class, this is where the Client end happens. 
public class Client {
	// Arbitarily chosen parameters 
    	public static final String host = "localhost";
    	public static final int serverPort = 2005;

	// Error Codes.
	String Success = "Server: " + "0x00";
	String badInfo = "Server: " + "0x01";
	String dupl = "Server: " + "0x02";
	String badFID = "Server: " + "0x03";
	String badPort = "Server: " + "0x04";
	String badFormat = "Server: " + "0xFF";

	private int nextPort = 2012; // Initialize new port.

	// Method that grabs the next port to use for communication.
	// At the moment, it's a bit archaic; try random ports until one sticks.
	public int getNextPort() {
		int val = 0;

		System.out.println("Attempting to find a valid port for file transfer.");
		
		while (true) {
			if (validPort(val)) {
				
				return val;

			}

			val = 2020 + (int)(Math.random() * ((65000 - 2020) + 1));
			System.out.println("Randomized Port: " + Integer.toString(val));

		}

	}

	// Method to determine if a specified port is open for use. 
	public boolean validPort(int port) {
		String portString = "";

		portString = Integer.toString(port); // Transfer it to a string

		try {
			new ServerSocket(port).close();
			System.out.println(portString + " : is an open port."); // Client log. Debugging.

			return true;

		}

		catch (IOException ioe) {
			System.err.println(portString + " : is NOT an open port. Will try another."); // Client log. 
           		ioe.printStackTrace();

			return false;
		
		}

	}

    	public static void main(String[] args) {
        	BufferedReader in = null; // Grab buffer reader to read from sockets.
        	PrintWriter out = null; // Grab buffer reader to write to socket.


		// Testing Purposes. 
		// System.out.println("Before connecting to server:");
		// System.out.println("Here is your IP: " + host);
		// System.out.println("Here is your port: " + serverPort);

        	try {
           		// Try to Connect to Server
           		Socket sock = new Socket(host, serverPort); // Our socket, way to communicate with Server.
           		in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // Connect buffer reader to socket
           		out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream())); // Connect buffer writer to socket
           		System.out.println("Connected to server " + host + " : " + serverPort); // Client log. 

        	} 

		catch (IOException ioe) {
           		System.err.println("Can not establish connection to " + host + " : " + serverPort); // Client log. 
           		ioe.printStackTrace();
           		System.exit(-1);

        	}
 
        	// Create and start Sender thread
        	ClientSender sender = new ClientSender(out);
        	sender.setDaemon(true);
        	sender.start();
 
        	try {
           		// Read messages from the server and print them
            		String message;
           		while ((message = in.readLine()) != null) {
				// Hold the message incase command is FPUT or FGET
				String saveMessage = "";
				saveMessage = message;
				String[] messageArray = null;
				messageArray = new String [6];

				String[] tempArray = new String[6];
				List<String> items = Arrays.asList(message.split("\\s*,\\s*")); // Don't be picky about whitespace.

				String[] lines = items.toArray(new String[0]);
				int size = lines.length;
				String[] argTerms = new String[size];

				for (int i = 0; i < size; i++) { // Want to use array for commands
					argTerms[i] = lines[i];
					
				}

				if (size <= 1) {
					System.out.println(saveMessage);

				}

// Start of GET/PUT

				else { // FPUT or FGET

					// Don't know how many arguments I'll use in the end.
					String code = argTerms[0]; // Error Code from Server.
					String comm = argTerms[1]; // Command code, FPUT_OK or FGET_OK

					// FPUT, Client side. 
					if (comm.equals("FPUT_OK")) { // Set up socket to share file. 
						String fName = argTerms[2];
						String myIP = argTerms[3];
						String myPor = argTerms[4];

						// System.out.println("User: " + myIP + " : " + myPor + ". Has Tried: ");
						// System.out.println(saveMessage);

						int myP = 0;
						Scanner s = new Scanner(myPor);
						myP = s.nextInt();

						try {
							SendingManager mngr = new SendingManager(myP, fName);
        						mngr.setDaemon(true);
        						mngr.start();

						}


						catch (IOException ioe) {
           						ioe.printStackTrace();
           						System.exit(-1);


						}

					}

					//FGET, Client side.
					else if (comm.equals("FGET_OK")) { // Set up socket to recieve file.
						String myIP = argTerms[2];
						String myPor = argTerms[3];

						Socket fSock = null;
						int intPort = 0;
						Scanner s = new Scanner(myPor);
						intPort = s.nextInt();

						try {
							fSock = new Socket(myIP, intPort); // Our socket, way to communicate with Server.
							// System.out.println("Starting ClientFileListener"); // Client log.
							ClientFileListener fListener = new ClientFileListener(fSock);
        						fListener.setDaemon(true);
        						fListener.start();

        					} 

						catch (IOException ioe) {
           						// System.err.println("Can not establish connection to " + host + " : " + serverPort); // Client log. 
           						ioe.printStackTrace();
           						System.exit(-1);

        					}

					}	

					else { // Not FPUT or FGET
               					System.out.println(saveMessage);

					}

				}

           		}

        	} 

		catch (IOException ioe) {
          		System.err.println("Connection to server broken."); // Client log. 
           		ioe.printStackTrace();

        	}
 
    	}

}

// Create the thread that sends from Client.
class ClientSender extends Thread {
	private PrintWriter Output; // Writing Buffer
 
	public ClientSender(PrintWriter anOutput) {
        	Output = anOutput;

	}

	// Until interruption, read messages from user's input and send to Server through socket. 
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while (!isInterrupted()) {
				String message = in.readLine();
				Output.println(message);
				Output.flush(); // Empty buffer for next message. 

			}

		} 

		catch (IOException ioe) {
            		Output.println("DISCONNECT");
			cancel();

		}

	}
	
	// Interrupt routine for Threads. 
	public void cancel() {
		Output.println("DISCONNECT");
		interrupt();

	}

}

// Thread to handle sending files, this way the Client can still send more, recieve some, or chat if they want while being open to recievers!
class SendingManager extends Thread {
	private String fName; // Name of the file we want to send
	private int myPort;
	private ServerSocket sock;
 
	public SendingManager(int aPort, String fileName) throws IOException {
		fName = fileName;
		sock = null;
		myPort = aPort;
		
	}

	// Until interruption, read messages from indicated file and send through socket to recieving Client. 
	public void run() {
		String line = null;

		try {	
			// sock = new ServerSocket(myPort);

			while (true) { // Accept new connections indefinitely. 
           			// Try to Connect to Client
				sock = new ServerSocket(myPort);
				Socket rSock = sock.accept();

				// System.out.println("Starting ClientFileSender"); // Client log
				ClientFileSender fSender = new ClientFileSender(rSock, fName);
        			fSender.setDaemon(true);
        			fSender.start();

			}

		} 

		catch (IOException ioe) {
            		// ioe.printStackTrace();
			cancel();

		}

	}

/*

	public synchronized void accept(int p, String f) {
		// Try to Connect to Client
		sock = new ServerSocket(p);
		Socket rSock = sock.accept();

		// System.out.println("Starting ClientFileSender"); // Client log
		ClientFileSender fSender = new ClientFileSender(rSock, f);
        	fSender.setDaemon(true);
        	fSender.start();

	}

*/
	
	// Interrupt routine for Threads. 
	public void cancel() {
		interrupt();

	}

}

// Create the thread that sends a file from Client.
class ClientFileSender extends Thread {
	private Socket sock; // Socket to Write to.
	private String fName; // Name of the file we want to send
	private PrintWriter output;
 
	public ClientFileSender(Socket aSocket, String fileName) throws IOException {
		sock = aSocket;
		fName = fileName;
		output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
		
	}

	// Until interruption, read messages from indicated file and send through socket to recieving Client. 
	public void run() {
		String line = null;
		
		try {	
			// System.out.println("Trying to read, from: " + fName); // Client log.
			FileReader reader = new FileReader(fName);
			BufferedReader bufReader = new BufferedReader(reader);

			while ( ( !isInterrupted() ) && ( (line = bufReader.readLine() ) != null) ) {
				// System.out.println(line);
				output.println(line);
				output.flush();

			}

			// System.out.println("HELLO!!! I AM STILL HERE"); // Debugging.
			// output.close();
			bufReader.close();
			// reader.close();
			cancel();

		} 

		catch (IOException ioe) {
            		ioe.printStackTrace();
			cancel();

		}

	}
	
	// Interrupt routine for Threads. 
	public void cancel() {
		interrupt();

	}

}

// Create the thread that recieves a file from Client.
class ClientFileListener extends Thread {
	private Socket sock; // Socket to Listen from
	private BufferedReader Input;
	private String fName;
	private BufferedWriter bufWriter;
	private FileWriter writer;
 
	public ClientFileListener(Socket aSocket) throws IOException {
		sock = aSocket;
		Input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		fName = "test2.txt";

		writer = new FileWriter(fName);
		bufWriter = new BufferedWriter(writer);

	}

	public void run() {
		try {
			// System.out.println("Name of file is: " + fName); // Client Log.
			String line;

			while (!isInterrupted() && ( ( line = Input.readLine() ) != null) ) {
				// System.out.println("Attempting to Read line from Socket: "); // Client Log.
				// String line = Input.readLine();

				if (line == null || line == "\n" || line == "") {
					// System.out.println("Line is Null.");

					break;

				}

				else {
					// System.out.println(line);
					bufWriter.write(line);
					bufWriter.newLine();
					bufWriter.flush();

				}

			}

		}

		catch (IOException ioe) {
            		ioe.printStackTrace();
			cancel();

		}

		finally {
			try { 
                		bufWriter.close();
				Input.close();
				writer.close();
				System.out.println("ListenerExiting.");
				cancel();

            		} 

            		catch(Exception e) { 
                		e.printStackTrace(); 
				cancel();

            		}  

		}

		System.out.println("End of FileListener.");

	}
	
	// Interrupt routine for Threads. 
	public void cancel() {
		interrupt();

	}

}
