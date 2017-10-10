Author: Maxwell Zane Miller
x500: mill5488
SID: 4365954
This is my Submission for Phase 2 project of the CSCI4211, SPRING 2017 course at the University of Minnesota, Twin Cities. 

Directions on usage are at the bottom of this file:

Quick rundown. This is a Multithreaded Chat Application, using a Server - Client model. This Iteration of the application, also implements Peer-to-Peer (P2P) file sharing.

	While the Server runs, it creates a Listener thread and a Sender thread for each Client that conencts to it, and the Server also creates its own Thread, (The Master Thread) called the Handler. The Handler thread processes all of the Server's communications and jobs. 

	The Handler thread distributes messages from Client (or Server) Sockets to their target(s), and processes all commands and requests. It builds an ArrayList of registered users from the file, (and adds to it if someone uses the REGISTER command), Vectors for the Server's message queue, list of Operating Clients (clients connected to it, and list of Active Clients (Clients whose users are also Logged in)). I chose an ArrayList for the list of registered users because it's very easy to manipulate and iterate over (if you don't know if someonething is there). By contrast, I chose to make the message queue(s), and lists of Operating Clients and Active Clients respectively, with Vectors, because Vectors are easy to run over when you know what you're looking for. Everytime I use a Vector, I know what I'm looking for. Not to mention, all of the objects in the Vectors are in use. Whereas most of the list of registered users probably aren't. 

	The Client class is used by the Client, separate from the Server. And behaves sort of in a similar way to the Server, it also attaches sockets and reads from and writes to them. In this version (The Phase 1 version) it only writes to the Server (Handler thread/class) using its Sender Thread, and has the Handler deliver it. 

	I was not sure on whether we should make it so MSG command sends to all users BUT the sender, or whether it was just supposed to broadcast to all of them. I assumed the former. If I am incorrect, however, there is another method within Handler, around line 469, called allActiveBroadcast, which sends it to ALL active Clients. In which case, simply go to line 516, within the while loop of the run() for the Handler Thread, change 

broadcast(message); 

to 

allActiveBroadcast(message); 


and you will have that functionality!

I also assumed for the Client, that the Server was already started. If that is not the case, the Client will fail miserably. Make sure the Server is up and running first.

Finally, I would like to make some comments about the structure and syntax of my code. I'm not the most stylistic or obsessively compulsive about my design or style. I go with what looks, sounds, or feels good while I'm in the moment of writing it, and sometimes I fail to go back and change variable/method/whatever names to be what they should be. My comments should explain what the methods actually do, so I don't think that should be too big of a problem.


Directions for using (and testing) my project:

To build my project, 
1. Extract from the Archive
1. Get to the directory where these files are located within a shell. (will need to go inside the directory that is immediately extracted from the archive)
2. run < make >
3. Let Makefile build the project.

To run the project,
4. Get inside of the directory where the executables are located within a shell. (Can be the same as you ran make with)

5. run < java Server inputfile.txt > where inputfile.txt is any .txt file that follows the structure we were chown in class, shown at: http://www-users.cselabs.umn.edu/classes/Spring-2017/csci4211/files/UserDetails.txt
There is a similar file(1 additional line) provided within this zipfolder, called users.txt. I used it for testing and debugging.

6. Get inside of the directory where the executables are located within a SEPERATE shell.
7. run < java Client > 

8. Repeat steps 6 -> 7 for more Clients.


I feel I learned a ton making this project, and feel good about what I've accomplished on it so far. 


