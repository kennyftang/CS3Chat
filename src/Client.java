import java.net.*;
import java.util.*;
import java.io.*;

public class Client implements Runnable, IClient
{
	//Instance Variables	
	private Socket socket;						//Object that represents the connection to the Server
	private BufferedReader in;					//Object used to read data send from the Server (if connected)
	protected PrintWriter out;					//Object used to send data to the Server (if connected)
	private boolean running;					//is the Thread is currently running?
	private List<INetworkListener> listeners;	//List of all INetworkListener objects that are listening to this client
    protected ClientGUI gui;

	public Client(String ip, int port, ClientGUI gui) throws UnknownHostException, IOException
	{
		//Initialize the instance variables
		socket = new Socket(ip, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream());
		running = true;
		listeners = new LinkedList<INetworkListener>();
        this.gui = gui;
	}

	@Override
	public void send(String data) {
        if(data.matches("$/.*"))
            data = data.substring(1);
        else
            data = "SAY " + data;
		for(INetworkListener commands : listeners){
			commands.send(data, this);
		}
	}

	@Override
	public void process(String str) {
        for(INetworkListener commands : listeners){
            commands.send(str, this);
        }
	}

	@Override
	public void addNetworkListener(INetworkListener listener) {
        listeners.add(listener);
	}

	@Override
	public void stop() {

	}

	@Override
	public void run() {
        try{
            while(running){
                this.process(in.readLine());
            }
        } catch (Exception e){

        }
	}
    public void changeGUIName(String name){
        gui.setName(name);
    }

	//Methods (defined in interfaces: IClient and Runnable)
}

