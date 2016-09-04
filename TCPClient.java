import java.io.*;
import java.net.*;

public class TCPClient {
   public static void main(String[] args) throws IOException {	
      // Variables for setting up connection and communication
      Socket Socket = null; // socket to connect with ServerRouter
      Socket SocketPeer = null; // socket to connect with peer
      PrintWriter out = null; // for writing to ServerRouter
      BufferedReader in = null; // for reading form ServerRouter
      PrintWriter outPeer = null; // for writing to peer
      BufferedReader inPeer = null; // for reading form peer
      InetAddress addr = InetAddress.getLocalHost();
		String host = addr.getHostAddress(); // Client machine's IP
      String routerName = "j263-08.cse1.spsu.edu"; // ServerRouter host name
		int SockNum = 5555; // port number
			
		// Tries to connect to the ServerRouter
      // try {
      //    Socket = new Socket(routerName, SockNum);
      //    out = new PrintWriter(Socket.getOutputStream(), true);
      //    in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
      // } 
      // catch (UnknownHostException e) {
      //    System.err.println("Don't know about router: " + routerName);
      //    System.exit(1);
      // } 
      // catch (IOException e) {
      //    System.err.println("Couldn't get I/O for the connection to: " + routerName);
      //    System.exit(1);
      // }
				
      // Variables for message passing	
      Reader reader = new FileReader("file.txt"); 
		BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
      String fromServer; // messages received from ServerRouter
      String fromPeer; // messages received from Peer
      String fromUser; // messages sent to ServerRouter
		String address ="10.5.2.109"; // destination IP (Server)
		long t0, t1, t;
			
		// Communication process (initial sends/receives
		// out.println(address);// initial send (IP of the destination Server)
		// fromServer = in.readLine();//initial receive from router (verification of connection)
		// System.out.println("ServerRouter: " + fromServer);
		// fromServer = in.readLine(); // get the address from the serverRouter
  //     System.out.println("ServerRouter: " + fromServer);
      
      // Tries to connect to the other peer
      String peerName = "192.168.1.111";
      int peerPort = 5557;
      try {
         SocketPeer = new Socket(peerName, peerPort);
         outPeer = new PrintWriter(SocketPeer.getOutputStream(), true);
         inPeer = new BufferedReader(new InputStreamReader(SocketPeer.getInputStream()));
         System.err.println("Connection established " + peerName);
      } 
      catch (UnknownHostException e) {
         System.err.println("Don't know about peer: " + peerName + ". \n" + e.getMessage());
         System.exit(1);
      } 
      catch (IOException e) {
         System.err.println("Couldn't get I/O for the connection to peer: " + peerName + ". \n" + e.getMessage());
         System.exit(1);
      }

      // Communicate with peer
      outPeer.println(host); // Client sends the IP of its machine as initial send
		t0 = System.currentTimeMillis();
      	
		// Communication while loop
      while ((fromPeer = inPeer.readLine()) != null) {
         System.out.println("Peer: " + fromPeer);
			t1 = System.currentTimeMillis();
         if (fromPeer.equals("Bye.")) // exit statement
            break;
			t = t1 - t0;
			System.out.println("Cycle time: " + t);
       
         fromUser = fromFile.readLine(); // reading strings from a file
         if (fromUser != null) {
            System.out.println("Client: " + fromUser);
            outPeer.println(fromUser); // sending the strings to the Server via ServerRouter
				t0 = System.currentTimeMillis();
         }
      }
      	
		// closing connections
      // out.close();
      in.close();
      Socket.close();
   }
}