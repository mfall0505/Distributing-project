import java.io.*;
import java.net.*;

public class TCPConnection {
	String name = "192.168.1.111";
  int port = 5557;
  Socket socket = null;
  public PrintWriter writer = null;
  public BufferedReader reader = null;

  public TCPConnection(String name, int port){
  	this.name = name;
  	this.port = port;
  }

  public boolean connect(){
  	try {
       this.socket = new Socket(this.name, this.port);
       writer = new PrintWriter(this.socket.getOutputStream(), true);
       reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
       System.err.println("Connection established " + this.name);
       return true;
    } 
    catch (UnknownHostException e) {
      System.err.println(e.getMessage());
      return false;
    } 
    catch (IOException e) {
      System.err.println(e.getMessage());
      return false;
    }
  }

}