import java.io.*;
import java.net.*;
import java.lang.Exception;

	
public class SThread extends Thread 
{
	private Object [][] RTable; // routing table
	private PrintWriter out, outTo; // writers (for writing back to the machine and to destination)
	private BufferedReader in, inTo; // reader (for reading from the machine connected to)
	private String inputLine, outputLine, destination, addr; // communication strings
	private Socket outSocket; // socket for communicating with a destination
	private int ind; // indext in the routing table
	private String serverOther;

	// Constructor
	SThread(Object [][] Table, Socket toClient, int index, String otherSRouter) throws IOException {
		out = new PrintWriter(toClient.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
		serverOther = otherSRouter;
		RTable = Table;
		addr = toClient.getInetAddress().getHostAddress();
		RTable[index][0] = addr; // IP addresses 
		//RTable[index][1] = toClient; // sockets for communication
		ind = index;
	}
	
	// Run method (will run for each machine that connects to the ServerRouter)
	public void run() {
		
		try {
			// Initial sends/receives
			destination = in.readLine();// initial read (the destination for writing)
			if (destination!=null) {
				System.out.println("ServerRouter searching for destination: " + destination);
				out.println("Connected to the ServerRouter."); // confirmation of connection
			
				// waits 10 seconds to let the routing table fill with all machines' information
				try { Thread.currentThread().sleep(10000); }
				catch(InterruptedException ie) { System.out.println("Thread interrupted"); }
				
				// loops through the routing table to find the destination
				boolean destinationFound = false;
				for ( int i=0; i<10; i++) {
					System.out.println(RTable[i][0]);
					// destination is found in routing table
					if (destination.equals((String) RTable[i][0])){
						System.out.println("ServerRouter found destination: " + destination);
						out.println(RTable[i][0]);
						System.out.println("ServerRouter sent back address of destination: " + RTable[i][0]);
						destinationFound = true;
						break;
					}
				}
				// destination is not found in routing table
				if(destinationFound==false) {
					System.out.println("ServerRouter did not find destination: " + destination);
					System.out.println("serverOther: " + serverOther);

					TCPConnection otherSRouterConnection = new TCPConnection(serverOther,5555);
					boolean result = otherSRouterConnection.connect();
					System.out.println(result);

					otherSRouterConnection.writer.println(destination);
					String response = otherSRouterConnection.reader.readLine();
					System.out.println(response);
					response = otherSRouterConnection.reader.readLine();
					System.out.println(response);

					out.println(response);
				}
			}
		
		 }// end try
		catch (IOException e) {
			System.err.println("Could not listen to socket.");
			System.exit(1);
		}
	}


}