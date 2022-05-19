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


    @RequiresApi(api = Build.VERSION_CODES.O)
    protected PublicKey getKeys() throws Exception{

        String publicKeyContent = new String(getAssets().open("keypair/user1").toString());

        //Base64.Decoder b64 = Base64.getDecoder();
        byte[] decoded = Base64.decode(publicKeyContent, Base64.DEFAULT);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory keyF = KeyFactory.getInstance("RSA");
        PublicKey publicK = keyF.generatePublic(spec);
        return publicK;
    }

    public PrivateKey getPrivateKey() throws Exception {

        String tContents = "";

        try {
            InputStream stream = getAssets().open("keypair/user1");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }


        //String publicKeyContent = new String(getAssets().open("keypair/user1").toString());
        System.out.println("publicKeyContentpublicKeyContent");
        System.out.println(tContents);

        String privKeyPEM = tContents.replace("-----BEGIN OPENSSH PRIVATE KEY-----\n", "");
        privKeyPEM = privKeyPEM.replace("-----END OPENSSH PRIVATE KEY-----", "");

        byte[] keyBytes = privKeyPEM.getBytes();
        //byte [] decoded = Base64.decode(privKeyPEM, Base64.DEFAULT);

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
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
                                        PrivateKey privateKey = keyPair.getPrivate();

                                        Signature firma = Signature.getInstance("SHA256withRSA");
                                        firma.initSign(privateKey);
                                        String dataNumbers = numSab + "-" + numCam + "-" + numMes + "-" + numSil;
                                        int index = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
                                        if(index == 0) {
                                            firma.update(dataNumbers.getBytes());
                                            byte[] firma_ = firma.sign();
                                            String firmmm = Base64.encodeToString(firma_, Base64.NO_WRAP);
                                            String publickeyy = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());


                                            System.out.println("USER 1");
                                            System.out.println(firmmm);

                                            String message = dataNumbers + "," + firmmm + "," + publickeyy;
                                            startClient(message);

                                        }else if(index == 1) {
                                            firma.update(dataNumbers.getBytes());
                                            byte[] firma_ = firma.sign();
                                            String firmmm = Base64.encodeToString(firma_,  Base64.NO_WRAP);
                                            String publickeyy = Base64.encodeToString(publicKey.getEncoded(),  Base64.NO_WRAP | Base64.URL_SAFE);


                                            System.out.println("USER 2");
                                            System.out.println(firmmm);

                                            String message = dataNumbers + "," + firmmm + "," + publickeyy;
                                            startClient(message);

                                        }else{
                                            firma.update(dataNumbers.getBytes());
                                            byte[] firma_ = firma.sign();
                                            String firmmm = Base64.encodeToString(firma_, Base64.NO_WRAP);
                                            String publickeyy = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());


                                            System.out.println("USER 3");
                                            System.out.println(firmmm);

                                            String message = dataNumbers + "," + firmmm + "," + publickeyy;
                                            startClient(message);
                                        }


                                    }catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                    )
                    .setNegativeButton(android.R.string.no, null).show();
        }

        }
    }