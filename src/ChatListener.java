import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatListener implements Runnable {
    private BufferedReader chatIn;
    private PrintWriter chatOut;
    private ChatClient client;

    public ChatListener(Socket s, ChatClient client, String name){
        this.client = client;
        try{
            chatIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            chatOut = new PrintWriter(s.getOutputStream(), true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void run() {
        while(true) {
            String messageIn;
            try {
                messageIn = chatIn.readLine();
            } catch (Exception e) {
                messageIn = e.getMessage();
            }
            client.addMessage(messageIn + "\r\n");
        }
    }
    public boolean sendMessage(String message){
        try{
            chatOut.println(message);
        } catch (Exception e){
            return false;
        }
        return false;
    }
}
