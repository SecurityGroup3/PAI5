package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.nio.file.*;
import java.security.*;
import java.security.spec.*;


public class MainActivity extends AppCompatActivity {

    //Strings
    String sabanas;
    String camas;
    String mesas;
    String sillas;

    // Inputs aplicación
    EditText input1;
    EditText input2;
    EditText input3;
    EditText input4;

    RadioGroup radioGroup;
    RadioButton radioButton;

    // Setup Server information
    protected static String server = "10.0.2.2";
    protected static int port = 7070;
    protected static SSLSocket conexion;

    protected static String publicKeyY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzvziRpETVJ+agiN8iNs/VFpe5sxemrxnogunkfsNczHqUMa7Jw+VELtrD/G1cjT7LwQt2DoJ34UHWg3S7VgVs3xAqE30/im0HzgINRQyg0/proHIlL2rYqi4kANIPPo32BIRu0mbqfF6yWQ2ye0Ol1yfEyCgL90GysRb/BZunlJdGEFDvVY+u14r2WPWjpk1a3CJYpGQ1yDsXzYZZG372+ZwSmBI/qIX4AaDKeoAj+JuMLHTXYDhEQzRsc+qZhzJ7vv9/xSuxiPcL9fjpNFRIUoUQYlSC1XZ9o5mip3+ldzpKX/p8Du0JwFKdOtbmVaZdZR13VNRl2j0PgFBFICxYwIDAQAB";
    protected static String privateKeyY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDO/OJGkRNUn5qCI3yI2z9UWl7mzF6avGeiC6eR+w1zMepQxrsnD5UQu2sP8bVyNPsvBC3YOgnfhQdaDdLtWBWzfECoTfT+KbQfOAg1FDKDT+mugciUvatiqLiQA0g8+jfYEhG7SZup8XrJZDbJ7Q6XXJ8TIKAv3QbKxFv8Fm6eUl0YQUO9Vj67XivZY9aOmTVrcIlikZDXIOxfNhlkbfvb5nBKYEj+ohfgBoMp6gCP4m4wsdNdgOERDNGxz6pmHMnu+/3/FK7GI9wv1+Ok0VEhShRBiVILVdn2jmaKnf6V3Okpf+nwO7QnAUp061uZVpl1lHXdU1GXaPQ+AUEUgLFjAgMBAAECggEAA4wKkXyUPrla4otRkDqKLjcXGRmOeAtbfLV6Rt3tAAIAwZTlG7blAI4LXNoVmGtb3bnk9vCxY57VeesXJ+OkwLO4dxMHfooW17+K/i1K7bxsV2FvQ975oxtAW5Ej+Y3RlL2I0K6+TqdOEwC3IskkQRWgdamGHBK5WwBTbTX7Mt94aSZlj1eWsP7Aw/xcy2MzIfcuObnMXcBbSf3lKcZjqn6C777gdi7cDluF6Pv4aQNuafkEHyzdvcTGAkIjLyy+eCoUDHSgMOk1V95Cm/2GdGhuo1KdhFeodpCKRfPMfMMhx55JUrHRQRIxIp4vB6+M0pNwP0AdHce2J23RIbDBhQKBgQDz2xaMQrjCITnc7qHXosjn4qY+yVP7eFL95Fj67garCRFfWCDz9wH1gTqRTgW0BzB6Ua0dijtJqOjXkQtj4jEgP2nAZPt1Br8MH3/kFw7G7ToUcYrvH5Rd34AeFaItyOMr13UFE+jQE5DIAVQS1xwlsoTHsqizNCqXTlSvxdQQzQKBgQDZS8RJ1MhuQUWPwx1jIMOALqGTYjf6UqwnWsNCt062AmqVDDXwaRCBkd8D1Rz75L17AAV8jQStplFS8DrJvcD827eCmMnulQTKFlQDqjCiWS2rsOcXTR02WlTvO+Y1AHKr+NoDS7mXqG5UYnRwmQApQomn2uaVKev5twk68lEK7wKBgQDhgwe8GGwCY3W+YhTbdRg0kb7KK6d11BVz5HuBOySBgXZGZRq8Yv61ypeeDrhpf4iOZgAhZFdlzEXgLKuK6IAj4WDr4rdEx0Ngc7Ty+3vX7USb6VDhyKONmIZDzB/aFYA4aB043+uama13uPrGHZZTKl9uykMGzsRgfrkEgH+YNQKBgQCJBKKwX5FHHLdtUH4+XJGKSV0vH1bm7KyLYEsRzZ6/XESy2XFFuCplWYMD2qUdHP5yCfBrukxB50cOgla/kXyYtT9BBXEHlUXg3DV73hwuswb+ZV1CryiFdm5qMB4YO8ETOabXPy+3NsjjQvXjbkbQvZkpk/mkAzw+STFzzwpVVwKBgQDoVlpBdqKgNI3LBNjcXPPJYJrAD1DoLOMgSTq5udIWU9ZGYM8OG6LAWtmC4KfvHRvjlcNf4vAe9TtFgcIx+rJmferTePQDbWoJsWw4FeZP71XaKr/yQIEzXIrdVSYzE5kf7+V6C3QlMUqKbf3YC02zmlIKDlGJ4UKCZi1FukKJqA==";



