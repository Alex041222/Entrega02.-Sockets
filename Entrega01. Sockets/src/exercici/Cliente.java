package exercici;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: Falten arguments (Port i Paraula Clau)");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String paraulaClau = args[1];

        System.out.println("PORT_SERVIDOR: " + port);
        System.out.println("PARAULA_CLAU_CLIENT: \"" + paraulaClau + "\"");
        System.out.println("> Client chat to port " + port);

        try (Socket socket = new Socket("127.0.0.1", port);
             Scanner teclat = new Scanner(System.in)) {

            System.out.println("> Inicializing client... OK"); // [cite: 8]

            PrintWriter sortida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("> Inicializing chat... OK");

            boolean continuar = true;
            while (continuar) {
                // El client inicia la conversa enviant [cite: 18, 30]
                System.out.print("#Enviar al servidor: ");
                String msgEnviar = teclat.nextLine();
                sortida.println(msgEnviar);

                if (msgEnviar.contains(paraulaClau)) { // [cite: 13]
                    continuar = false;
                } else {
                    // Rebre resposta del servidor [cite: 31]
                    String msgRebut = entrada.readLine();

                    if (msgRebut == null) {
                        System.out.println("> Connexió perduda.");
                        continuar = false;
                    } else {
                        System.out.println("#Rebut del servidor: " + msgRebut);

                        if (msgRebut.contains(paraulaClau)) { // [cite: 16]
                            System.out.println("> Server keyword detected!");
                            continuar = false;
                        }
                    }
                }
            }

            // Tancament de recursos [cite: 17]
            System.out.println("> Closing chat... OK");
            socket.close();
            System.out.println("> Closing client... OK");
            System.out.println("> Bye!");

        } catch (IOException e) {
            System.out.println("Error: No s'ha pogut connectar al servidor.");
        }
    }
}