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
			String[] msg = message.split(" ", 3);
			String old = msg[2].substring(0, Integer.valueOf(msg[1]) + 1);
			String newName = msg[2].substring(msg[2].indexOf(" ", Integer.valueOf(msg[1])));
			((Client)client).gui.pushLocalMessage(old  + " has changed names to " + newName);
		}
	}
}