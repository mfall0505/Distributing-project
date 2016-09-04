import java.net.*;
import java.io.*;

public class TCPServerRouter {

  ServerSocket serverSocket = null;
  String serverAddress = null;
  int peerCount = 0; // indext in the routing table 
  Object [][] routingTable = new Object [10][2];

  String otherServerRouter = null;

  public TCPServerRouter(String otherServerRouter){
    this.otherServerRouter = otherServerRouter;

    try {
      serverSocket = new ServerSocket(5555);
      serverAddress = InetAddress.getLocalHost().getHostAddress();
      System.out.println("ServerRouter(" + serverAddress +") is listening on port: 5555.");
    }
    catch (IOException e) {
      System.err.println("Could not listen on port: 5555.");
      System.exit(1);
    }

    while (true){
      try {
        Socket clientSocket = serverSocket.accept();
        String clientAddress = clientSocket.getInetAddress().getHostAddress();
        System.out.println("Client connected: " + clientAddress);

        if(clientAddress.equals(serverAddress)){
          System.out.println("Client is peer...");
          // System.out.println("ServerRouter connected with PEER: " + clientSocket.getInetAddress().getHostAddress()+ " "+clientSocket.getInetAddress().getHostName());
          System.out.println("PEER hostname: " + InetAddress.getLocalHost().getHostName());

          SThread t = new SThread(routingTable, clientSocket, peerCount, otherServerRouter); // creates a thread with a random port
          t.start(); // starts the thread
          peerCount++; // increments the index
          
        }
        else {
          System.out.println("Client is serverRouter...");
          
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

          String destination = in.readLine();
          out.println("Searching for destination("+ destination+")");
          for ( int i=0; i<10; i++) {
            System.out.println(routingTable[i][0]);
            // destination is found in routing table
            if (destination.equals((String) routingTable[i][0])){
              System.out.println("ServerRouter found destination: " + destination);
              out.println(routingTable[i][0]);
              System.out.println("ServerRouter sent back address of destination: " + routingTable[i][0]);
              break;
            }
          }

          clientSocket.close();
        }
      }
      catch (IOException e) {
        System.err.println("Client/Server failed to connect.");
        System.exit(1);
      }
    }

  }  

  public static void main(String[] args) throws IOException {
    if(args.length > 0){
      TCPServerRouter localRouter = new TCPServerRouter(args[0]);
    }else {
      // TCPServerRouter localRouter = new TCPServerRouter();
    }
  }
}