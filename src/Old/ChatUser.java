package Old;

import java.io.PrintWriter;
import java.net.Socket;

public class ChatUser {
    private String name;
    private int id;
    private boolean op;
    private Socket connection;
    private Thread clientIO;
    private PrintWriter clientOut;

    public ChatUser(String name, int id, boolean op, Socket connection) {
        this.name = name;
        this.id = id;
        this.op = op;
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean op) {
        this.op = op;
    }

    public Socket getConnection() {
        return connection;
    }

    public Thread getClientIO() {
        return clientIO;
    }

    public PrintWriter getClientOut() {
        return clientOut;
    }

    public void setClientOut(PrintWriter clientOut) {
        this.clientOut = clientOut;
    }

    public void setClientIO(Thread clientIO){
        this.clientIO = clientIO;
    }
    @Override
    public boolean equals(Object o){
        if(!o.getClass().getName().equals(this.getClass().getName()))
            return false;
        else if (((ChatUser)o).getId() == this.getId())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
