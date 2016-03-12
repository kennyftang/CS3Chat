import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChatServer implements Runnable{
    private static final int PORT = 1337;
    public static ServerSocket serverSocket;
    public static List<PrintWriter> messageOut;
    public Socket client;
    public static void main(String[] args){
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.print("Starting server on port: " + PORT);
        Map<Socket, Thread> clients =  new HashMap<>();
        while(true) {
            try {
                Socket client = serverSocket.accept();
                clients.put(client, new Thread(new ChatServer(client)));
                System.out.println("Accepted new client at " + client.getInetAddress().getHostAddress());
            } catch (Exception e){

            }
        }
    }
    public ChatServer(Socket client){
        this.client = client;
    }
    public void run(){
        if(serverSocket == null)
            return;
        PrintWriter clientOut;
        BufferedReader clientIn;
        try {
            clientOut = new PrintWriter(client.getOutputStream());
            clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        ChatServer.messageOut.add(clientOut);
        while(true){
            try {
                String msg = clientIn.readLine();
                System.out.println(msg);
                sendMessage(msg);
            } catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
    }
    public static void sendMessage(String message){
        for(PrintWriter x : messageOut)
            x.println(message);
    }
}
