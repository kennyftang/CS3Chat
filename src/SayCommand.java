class SayCommand extends NetworkListenerAdapter
{
	public static String COMMAND = "SAY";
	
	public void process(String message, IClient client)
	{
		if(isCommand(message, COMMAND))
		{
			//process the Say comment
			//SAY # handle message
			//Example: 
			//		SAY 5 MrMayComputer Science!
		}
	}
}