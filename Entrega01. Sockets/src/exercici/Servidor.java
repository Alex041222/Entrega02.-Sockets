package exercici;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Servidor {
    public static void main(String[] args) {
        // Validació d'arguments: Port i Paraula Clau [cite: 5, 14]
        if (args.length < 2) {
            System.out.println("Error: Falten arguments (Port i Paraula Clau)");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String paraulaClau = args[1];

        // Mostrem configuració inicial [cite: 19]
        System.out.println("PORT_SERVIDOR: " + port);
        System.out.println("PARAULA_CLAU_SERVIDOR: \"" + paraulaClau + "\"");
        System.out.println("> Server chat at port " + port);

        // Gestió d'errors amb try/catch [cite: 9]
        try (ServerSocket servidor = new ServerSocket(port);
             Scanner teclat = new Scanner(System.in)) {

            System.out.println("> Inicializing server... OK"); // [cite: 8]
            
            Socket socket = servidor.accept(); 
            System.out.println("> Connection from client... OK");

            PrintWriter sortida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("> Inicializing chat... OK");

            boolean continuar = true;
            while (continuar) {
                // El servidor primer rep [cite: 18]
                String msgRebut = entrada.readLine();
                
                if (msgRebut == null) {
                    continuar = false;
                } else {
                    System.out.println("#Rebut del client: " + msgRebut);

                    // Tancament per paraula clau [cite: 13, 16]
                    if (msgRebut.contains(paraulaClau)) {
                        System.out.println("> Client keyword detected!");
                        continuar = false;
                    } else {
                        // El servidor respon per consola [cite: 11]
                        System.out.print("#Enviar al client: ");
                        String msgEnviar = teclat.nextLine();
                        sortida.println(msgEnviar);

                        if (msgEnviar.contains(paraulaClau)) {
                            continuar = false;
                        }
                    }
                }
            }

            // Tancament de recursos [cite: 17]
            System.out.println("> Closing chat... OK");
            socket.close();
            System.out.println("> Closing server... OK");
            System.out.println("> Bye!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}