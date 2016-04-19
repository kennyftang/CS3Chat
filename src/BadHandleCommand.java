class BadHandleCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "BADHANDLE";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			//Notification that a previous SETHANDLE message resulted in an error on the server
			//BADHANDLE error message
			//Example: 
			//		BADHANDLE Something went wrong, could not set handle
			((Client)client).gui.pushLocalMessage("Invalid Name");
			((Client)client).changeGUIName("");
		}
	}
}