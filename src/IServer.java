interface IServer 
{
	//This will send the data to all clients that are connected to this server
	public void broadcast(String data);
	
	//This will remove a client from the list of connected clients
	//If the client is connected to the server, it should send the client a message telling the client that it is being disconnected
	//It should also stop the ServerSideClient object
	public void remove(IClient c);
	
	//This has an "infinite" loop that listens for new client connections
	//When a new client connects, a ServerSideClient is created and added to the list of connected clients
	public void run();
	
	//This makes the "infinite" loop in the run method stop looping
	public void stop();
	
	public IClient[] getClients();
}
