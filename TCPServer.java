import java.io.*;
import java.net.*;

public class TCPServer {
  public static void main(String[] args) throws IOException {
      	
    // Variables for setting up connection and communication
    Socket Socket = null; // socket to connect with ServerRouter
    PrintWriter out = null; // for writing to ServerRouter
    BufferedReader in = null; // for reading form ServerRouter
    PrintWriter outPeer = null; // for writing to peer
    BufferedReader inPeer = null; // for reading form peer
		InetAddress addr = InetAddress.getLocalHost();
		String host = addr.getHostAddress(); // Server machine's IP			
		String routerName = "j263-08.cse1.spsu.edu"; // ServerRouter host name
		int SockNum = 5555; // port number
			
		// Tries to connect to the ServerRouter
    try {
      Socket = new Socket(routerName, SockNum);
      out = new PrintWriter(Socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
    } 
    catch (UnknownHostException e) {
      System.err.println("Don't know about router: " + routerName);
      System.exit(1);
    } 
    catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to: " + routerName);
      System.exit(1);
    }
				
    // Variables for message passing			
    String fromServer; // messages sent to ServerRouter
    String fromClient; // messages received from ServerRouter   
    String fromPeer; // messages received from Peer    
 		String address ="10.5.3.196"; // destination IP (Client)
			
		// Communication process (initial sends/receives)
		out.println(address);// initial send (IP of the destination Client)
		fromClient = in.readLine();// initial receive from router (verification of connection)
		System.out.println("ServerRouter: " + fromClient);
    fromServer = in.readLine(); // get the address from the serverRouter
    System.out.println("ServerRouter: " + fromServer);
		

    // Accepting connections from peer
    ServerSocket serverSocket = null; // server socket for accepting connections from peers
    Socket serverSocket = null; // server socket for communication with each peer
    try {
      serverSocket = new ServerSocket(5557);
      System.out.println("ServerRouter is Listening on port: 5555.");
    }
    catch (IOException e) {
      System.err.println("Could not listen on port: 5555.");
      System.exit(1);
    }
    // Creating threads with accepted connections from peer
    while (Running == true) {
      try {
        clientSocket = serverSocket.accept();
        System.out.println("A peer has connected: " + clientSocket.getInetAddress().getHostAddress()+ " "+clientSocket.getInetAddress().getLocalHost().getHostName());
        outPeer = new PrintWriter(clientSocket.getOutputStream(), true);
        inPeer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // Communication while loop
        while ((fromPeer = inPeer.readLine()) != null) {
          System.out.println("Peer said: " + fromPeer);
          if (fromPeer.equals("Bye.")) // exit statement
            break;
          fromServer = fromPeer.toUpperCase(); // converting received message to upper case
          System.out.println("I said: " + fromServer);
          outPeer.println(fromServer); // sending the converted message back to the Client via ServerRouter
        }
      }
      catch (IOException e) {
        System.err.println("Client/Server failed to connect.");
        System.exit(1);
      }
    }//end while
			
		// closing connections
    out.close();
    in.close();
    Socket.close();
  }
}