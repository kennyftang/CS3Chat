class RenameCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "RENAME";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			//process the Rename command
			//RENAME # oldHandle# newHandle
			//Example: 
			//		RENAME 9 Ned Stark17 Headless Horseman
			//
			//This should rename the user "Ned Stark" to "Headless Horseman"
		}
	}
}