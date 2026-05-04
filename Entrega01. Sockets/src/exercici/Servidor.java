package exercici;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    private List<GestorCliente> clientesActivos = new CopyOnWriteArrayList<>();
    private boolean algunConnectat = false;
    private boolean servidorVivo = true;
    
    private final BufferedReader teclat = new BufferedReader(new InputStreamReader(System.in));
    private final Object scannerLock = new Object();

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Error: arguments (Port, ParaulaClauServidor, MaxClients)");
            return;
        }
        new Servidor().iniciar(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]));
    }

    public void iniciar(int port, String paraulaClau, int max) {
        try (ServerSocket servidorSocket = new ServerSocket(port)) {
            System.out.println("PORT_SERVIDOR: " + port);
            System.out.println("PARAULA_CLAU_SERVIDOR: \"" + paraulaClau + "\"");
            System.out.println("> Server chat at port " + port);
            System.out.println("> Inicializing server... OK");
            
            servidorSocket.setSoTimeout(1000);

            while (servidorVivo) {
                try {
                    if (clientesActivos.size() < max) {
                        Socket s = servidorSocket.accept();
                        algunConnectat = true;
                        int id = clientesActivos.size() + 1;
                        System.out.println("> Connection from client " + id + " ... OK");
                        
                        GestorCliente gc = new GestorCliente(s, paraulaClau, id, this);
                        clientesActivos.add(gc);
                        new Thread(gc).start();
                    }
                } catch (SocketTimeoutException e) {
                    if (algunConnectat && clientesActivos.isEmpty()) {
                        servidorVivo = false;
                    }
                }
            }
        } catch (IOException e) {
            if (servidorVivo) System.out.println("Error: " + e.getMessage());
        }
        System.out.println("> Bye!");
    }

    public BufferedReader getTeclat() { return teclat; }
    public Object getScannerLock() { return scannerLock; }
    public void eliminarCliente(GestorCliente g) { clientesActivos.remove(g); }

    public void tancarTot() {
        // Aquests missatges apareixeran a la consola del servidor
        System.out.println("\n> Closing chat... OK");
        for (GestorCliente g : clientesActivos) {
            g.forzarCierre();
        }
        System.out.println("> Closing server... OK");
        System.out.println("> Bye!");
        System.exit(0); // Tanca tot el programa immediatament
    }
}