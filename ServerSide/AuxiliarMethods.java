import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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
        if (sabanas <= 0 || camas <= 0 || sillas <= 0 || mesas <= 0 ||
                sabanas > 300 || camas > 300 || sillas > 300 || mesas > 300) {
            // Call to error
            System.out.println("Se han recibido valores incorrectos, petición denegada");
            writeIntermediateFile("No");
            writeTransactionsFile(dataSplit[0] + "NOTOKEY");
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
            writeIntermediateFile("OK");
            writeTransactionsFile(dataSplit[0] + "OK");

            return true;
        }
    }

    public static void writeIntermediateFile(String state) {
        BufferedReader objReader = null;
        String strCurrentLine = null;
        try {
            objReader = new BufferedReader(new FileReader("./log.txt"));
            final List<String> lines = Files.lines(Paths.get("./log.txt")).collect(Collectors.toList());
            if (state == "OK") {
                Integer total = 0;
                Integer acert = 0;
                Integer i = 0;
                while ((strCurrentLine = objReader.readLine()) != null) {
                    System.out.println(strCurrentLine);
                    if (i == 0) {
                        total = Integer.valueOf(strCurrentLine);
                    } else if (i == 1) {
                        acert = Integer.valueOf(strCurrentLine);
                    }
                    i++;
                }
                total = total + 1;
                acert = acert + 1;
                lines.add(Math.min(1, lines.size()), String.valueOf(total));
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./log.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines) {
                        out.append(line).append(System.lineSeparator());
                    }
                }

                lines.add(Math.min(2, lines.size()), String.valueOf(acert));
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./log.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines) {
                        out.append(line).append(System.lineSeparator());
                    }
                }
            } else {
                Integer total = 0;
                Integer i = 0;
                while ((strCurrentLine = objReader.readLine()) != null) {
                    System.out.println(strCurrentLine);
                    if (i == 0) {
                        total = Integer.valueOf(strCurrentLine);
                    }
                    i++;
                }
                total = total + 1;
                lines.add(Math.min(1, lines.size()), String.valueOf(total));
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./log.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines) {
                        out.append(line).append(System.lineSeparator());
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void writeTransactionsFile(String transaction) {
        BufferedReader objReader = null;

        try {
            objReader = new BufferedReader(new FileReader("./transactions.txt"));
            final List<String> lines = Files.lines(Paths.get("./transactions.txt")).collect(Collectors.toList());
            lines.add(Math.min(lines.size(), lines.size()), transaction);
            try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./transactions.txt"),
                    Charset.forName("UTF-8"))) {
                for (final String line : lines) {
                    out.append(line).append(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void writeTendenceFile() {
        BufferedReader objReader = null;

        String strCurrentLine;

        try {
            objReader = new BufferedReader(new FileReader("./tendences.txt"));
            Integer i = 0;
            while ((strCurrentLine = objReader.readLine()) != null) {
                System.out.println(strCurrentLine);
                i++;
            }
            objReader = new BufferedReader(new FileReader("./log.txt"));
            float total = 0;
            float acert = 0;
            Integer j = 0;
            while ((strCurrentLine = objReader.readLine()) != null) {
                System.out.println(strCurrentLine);
                if (j == 0) {
                    total = Float.valueOf(strCurrentLine);
                } else if (j == 1) {
                    acert = Float.valueOf(strCurrentLine);
                }
                j++;
            }
            objReader = new BufferedReader(new FileReader("./tendences.txt"));
            float ratio_ = acert / total;
            if (i < 2) {
                final List<String> lines = Files.lines(Paths.get("./tendences.txt")).collect(Collectors.toList());
                Month mes = LocalDateTime.now().getMonth();
                int anyo = LocalDateTime.now().getYear();
                String result = mes.name() + "," + String.valueOf(anyo) + "," + String.valueOf(ratio_) + "," + "0";
                lines.add(Math.min(lines.size(), lines.size()), result);
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./tendences.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines) {
                        out.append(line).append(System.lineSeparator());
                    }
                }
                final List<String> lines_ = Files.lines(Paths.get("./log.txt")).collect(Collectors.toList());

                lines_.add(Math.min(1, lines.size()), "0");
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./log.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines_) {
                        out.append(line).append(System.lineSeparator());
                    }
                }

                final List<String> lines__ = Files.lines(Paths.get("./log.txt")).collect(Collectors.toList());

                lines__.add(Math.min(2, lines.size()), "0");
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./log.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines__) {
                        out.append(line).append(System.lineSeparator());
                    }
                }

            } else {
                final List<String> lines = Files.lines(Paths.get("./tendences.txt")).collect(Collectors.toList());
                Month mes = LocalDateTime.now().getMonth();
                int anyo = LocalDateTime.now().getYear();
                objReader = new BufferedReader(new FileReader("./tendences.txt"));
                List<String> linesList = new ArrayList<>();
                while ((strCurrentLine = objReader.readLine()) != null) {
                    linesList.add(strCurrentLine.split(",")[2]);
                }
                float pastMonth = Float.valueOf(linesList.get(linesList.size() - 1));
                float past2Month = Float.valueOf(linesList.get(linesList.size() - 2));
                String tende = "";
                if ((pastMonth < ratio_ || past2Month < ratio_) || ((pastMonth == ratio_ || past2Month < ratio_))
                        || (pastMonth < ratio_ || past2Month == ratio_)) {
                    tende += "+";
                } else if (pastMonth > ratio_ || past2Month > ratio_) {
                    tende += "-";
                } else if (pastMonth == ratio_ && past2Month == ratio_) {
                    tende += "0";

                }

                String result = mes.name() + "," + String.valueOf(anyo) + "," + String.valueOf(ratio_) + "," + tende;
                lines.add(Math.min(lines.size(), lines.size()), result);
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./tendences.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines) {
                        out.append(line).append(System.lineSeparator());
                    }
                }

                final List<String> lines_ = Files.lines(Paths.get("./log.txt")).collect(Collectors.toList());

                lines_.add(Math.min(1, lines.size()), "0");
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./log.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines_) {
                        out.append(line).append(System.lineSeparator());
                    }
                }

                final List<String> lines__ = Files.lines(Paths.get("./log.txt")).collect(Collectors.toList());

                lines__.add(Math.min(2, lines.size()), "0");
                try (final BufferedWriter out = Files.newBufferedWriter(Paths.get("./log.txt"),
                        Charset.forName("UTF-8"))) {
                    for (final String line : lines__) {
                        out.append(line).append(System.lineSeparator());
                    }
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
