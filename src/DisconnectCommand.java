class DisconnectCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "DISCONNECT";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			client.stop();
		}
	}
}