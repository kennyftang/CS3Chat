import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatListener implements Runnable {
    private JTextArea chatBox;
    private String name;
    private BufferedReader chatIn;
    private PrintWriter chatOut;

    public ChatListener(Socket s, JTextArea chatBox, String name){
        this.chatBox = chatBox;
        this.name = name;
        try{
            chatIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            chatOut = new PrintWriter(s.getOutputStream());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void run() {
        String messageIn;
        try {
            messageIn = chatIn.readLine();
        } catch(Exception e){
            messageIn = e.getMessage();
        }
        chatBox.append(messageIn + "\r\n");
    }
    public boolean sendMessage(String message){
        try{
            chatOut.println(name + ": " + message);
        } catch (Exception e){
            return false;
        }
        return false;
    }
}
