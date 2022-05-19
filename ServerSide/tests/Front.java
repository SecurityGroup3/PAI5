package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Front {

    public static void getFront(Socket conexion, String userName, String password) {
        try {
            PrintWriter output = new PrintWriter(new OutputStreamWriter(conexion.getOutputStream()));
            output.println(userName);

            output.println(password);

            output.flush();

            BufferedReader input = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String received = input.readLine();

            //JOptionPane.showMessageDialog(null, received);

            output.close();
            input.close();
            conexion.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static List<String> getData() {
        String userName = JOptionPane.showInputDialog(null, "Usuario : ");
        String password = JOptionPane.showInputDialog(null, "Contraseña : ");
        List<String> result = new ArrayList<String>();
        result.add(userName);
        result.add(password);
        return result;
    }

}
