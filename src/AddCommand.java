class AddCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "ADD";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			//process the Add command
			//ADD handle
			//Example: 
			//		ADD Ned Stark
		}
	}
}