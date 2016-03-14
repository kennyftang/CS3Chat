import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatServer implements Runnable{
    public Socket client;
    private static final int PORT = 1337;
    public static List<PrintWriter> messageOut;
    private PrintWriter clientOut;
    private BufferedReader clientIn;
    private static List<ChatUser> users;
    private ChatUser user;

    public static void main(String[] args){
        messageOut = new LinkedList<>();
        users = new LinkedList<>();
        System.out.print("Starting server on port: " + PORT + "\r\n");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port: " + PORT);
            while(true) {
                try {
                    Socket client = serverSocket.accept();
                    ChatUser user = new ChatUser(client.getInetAddress().getHostName(), client.hashCode(), false, client);
                    user.setClientIO(new Thread(new ChatServer(client, user)));
                    user.getClientIO().start();
                    System.out.println("Accepted new client at " + client.getInetAddress().getHostAddress());
                    users.add(user);
                } catch (Exception e){
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public ChatServer(Socket client, ChatUser user){
        this.client = client;
        this.user = user;
        try {
            clientOut = new PrintWriter(client.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
            messageOut.add(clientOut);
            clientOut.println("GET NAME");
            user.setName(clientIn.readLine());
            System.out.println("Name: " + user.getName());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void run(){
        while(true){
            try {
                System.out.println("Waiting for client...");
                String msg = clientIn.readLine();
                if(msg == null)
                    throw new SocketException("Disconnected");
                if(msg.matches("^>.*")) {
                    sendMessage(msg.substring(1));
                    continue;
                }
                System.out.println("Server command issued: " + msg);
                String[] command = msg.split(" ");
                List<String> names = new ArrayList<>();
                int selfIndex;
                for(int i = 0; i < users.size(); i++) {
                    if (users.get(i).equals(user))
                        selfIndex = i;
                    else
                        names.add(users.get(i).getName());
                }
                //PM KICK LIST OP
                switch (command[0]){
                    case "NEWNAME":
                        String newName = command[1];
                        while(names.contains(newName)) {
                            if (newName.matches(".* \\([1-9]\\)$")) {
                                newName = newName.substring(0, newName.length() - 1) + Integer.valueOf(newName.charAt(newName.length() - 1) + 1 + ")");
                            } else
                                newName += "(1)";
                        }
                        clientOut.println("NAMETAKENSUB " + newName);
                        user.setName(newName);
                        System.out.println("User " + user.getName() + " changed names to " + command[1]);
                        break;
                    case "PM":

                }
            } catch (SocketException e){
                System.out.println("Client disconnected");
                return;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void sendMessage(String message){
        for(PrintWriter x : messageOut)
            x.println(user.getName() + ": " + message);
    }
}
