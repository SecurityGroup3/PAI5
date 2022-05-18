package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

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
    protected static String server = "http://10.0.2.2";
    protected static int port = 7070;
    protected static SSLSocket conexion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startClient();


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

                startClient();

                Toast.makeText(getApplicationContext(), radioButton.getText(), Toast.LENGTH_SHORT).show();
                showDialog();

            }
        });


    }

    private void startClient(){
        String ip = "http://10.0.2.2";
        int puerto = 7071;
        String socket = "http://10.0.2.2:7071";
        try{
            KeyStore keyStore = KeyStore.getInstance("BKS");
            //FileInputStream file = new FileInputStream("D://ssiiCerts/server.cer");
            //System.out.println(file.toString());
            keyStore.load(getAssets().open("certs/server/certificate.bks"),
                    "mypassword".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "mypassword".toCharArray());

            KeyStore trustedStore = KeyStore.getInstance("BKS");
            trustedStore.load(getAssets().open("certs/client/keystore2-0.jks"), "changeit"
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
            System.out.println("enviando ... Hola Mundo!");
            salida.println("Hola mundo");

            conexion.close();
        }
        catch (Exception e){
            System.out.println("error: " + e.toString());
        }
    }

    private void showDialog() throws Resources.NotFoundException {
        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);
        input3 = (EditText) findViewById(R.id.input3);
        input4 = (EditText) findViewById(R.id.input4);
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
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try{
                                    KeyPairGenerator keyParGenerator = KeyPairGenerator.getInstance("RSA");
                                    keyParGenerator.initialize(2048);
                                    KeyPair keyPair = keyParGenerator.generateKeyPair();
                                    PublicKey publicKey = keyPair.getPublic();
                                    PrivateKey privateKey = keyPair.getPrivate();
                                    Signature firma = Signature.getInstance("SHA256withRSA");
                                    firma.initSign(privateKey);
                                    String messageSignature = numSab + "-" + numCam + "-" + numMes + "-" + numSil;
                                    byte[] bytesOfMessageSignature = messageSignature.getBytes();
                                    firma.update(bytesOfMessageSignature);
                                    String message = messageSignature + "-" + publicKey + "-" + bytesOfMessageSignature.toString();
                                    firma.sign();
                                }catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }

                )
                .setNegativeButton(android.R.string.no, null).show();
        }
    }