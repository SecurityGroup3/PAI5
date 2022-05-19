package tests;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;

public class ServerTest {

    private static final String[] protocols = new String[] { "TLSv1.3" };
    private static final String[] cipherSuites = new String[] { "TLS_AES_128_GCM_SHA256" };

    private SSLServerSocket serverSocket;

    public ServerTest(int port) throws Exception {

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManager[] tM = AuxiliarMethods.getTrustFactoryServer().getTrustManagers();
        KeyManager[] kM = AuxiliarMethods.getKeyFactoryServer().getKeyManagers();
        context.init(kM, tM, null);

        SSLServerSocketFactory factory = context.getServerSocketFactory();
        serverSocket = (SSLServerSocket) factory.createServerSocket(port);
        serverSocket.setEnabledCipherSuites(cipherSuites);
        serverSocket.setEnabledProtocols(protocols);

    }

    public void startServer() {
        ServerConfigTest.startServer(serverSocket);
    }

    public static void main(String[] args) throws Exception {

        new ServerTest(7071).startServer();

    }
}
