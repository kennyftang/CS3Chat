class SetHandleCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "SETHANDLE";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			//Set the handle of this client to the 
			//SETHANDLE handle
			//Example: 
			//		SETHANDLE Tad Cooper
		}
	}
}