package tests;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class ClientTest extends Thread{
    
    private SSLSocket conexion;

    public void run(){
        try{
            String ip = InetAddress.getLocalHost().getHostAddress();
            List<String> credentials = Arrays.asList("Test", "Test!");;
            new ClientTest(ip, 7071).logIn(credentials.get(0), credentials.get(1));
        } catch(Exception e) {
            System.out.println("error");
        }
        
    }

    public ClientTest(String address, int port) throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManager[] tM = AuxiliarMethods.getTrustFactoryClient().getTrustManagers();
        KeyManager[] kM = AuxiliarMethods.getKeyFactoryClient().getKeyManagers();
        context.init(kM, tM, null);

        SSLSocketFactory factory = context.getSocketFactory();
        conexion = (SSLSocket) factory.createSocket(address, port);
        conexion.startHandshake();
    }


    public ClientTest() {
        run();
    }

    public void logIn(String userName, String password) {
        Front.getFront(conexion, userName, password);
    }


    public static void main(String[] args) throws Exception {
        for(int i = 0; i < 300; ++i){
            new Thread(new ClientTest()).start();

         }
        System.out.println("Ejecutados los 300 hilos");
        

    }
}
