import java.net.*;
import java.util.*;
import java.io.*;

public class Client implements Runnable, IClient
{
	//Instance Variables	
	private Socket socket;						//Object that represents the connection to the Server
	private BufferedReader in;					//Object used to read data send from the Server (if connected)
	private PrintWriter out;					//Object used to send data to the Server (if connected)
	private boolean running;					//is the Thread is currently running?
	private List<INetworkListener> listeners;	//List of all INetworkListener objects that are listening to this client
	
	public Client(String ip, int port) throws UnknownHostException, IOException
	{
		//Initialize the instance variables
	}

	//Methods (defined in interfaces: IClient and Runnable)
}

