
public abstract class NetworkListenerAdapter implements INetworkListener 
{
	public boolean isCommand(String message, String cmd)
	{
		String[] parts = message.split(" ", 2);
		return parts[0].toUpperCase().equals(cmd.toUpperCase());
	}
	
	//This is called when a client receives a message from the Server
	public void process(String message, IClient client) {}
	
	//This is called when a server received a message from a Client
	public void process(String message, IClient client, IServer server) {}
	
	//This is called before a client sends a message to the Server
	public void send(String message, IClient client) {}
	
}
