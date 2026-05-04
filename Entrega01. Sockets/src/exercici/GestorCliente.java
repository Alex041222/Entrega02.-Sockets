package exercici;

import java.io.*;
import java.net.*;

public class GestorCliente implements Runnable {
    private Socket socket;
    private String paraulaClauServidor; // Aquesta és "Maria"
    private String paraulaClauClient;   // Aquesta és "abc"
    private int idClient;
    private Servidor servidorPare;

    public GestorCliente(Socket socket, String paraulaClauServidor, int idClient, Servidor servidorPare) {
        this.socket = socket;
        this.paraulaClauServidor = paraulaClauServidor;
        this.idClient = idClient;
        this.servidorPare = servidorPare;
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter sortida = new PrintWriter(socket.getOutputStream(), true)) {

            // Identificació inicial
            this.paraulaClauClient = entrada.readLine();
            System.out.println("> Chat inicialitzat amb Client " + idClient + " [Clau: " + paraulaClauClient + "]");

            while (true) {
                String msgRebut = entrada.readLine();
                if (msgRebut == null) break;

                // --- COMPROVACIÓ IMMEDIATA ---
                // Si el client envia la paraula del servidor ("Maria"), tanquem IMMEDIATAMENT
                if (msgRebut.trim().equalsIgnoreCase(paraulaClauServidor)) {
                    System.out.println("\n#Rebut del client " + idClient + ": " + msgRebut);
                    System.out.println("> Client keyword detected!");
                    
                    // IMPORTANT: Enviem un missatge al client perquè ell sàpiga que ha de tancar
                    // Li enviem la paraula clau del propi client perquè la seva condició de tancament l'atapi
                    sortida.println(paraulaClauClient); 
                    
                    servidorPare.tancarTot(); 
                    return; 
                }

                // Si NO és la paraula clau, continuem el xat normal
                synchronized (servidorPare.getScannerLock()) {
                    System.out.println("\n#Rebut del client " + idClient + ": " + msgRebut);
                    System.out.print("#Enviar al client " + idClient + ": ");
                    
                    String msgEnviar = servidorPare.getTeclat().readLine();
                    if (msgEnviar == null) break;
                    
                    sortida.println(msgEnviar);
                }
            }
        } catch (IOException e) {
            // Socket tancat
        } finally {
            servidorPare.eliminarCliente(this);
            forzarCierre();
        }
    }

    public void forzarCierre() {
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException e) {}
    }
}