    @RequiresApi(api = Build.VERSION_CODES.O)
    public PrivateKey getPrivateKey(Integer i) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(privateKeyY));
        PrivateKey privateKey = kf.generatePrivate(keySpecPKCS8);

        return privateKey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);
        input3 = (EditText) findViewById(R.id.input3);
        input4 = (EditText) findViewById(R.id.input4);

        radioGroup = findViewById(R.id.radioGroup);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build());


        // Capturamos el boton de Enviar
        View button = findViewById(R.id.button_send);

        // Llama al listener del boton Enviar
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);

                sabanas = input1.getText().toString();
                camas = input2.getText().toString();
                mesas = input3.getText().toString();
                sillas = input4.getText().toString();

                showDialog();

            }
        });


    }

    private void startClient(String message){
        String ip = "http://10.0.2.2";
        int puerto = 7071;
        String socket = "http://10.0.2.2:7071";
        try{
            KeyStore keyStore = KeyStore.getInstance("BKS");
            //FileInputStream file = new FileInputStream("D://ssiiCerts/server.cer");
            //System.out.println(file.toString());
            keyStore.load(getAssets().open("certs/server/server.bks"),
                    "changeit".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "changeit".toCharArray());

            KeyStore trustedStore = KeyStore.getInstance("BKS");
            trustedStore.load(getAssets().open("certs/server/cacertserverbueno.bks"), "changeit"
                    .toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustedStore);

            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager[] tM = tmf.getTrustManagers();
            KeyManager[] kM = kmf.getKeyManagers();
            context.init(kM, tM, null);

            SSLSocketFactory factory = context.getSocketFactory();
            conexion = (SSLSocket) factory.createSocket(server, puerto);
            conexion.startHandshake();

            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(conexion.getOutputStream()),true);

            try {
                System.out.println(message);
                salida.println(message);
                Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                System.out.println(e);
            }
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String messagereceived = entrada.readLine();
            Toast.makeText(getApplicationContext(), messagereceived.toString(), Toast.LENGTH_SHORT).show();


            conexion.close();
        }
        catch (Exception e){
            System.out.println("error: " + e.toString());
            Toast.makeText(getApplicationContext(), "No se ha podido realizar una conexión con el servidor", Toast.LENGTH_SHORT).show();

        }
    }

    private void showDialog() throws Resources.NotFoundException {
        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);
        input3 = (EditText) findViewById(R.id.input3);
        input4 = (EditText) findViewById(R.id.input4);
        if(TextUtils.isEmpty(input1.getText().toString()) ||
                TextUtils.isEmpty(input2.getText().toString()) ||
                TextUtils.isEmpty(input3.getText().toString()) ||
                TextUtils.isEmpty(input4.getText().toString())){
            Toast.makeText(getApplicationContext(), "Por favor introduce valores en todos los campos", Toast.LENGTH_SHORT).show();
        } else if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Por favor seleccione un usuario para firmar", Toast.LENGTH_SHORT).show();
        } else if ( Integer.parseInt(input1.getText().toString().trim()) > 300 ||
                Integer.parseInt(input2.getText().toString().trim()) > 300 ||
                Integer.parseInt(input3.getText().toString().trim())  > 300||
                Integer.parseInt(input4.getText().toString().trim()) > 300){
            Toast.makeText(getApplicationContext(), "Por favor seleccione valores por debajo de 300", Toast.LENGTH_SHORT).show();
        } else {
            final int numSab = Integer.parseInt(input1.getText().toString().trim());
            final int numCam = Integer.parseInt(input2.getText().toString().trim());
            final int numMes = Integer.parseInt(input3.getText().toString().trim());
            final int numSil = Integer.parseInt(input4.getText().toString().trim());
            new AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Se va a proceder al envio")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                // Catch ok button and send information
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("ResourceType")
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try{
                                        KeyPairGenerator keyParGenerator = KeyPairGenerator.getInstance("RSA");
                                        keyParGenerator.initialize(2048);
                                        KeyPair keyPair = keyParGenerator.generateKeyPair();
                                        PublicKey publicKey = keyPair.getPublic();
                                        //PrivateKey privateKey = keyPair.getPrivate();



                                        Signature firma = Signature.getInstance("SHA256withRSA");
                                        String dataNumbers = numSab + "-" + numCam + "-" + numMes + "-" + numSil;

                                        int index = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
                                        if(index == 0) {
                                            PrivateKey privateKey = getPrivateKey(index);
                                            firma.initSign(privateKey);
                                            firma.update(dataNumbers.getBytes());
                                            byte[] firma_ = firma.sign();
                                            String firmmm = Base64.encodeToString(firma_, Base64.NO_WRAP);

                                            String privateKeyy = java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
                                            String publickeyy = publicKeyY;
                                            //PrivateKey privateKey =

                                            System.out.println("USER 1 public and private");
                                            System.out.println(publickeyy);
                                            System.out.println(privateKeyy);

                                            String message = dataNumbers + "," + firmmm + "," + publickeyy;
                                            startClient(message);

                                        }else if(index == 1) {
                                            firma.update(dataNumbers.getBytes());
                                            byte[] firma_ = firma.sign();
                                            String firmmm = Base64.encodeToString(firma_,  Base64.NO_WRAP);
                                            String publickeyy = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
                                            //String privateKeyy = java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());


                                            System.out.println("USER 2 public and private");
                                            System.out.println(publickeyy);
                                            //System.out.println(privateKeyy);


                                            String message = dataNumbers + "," + firmmm + "," + publickeyy;
                                            startClient(message);

                                        }else{
                                            firma.update(dataNumbers.getBytes());
                                            byte[] firma_ = firma.sign();
                                            String firmmm = Base64.encodeToString(firma_, Base64.NO_WRAP);
                                            String publickeyy = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
                                            //String privateKeyy = java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());


                                            System.out.println("USER 3 public and private");
                                            System.out.println(publickeyy);
                                            //System.out.println(privateKeyy);

                                            String message = dataNumbers + "," + firmmm + "," + publickeyy;
                                            startClient(message);
                                        }


                                    }catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                    )
                    .setNegativeButton(android.R.string.no, null).show();
        }

        }
    }