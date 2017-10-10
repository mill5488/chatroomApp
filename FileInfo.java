// FileInfo Class for Multithreaded Chat Application 
// A Java Implementation
// Author: Maxwell Miller, mill5488@umn.edu 

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// FileInfo class. This is the information for a specific file. 
public class FileInfo {

	public String filename = null; // name of the file
	public int fileID = 0; // ID of the file
	public String IP_addr = null; // IP Address of the hosting Client
	public String port = null; // port # of the hosting Client
	public String owner = null;

	// Constructor for FileInfo.
	public FileInfo(String a, int b, String c, String d, String e) {
		filename = a;
		fileID = b;
		IP_addr = c;
		port = d;
		owner = e;

	} 

	// Grabs name of this File.
	public String getFilename() {

		return filename;

	}

	// Grabs FileID for this File.
	public int getID() {

		return fileID;

	}

	// Grabs the IP address of the Client hosting this File.
	public String getIP() {

		return IP_addr;

	}

	// Grabs the port number of the Client hosting this File.
	public String getPort() {
 
		return port;

	}

	// Grabs the username of the Client hosting this file.
	public String getOwner() {

		return owner;

	}

	// The good old Override for printing classes. 
	@Override
	public String toString() {

		return "Filename: " + filename + " " + "FileID: " + fileID;

	}

}

