package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


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
    protected static String server = "192.168.1.133";
    protected static int port = 7070;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);
        input3 = (EditText) findViewById(R.id.input3);
        input4 = (EditText) findViewById(R.id.input4);

        radioGroup = findViewById(R.id.radioGroup);

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


                Toast.makeText(getApplicationContext(), radioButton.getText(), Toast.LENGTH_SHORT).show();
                //showDialog();

            }
        });


    }

    // Creación de un cuadro de dialogo para confirmar pedido
    /**
    private void showDialog() throws Resources.NotFoundException {
        CheckBox sabanas = (CheckBox) findViewById(R.id.checkBox_sabanas);


        if (!sabanas.isChecked()) {
            // Mostramos un mensaje emergente;
            //int numSab = Integer.parseInt(mEdit.getText().toString());
            Toast.makeText(getApplicationContext(), ("num") , Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Se va a proceder al envio")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                // Catch ok button and send information
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // 1. Extraer los datos de la vista

                                    // 2. Firmar los datos

                                    // 3. Enviar los datos

                                    Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                                }
                            }

                    )
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }
     **/


}
