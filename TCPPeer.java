import java.io.*;
import java.net.*;

public class TCPPeer {
	TCPConnection serverConnection = null;
	public TCPConnection peerConnection = null;

	InetAddress address = null;
	String hostAddress = null;
	
	public int port = 9090;
	ServerSocket serverSocket = null;

	Reader fileReader = new FileReader("file.txt"); 
	BufferedReader fileContent =  new BufferedReader(fileReader); // reader for the string file

	public TCPPeer() throws IOException{
		this.address = InetAddress.getLocalHost();
		this.hostAddress = address.getHostAddress();
		System.out.println("peer name: " + this.address.getHostName());
	}

	public void openServerSocket() throws IOException {
		try {
      serverSocket = new ServerSocket(port);
      System.out.println("peer is open at: " + port);

      while(true){
				try {
					Socket peerSocket = serverSocket.accept();
					System.out.println("peer connected: " + peerSocket.getInetAddress().getHostAddress()+ " "+peerSocket.getInetAddress().getLocalHost().getHostName());

					PrintWriter outPeer = new PrintWriter(peerSocket.getOutputStream(), true);
	        BufferedReader inPeer = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
	        String fromClient; // messages received from ServerRouter   
    			String fromPeer;

	        // Communication while loop
	        while ((fromPeer = inPeer.readLine()) != null) {
	          System.out.println("Peer said: " + fromPeer);
	          if (fromPeer.equals("Bye.")) // exit statement
	            break;
	          fromClient = fromPeer.toUpperCase(); // converting received message to upper case
	          System.out.println("I said: " + fromClient);
	          outPeer.println(fromClient); // sending the converted message back to the Client via ServerRouter
	        }
				}
				catch (IOException e) {
					System.err.println(e);
				}
			}

    }
    catch (IOException e) {
      System.err.println(e);
    }
	}

	// public TCPPeer(int port) throws IOException{
	// 	this.address = InetAddress.getLocalHost();
	// 	this.hostAddress = address.getHostAddress();
	// 	this.port = port;
	// 	System.out.println("peer name: " + this.address.getHostName());
	// }

	public boolean connectToServer(){
		System.out.println("Connecting to server.");
		serverConnection = new TCPConnection("192.168.1.66",5555);
		return serverConnection.connect();
	}

	public boolean connectToServer(String name, int port){
		System.out.println("Connecting to server " + name);
		serverConnection = new TCPConnection(name,port);
		return serverConnection.connect();
	}

	public String initP2P(String destination) throws IOException{
  	System.out.println("Init P2P to peer" + destination);
  	serverConnection.writer.println(destination);

  	String serverReply = null;
  	try {
  		serverReply = serverConnection.reader.readLine();
  		System.out.println("ServerRouter responds: " + serverReply);
  		serverReply = serverConnection.reader.readLine();
  		System.out.println("ServerRouter responds: " + serverReply);
  	}
  	catch(IOException e){
  		System.err.println(e.getMessage());
  	}
  	return serverReply;
  }

  public boolean connectToPeer(String peerName){
  	System.out.println("Connecting to peer" + peerName);
  	peerConnection = new TCPConnection(peerName, 9090);
  	return peerConnection.connect();
  }

  public void sendFileContentToPeer() throws IOException{
  	String fromPeer;
  	String fromUser;
  	long t0, t1, t;
  	t0 = System.currentTimeMillis();
  	while ((fromPeer = peerConnection.reader.readLine()) != null) {
      System.out.println("Peer: " + fromPeer);
			t1 = System.currentTimeMillis();
      if (fromPeer.equals("Bye.")) // exit statement
          break;
			t = t1 - t0;
			System.out.println("Cycle time: " + t);
     
      fromUser = fileContent.readLine(); // reading strings from a file
      if (fromUser != null) {
        System.out.println("Client: " + fromUser);
        peerConnection.writer.println(fromUser); // sending the strings to the Server via ServerRouter
				t0 = System.currentTimeMillis();
      }
    }
  }

	public static void main(String[] args) throws IOException {

		if(args.length == 1 ){
			String serverName = args[0];

			TCPPeer peer = new TCPPeer();
			System.out.println("peer address: " + peer.hostAddress);
			if(peer.connectToServer(serverName, 5555)){
				peer.openServerSocket();
			}

		}

		else if(args.length == 2 ){
			String serverName = args[0];
			String destinationName = args[1];

			TCPPeer peer = new TCPPeer();
			System.out.println("peer address: " + peer.hostAddress);
			if(peer.connectToServer(serverName, 5555)){
				// open port

				// init p2p
				String peerName = peer.initP2P(destinationName);
				if(peer.connectToPeer(destinationName)){
					System.out.println("Connected to peer");
					try{
						peer.peerConnection.writer.println("Sending message to peer.");
						peer.sendFileContentToPeer();
					}catch(Exception e){
						System.err.println(e);
					}
				}
			}			

		}


	}
}