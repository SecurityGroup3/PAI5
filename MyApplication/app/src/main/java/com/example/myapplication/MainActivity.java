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

    protected static String publicKey2 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoCYI49zqOcQ+3zTCWaFduIMDgVUDECL3Ayer/GAA7DajZGuSan/RtYFz//ZhPMsGW9N/Aeq9FjbfJQSXrdfoyffmD38IqwOtlcDHUh5/nMFAb2aHP1xCYPpLvrWyBxwxewvlqkRInoDvma/lEt9EJkTCcEmGuTcfJmqHYgdU6qv8DhAfiX38Dxe3gN/VQiI+rlsl54oTsxY4Px3iqFMy1S7cAg8alPk4ms9S+btem7EOuX/BcX8r8PFLUl3Ds6+sVvCtw9G2gG8eJdY+4HTnA5q1GODDjUMJ5nTJXqb7LIVF79/vODwmRSdBEurj36cBS+2tbEg1pW7w1mehXxrN/wIDAQAB";
    protected static String privateKey2 = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCgJgjj3Oo5xD7fNMJZoV24gwOBVQMQIvcDJ6v8YADsNqNka5Jqf9G1gXP/9mE8ywZb038B6r0WNt8lBJet1+jJ9+YPfwirA62VwMdSHn+cwUBvZoc/XEJg+ku+tbIHHDF7C+WqREiegO+Zr+US30QmRMJwSYa5Nx8maodiB1Tqq/wOEB+JffwPF7eA39VCIj6uWyXnihOzFjg/HeKoUzLVLtwCDxqU+Tiaz1L5u16bsQ65f8Fxfyvw8UtSXcOzr6xW8K3D0baAbx4l1j7gdOcDmrUY4MONQwnmdMlepvsshUXv3+84PCZFJ0ES6uPfpwFL7a1sSDWlbvDWZ6FfGs3/AgMBAAECggEAA4Icdb6+1CR2wCBWwEHnB1tkte5CfUBW1ZcxOaZyFA/1BQZ6PvetL2MLmH2G5HRfdPQtdxbzrMs87ixV6kqtiyccFMDSqXRmLBP8tHwojYA073vOgKvTucN3P1FET3WQUUK8dZNosoLekij78ALmFPSH/I+6ciTr+2/KpsdvAR5Z2GnSKYoVnG0KwV4z9aDFw0i1oI93K5LibNJpD7lmhsEwZq4oObSQ4sCTb6DX/Mwox3mxPtycCxSBVYKqQjuSZGj39zkhSBlRp7sIqahGyuBySmrI7FdkBMSBhDxgAeD0UCRbHyMtRZTnq53C2xg8HW1lJq/SBBKoUU8t5smboQKBgQDMEch7GCr/Adkl2uJ8MXhC6At/SsxglTA5dZaCaTF/5UssYmof/nr4YdYlGsjWmlkXR3IQ03R2gBV2PBn+wLdmc1x1ovv6YD4WIGaqAm40K3P92emEAyBP/PPHjNByAOWFemikfHFzpUFhmxSVJ9+IOlugyTD7EvxwzPJPnRgZsQKBgQDI5wBF/Z0UvGk2k1Vgdy7ge4NguW7fkcsCs/trhA6THCl/gISpTRCZ26HzK0aGwveDuabaL0ZmzNSgPmK35BTAVbH9fPJptCURlPmWDTsaLg5gMYTyi3JeWWROqOeNG4Xr5BR1cOftguuItuIrnZw5Wg2yRsnVfKHctPewY6aerwKBgE9fv135qYeknWS2/Y8W4tMLVKV/X31Vqfs/fHpfFizL7/Bkmyi0n/oKe/PUtjeas3p1hK18wfjxmudxOx82sF8+EcGD0oSxeJS8p5IK/fh2ylf7sXNyj9fs6Jk+cTlWle/UC+2mpa44ofSoEFpvtudS457ngpC18tChNsK2tNARAoGAfiaPRWO6/wpQBrT9J1WvWcY0+Md3l2s0zr21Yg3KGRXV+Pn6U1TQ18vSJZ9G6dgDE/O/kcGds29rFDQYSOAvJ5an7URH85fqrt+c2Vv1gLrqy/xMPwDRD+2RztLAkxp1PWo6boQ3HizXOUGdQ0X0kw65CvjoQ+W+vZfk8B2iwoECgYAKj0dBjp58xUJxo3un/mWld6I3SinxpUkcSETjVW+V3cTtcg8a+OgA3oQBIg9y5PAbi4sRgSOIJ2Wh28K3IkhyzxlaKZY0wv7mshYV7Nd87d7JJGTJHFiRiamFh8hWIp/hZIR+yKWxB3OTf7nijZXvYBitZq555694awBii8Z5BQ==";

    protected static String publicKey3= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyZgO0jNogfzqCoEa/zDp0CaJijC+07lu9Mc8iTzY5/B5XNV+RxnRnYjFwUIDq9nugAimY0QCVk/EC6bG+LYbp8qNCH2dpK2WLWBmYGh6IMD0sSSnhTiQsptYszTbiFIc0alQmSBy3AnaQpqTpq3DNenunrFCJ2ZtX9BU0Zjrt09iqWfTnk/4/EsvVWRR9ntn9rpnB6GA50PvkI02+whHl4uldUrUXRqrRFoJ7pRVgjW1OdVyOk/WTfhSMypcv1SkpoO6w2dj+rRe/DNT1SQGVogIcC2kEAyntbUai+3r5/Cl/kY4soUHZZnNbUVkiUblUOxpmG3R0h0xY8wall6p/wIDAQAB";
    protected static String privateKey3 = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDJmA7SM2iB/OoKgRr/MOnQJomKML7TuW70xzyJPNjn8Hlc1X5HGdGdiMXBQgOr2e6ACKZjRAJWT8QLpsb4thunyo0IfZ2krZYtYGZgaHogwPSxJKeFOJCym1izNNuIUhzRqVCZIHLcCdpCmpOmrcM16e6esUInZm1f0FTRmOu3T2KpZ9OeT/j8Sy9VZFH2e2f2umcHoYDnQ++QjTb7CEeXi6V1StRdGqtEWgnulFWCNbU51XI6T9ZN+FIzKly/VKSmg7rDZ2P6tF78M1PVJAZWiAhwLaQQDKe1tRqL7evn8KX+RjiyhQdlmc1tRWSJRuVQ7GmYbdHSHTFjzBqWXqn/AgMBAAECggEAN2XXUNE6u1nekNNosU7mOcKVUknR7snK4W4sxwPXEWnGWAn9IRnJvhI7Ub/L1XpWbY2y951VTRWvaIpErE0S7XmeZwJqAzqGheKzT3P2d7C+rT1G4bepH8PLzLdOSrhR3hW6pdHCbpAwj9P9ljSy68pkvew1IzGlBcrwvAZbZCxp9fkVtcnAEZnqya0i0Pj/vtc/aI6VHmYCbX+IXilF8OGFMlzlHqkR3EMl7tHmlXWHe5Hgg3vARkjBvrQFVGeeRqF2SgAFoqRwwtihUa1ptVVxGHhoM/iUxWgdnLz9Lt1L1jotZlTz0hLVpdaRcjhu6Ls72N0jtt/StuUoiC2rwQKBgQDxvIZ8+KoR/eP360dPqYthU//A14txCKYHkQOCOkYRuq9xuhc2eIkfJRgG2CkT4ANCy2WZ5bZn9nNDfHPinjODhS+PPUFtRPuUDYZMSQwci7iqwDTzpCF5vlOHNaA+D8zpI7I3LrHz45SWio5YIklzGUqX6/ar1ZJjL9kt+9OCwQKBgQDVfSxW/0PsGycUyxDepgJ5OQkgBmUAPaPxX2jfOUpUQGnblmJa4kUqDM6Zl+6AlA8U49hqGOhwm86MEut/XIpjCH/Yi8716VduK4t8zNLuS3cfOqoX71RoXiBzVbKnispd7XZS4xCzs0d+lty6Rczk2X47OxbJIsA3Ta+9SQMcvwKBgFjmwncXZER1oO8+Qd2UNsQ2hio/mvKZAaZquGWeVMKUa41hqMPJlHQ1yh5KROEgU1KvRtBQYMLfKgi5rky7baqCwrjlCDjNU9BhIPDRNHkTEXkKcu7ff8gsLL/fAu2QFgXvsM0GiT9uhOjQUAUtpuGDfaFdkDwlsU50/GQi5uiBAoGAWR6dydtZoVBcaiOy8R7f3XbfxkylEQqqS3KGttiVnMIe7pnCiSn3sWwTOq7f0zD7cr8CcpffQFLqUDL9t/cLlffQVrdWN/Wml8j5u6lNTEiYe6LjqoYv+DmC1mKp56Lag4dDY6qCKGHosIGNtvy4YbghJ9ys0F3/W5bEjAHUmMUCgYBqFDsngQKJrSJGe371g5AmiCsDteQnG66PGMRsajKeAFdTI6CQ8x4FfymkzawyH6qBTBT8cNAcg95iTZOT29eM4SduzVGAl2TljYuu//R6TCnGqpsH27Bml+q8A+p0r3/+2I4GfMtP4apWaOv0EMSekarMprihF9yRRyNWksWYJw==";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PrivateKey getPrivateKey(Integer i) throws Exception {
        String keyString = "";
        switch (i){
            case 0:
                keyString = privateKeyY;
                break;
            case 1:
                keyString = privateKey2;
                break;
            case 2:
                keyString = privateKey2;
                break;
        }

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 =
                new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(keyString));
        PrivateKey privateKey = kf.generatePrivate(keySpecPKCS8);
        return privateKey;
    }

    public void createKeyPair() throws Exception {
        KeyPairGenerator keyParGenerator = KeyPairGenerator.getInstance("RSA");
        keyParGenerator.initialize(2048);
        KeyPair keyPair = keyParGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
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
                                        Signature firma = Signature.getInstance("SHA256withRSA");
                                        String dataNumbers = numSab + "-" + numCam + "-" + numMes + "-" + numSil;

                                        int index = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
                                        PrivateKey privateKey = getPrivateKey(index);
                                        firma.initSign(privateKey);
                                        firma.update(dataNumbers.getBytes());
                                        byte[] firma_ = firma.sign();
                                        String firmmm = Base64.encodeToString(firma_, Base64.NO_WRAP);
                                        String message = dataNumbers + "," + firmmm + "," + index;
                                        startClient(message);
                                    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
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