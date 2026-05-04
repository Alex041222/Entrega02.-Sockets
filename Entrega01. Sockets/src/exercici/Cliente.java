package exercici;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Error: Falta el paràmetre del Port.");
            return;
        }
        
        int port = Integer.parseInt(args[0]);
        
        try (Scanner teclat = new Scanner(System.in)) {
            System.out.print("Introdueix la teva PARAULA CLAU: ");
            String paraulaClau = teclat.nextLine().trim();

            try (Socket socket = new Socket("127.0.0.1", port)) {
                PrintWriter sortida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviem la clau inicial per identificar-nos al servidor
                sortida.println(paraulaClau);

                while (true) {
                    // 1. ENVIAR
                    System.out.print("#Enviar al servidor: ");
                    String msgEnviar = teclat.nextLine();
                    sortida.println(msgEnviar);

                    // Si el client envia la seva pròpia clau, tanca
                    if (msgEnviar.trim().equalsIgnoreCase(paraulaClau)) {
                        break;
                    }

                    // 2. REBRE (Confirmació o resposta)
                    String msgRebut = entrada.readLine();
                    
                    // Si el servidor ens tanca la connexió
                    if (msgRebut == null) break;
                    
                    System.out.println("#Rebut del servidor: " + msgRebut);
                    
                    // SI EL SERVIDOR ENS TORNA LA NOSTRA CLAU (senyal de tancament detectat)
                    if (msgRebut.trim().equalsIgnoreCase(paraulaClau)) {
                        System.out.println("> Client keyword detected!");
                        break;
                    }
                }
                
                // Missatges de comiat finals
                System.out.println("> Closing chat... OK");
                System.out.println("> Closing client... OK");
                System.out.println("> Bye!");

            } catch (IOException e) {
                System.out.println("Error: Connexió perduda o servidor no trobat.");
            }
        }
    }
}