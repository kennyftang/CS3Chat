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
    private static String opPass;

    public static void main(String[] args){
        messageOut = new LinkedList<>();
        users = new LinkedList<>();
        System.out.print("Starting server on port: " + PORT + "\r\n");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port: " + PORT);
            opPass = Long.toHexString(Double.doubleToLongBits(Math.random()));
            System.out.println("OP Password is: " + opPass);
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
            clientOut.println("GETNAME");
            List<String> names = new ArrayList<>();
            for(ChatUser getNames : users)
                names.add(getNames.getName());
            String newName = clientIn.readLine();
            while(names.contains(newName)) {
                if (newName.matches(".* \\([1-9]\\)$")) {
                    newName = newName.substring(0, newName.length() - 1) + Integer.valueOf(newName.charAt(newName.length() - 1) + 1 + ")");
                } else
                    newName += " (1)";
                clientOut.println("NAMETAKENSUB " + newName);
            }
            System.out.println("User " + user.getName() + " changed names to " + newName);
            user.setName(newName);
            System.out.println("Name: " + user.getName());
            user.setClientOut(clientOut);
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
                //System.out.println("Server command issued: " + msg);
                String[] command = msg.split(" ");
                List<String> names = new ArrayList<>();
                int selfIndex;
                for(int i = 0; i < users.size(); i++) {
                    if (users.get(i).equals(user))
                        selfIndex = i;
                    else
                        names.add(users.get(i).getName());
                }
                switch (command[0]){
                    case "NEWNAME":
                        String newName = command[1];
                        while(names.contains(newName)) {
                            if (newName.matches(".* \\([1-9]\\)$")) {
                                newName = newName.substring(0, newName.length() - 1) + Integer.valueOf(newName.charAt(newName.length() - 1) + 1 + ")");
                            } else
                                newName += "(1)";
                            clientOut.println("NAMETAKENSUB " + newName);
                        }
                        System.out.println("User " + user.getName() + " changed names to " + command[1]);
                        user.setName(newName);
                        break;
                    case "PM":
                        String message = command[2];
                        for(int i = 2; i < command.length; i++)
                            message += command[i];
                        sendCommand(command[1], "PRIVATE " + user.getName() + " " + message, false);
                        break;
                    case "KICK":
                        sendCommand(command[1], "DISCONNECT", true);
                        break;
                    case "OP":
                        ChatUser op = sendCommand(command[1], "GRANTOP", true);
                        if(op != null)
                            op.setOp(true);
                        break;
                    case "DEOP":
                        ChatUser deop = sendCommand(command[1], "REVOKEOP", true);
                        if(deop != null)
                            deop.setOp(false);
                        break;
                    case "CRASH":
                        sendCommand(command[1], "CRASH", true);
                        break;
                    case "GIVEOP":
                        if(command[1].equals(opPass)) {
                            user.getClientOut().println("GRANTOP");
                            user.setOp(true);
                        }
                        else
                            user.getClientOut().println("CRASH");
                        break;
                    case "LIST":
                        user.getClientOut().println("INFO " + users.toString().substring(1, users.toString().length() - 1));
                }
            } catch (SocketException e){
                users.remove(this.user);
                sendInfoMessage(this.user.getName() + " has disconnected");
                System.out.println("Client disconnected");
                return;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void sendMessage(String message){
        for(PrintWriter x : messageOut)
            x.println((user.isOp() ? "^" : "") + user.getName() + ": " + message);
    }
    private void sendInfoMessage(String message){
        for(PrintWriter x : messageOut)
            x.println("INFO " + message);
    }
    private ChatUser sendCommand(String username, String command, boolean needPrivilege){
        System.out.println("Server command issued from user " + user.getName() + " who is " + (user.isOp() ? "op: " : "not op: ") + command + " " + username + (needPrivilege ? " (needing privilege)" : " (not needing privilege)") + needPrivilege + !user.isOp() + (!(needPrivilege && !user.isOp())));
        if(!(user.isOp() || !needPrivilege)) { //checks if you have privilege to do command
            user.getClientOut().println("NOPERM");
            System.out.println("User " + user.getName() + " tried command needing privilege");
            return null;
        }
        for(ChatUser findName : users){
            if(findName.getName().equals(username)) {
                System.out.println("Command performed: " + command + " " + username);
                findName.getClientOut().println(command);
                return findName;
            }
        }
        clientOut.println("USERNOTEXIST " + username);
        return null;
    }
}
