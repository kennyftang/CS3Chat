class RemoveCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "REMOVE";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			//process the Remove command
			//REMOVE handle
			//Example: 
			//		REMOVE Ned Stark
		}
	}
}