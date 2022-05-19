package tests;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConfigTest {

	public static void startServer(final ServerSocket conexion) {
		System.out.println("Iniciando....");

		new Thread() {

			public void run() {
				try {

					while (true) {

						Socket accepted = conexion.accept();
						accepted.setSoLinger(true, 1000);
						System.out.println("Conectado con ... " + accepted.getInetAddress().getHostAddress());
						BufferedReader acceptedInput = new BufferedReader(new InputStreamReader(
								accepted.getInputStream()));
						PrintWriter acceptedOutput = new PrintWriter(
								new OutputStreamWriter(accepted.getOutputStream()));
						String user = acceptedInput.readLine();
						 String password = acceptedInput.readLine(); 
						System.out.println(user);
						System.out.println(password); 

						// output.flush();
						acceptedOutput.close();
						acceptedInput.close();
						accepted.close();

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();
	}
}
