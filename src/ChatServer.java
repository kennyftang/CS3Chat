import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChatServer implements Runnable{
    public Socket client;
    private static final int PORT = 1337;
//    public static ServerSocket serverSocket;
    public static List<PrintWriter> messageOut;
    public static void main(String[] args){
        messageOut = new LinkedList<>();
        System.out.print("Starting server on port: " + PORT + "\r\n");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port: " + PORT);
            Map<Socket, Thread> clients =  new HashMap<>();
            while(true) {
                try {
                    Socket client = serverSocket.accept();
                    clients.put(client, new Thread(new ChatServer(client)));
                    clients.get(client).start();
                    System.out.println("Accepted new client at " + client.getInetAddress().getHostAddress());
                } catch (Exception e){

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public ChatServer(Socket client){
        this.client = client;
    }
    public void run(){
        PrintWriter clientOut;
        BufferedReader clientIn;
        try {
            clientOut = new PrintWriter(client.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        messageOut.add(clientOut);
        while(true){
            try {
                System.out.println("Waiting for client...");
                String msg = clientIn.readLine();
                System.out.println(msg);
                sendMessage(msg);
            } catch (SocketException e){
                System.out.println("Client disconnected");
                return;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void sendMessage(String message){
        for(PrintWriter x : messageOut)
            x.println(message);
    }
}
