import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import java.util.List;

import javax.net.ssl.KeyManager;

import java.net.InetAddress;

public class Client {

    private SSLSocket conexion;

    public Client(String address, int port) throws Exception {

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManager[] tM = AuxiliarMethods.getTrustFactoryClient().getTrustManagers();
        KeyManager[] kM = AuxiliarMethods.getKeyFactoryClient().getKeyManagers();
        context.init(kM, tM, null);

        SSLSocketFactory factory = context.getSocketFactory();
        conexion = (SSLSocket) factory.createSocket(address, port);
        conexion.startHandshake();
    }

    public void logIn(String userName, String password) {
        Front.getFront(conexion, userName, password);
    }

    public static void main(String[] args) throws Exception {
        String ip = InetAddress.getLocalHost().getHostAddress();
        List<String> credentials = Front.getData();
        new Client(ip, 7071).logIn(credentials.get(0), credentials.get(1));

    }
}