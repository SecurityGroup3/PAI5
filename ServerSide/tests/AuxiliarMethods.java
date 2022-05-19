package tests;
import java.io.FileInputStream;
import java.security.KeyStore;

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
        kS.load(new FileInputStream("certs/server/keystore.jks"), "changeit".toCharArray());

        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(kS, "changeit".toCharArray());

        return keyFactory;
    }

    public static TrustManagerFactory getTrustFactoryServer() throws Exception {

        KeyStore tKeyStore = KeyStore.getInstance("JKS");
        tKeyStore.load(new FileInputStream("certs/client/cacerts.jks"), "changeit".toCharArray());

        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(tKeyStore);

        return trustFactory;
    }
}
