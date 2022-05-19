import java.net.InetAddress;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        new Server(7071).startServer();
        String ip = InetAddress.getLocalHost().getHostAddress();
        for (int i = 0; i <= 300; i++) {
            List<String> credentials = Front.getData();
            new Client(ip, 7071).logIn(credentials.get(0), credentials.get(1));
        }
    }

}
