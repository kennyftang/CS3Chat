class ListCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "LIST";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			//Process a list of all users connected to the server
			//# = length of handle
			//LIST # handle# handle#handle
			//Example: 
			//		LIST 5 MrMay2 Ed10 Tad Cooper
			//
			//There are 3 users in the above list:
			//	MrMay
			//	Ed
			//	Tad Cooper
		}
	}
}