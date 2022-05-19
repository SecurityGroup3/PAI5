import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class AuxiliarMethods {

    public static TrustManagerFactory getTrustFactoryClient() throws Exception {

        KeyStore tKeyStore = KeyStore.getInstance("JKS");
        tKeyStore.load(new FileInputStream("certs/server/cacerts.jks"), "changeit".toCharArray());

        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(tKeyStore);

        return trustFactory;
    }

    public static KeyManagerFactory getKeyFactoryClient() throws Exception {

        KeyStore kS = KeyStore.getInstance("JKS");
        kS.load(new FileInputStream("certs/client/keystore2.jks"), "changeit".toCharArray());

        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(kS, "changeit".toCharArray());

        return keyFactory;
    }

    public static KeyManagerFactory getKeyFactoryServer() throws Exception {
        KeyStore kS = KeyStore.getInstance("JKS");
        kS.load(new FileInputStream("certs/server/server.jks"), "changeit".toCharArray());

        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(kS, "changeit".toCharArray());

        return keyFactory;
    }

    public static TrustManagerFactory getTrustFactoryServer() throws Exception {

        KeyStore tKeyStore = KeyStore.getInstance("JKS");
        tKeyStore.load(new FileInputStream("certs/server/cacertsserverjksder.jks"), "changeit".toCharArray());

        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(tKeyStore);

        return trustFactory;
    }

    public static Boolean checkDataReceived(String data) throws Exception {
        System.out.println("data received");
        System.out.println(data);
        String[] dataSplit = data.split(",");
        String[] values = dataSplit[0].split("-");
        Integer sabanas = Integer.valueOf(values[0]);
        Integer camas = Integer.valueOf(values[1]);
        Integer sillas = Integer.valueOf(values[2]);
        Integer mesas = Integer.valueOf(values[3]);
        String message = dataSplit[0];
        String firm = dataSplit[1];
        String publicKey = dataSplit[2];
        if(sabanas <= 0 || camas <= 0 || sillas <= 0 || mesas <= 0 ||
        sabanas > 300 || camas > 300  || sillas > 300  || mesas > 300){
            //Call to error
            System.out.println("Se han recibido valores incorrectos, petición denegada");
            return false;
        } else {
            byte[] bytesPublicKey = Base64.getDecoder().decode(dataSplit[2].getBytes());
            byte[] bytesFirma = Base64.getDecoder().decode(dataSplit[1].getBytes());

            PublicKey publicK = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytesPublicKey));
            Signature sg = Signature.getInstance("SHA256withRSA");
            sg.initVerify(publicK);
            sg.update(message.getBytes());
            Boolean result = sg.verify(bytesFirma);
            System.out.println(result);


            return true;
        }
    }
}
