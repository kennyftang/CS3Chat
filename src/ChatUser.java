import java.net.Socket;

public class ChatUser {
    private String name;
    private int id;
    private boolean op;
    private Socket connection;
    private Thread clientIO;

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
    public void setClientIO(Thread clientIO){
        this.clientIO = clientIO;
    }
}
