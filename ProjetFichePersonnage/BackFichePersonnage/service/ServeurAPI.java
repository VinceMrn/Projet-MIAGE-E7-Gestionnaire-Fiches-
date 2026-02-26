package service;

import service.route.Route;
import service.route.RouteAuth;
import service.route.RouteFiches;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurAPI {

    private ServerSocket serverSocket;
    private boolean enMarche;
    private Route[] routes;

    public ServeurAPI(GestionUtilisateur gestionUtilisateur, GestionFiche gestionFiche) {
        this.routes = new Route[]{
            new RouteAuth(gestionUtilisateur),
            new RouteFiches(gestionUtilisateur, gestionFiche)
        };
    }

    public void demarrer() throws IOException {
        serverSocket = new ServerSocket(8080);
        enMarche = true;
        System.out.println("Serveur demarre sur http://localhost:8080");

        Thread thread = new Thread(() -> {
            while (enMarche) {
                try {
                    Socket client = serverSocket.accept();
                    traiterRequete(client);
                } catch (IOException e) {
                    if (enMarche) System.out.println("Erreur : " + e.getMessage());
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void arreter() {
        enMarche = false;
        try { serverSocket.close(); } catch (IOException e) {}
        System.out.println("Serveur arrete.");
    }

    private void traiterRequete(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream out = client.getOutputStream()) {

            // Lire la premiere ligne : "GET /api/fiches HTTP/1.1"
            String premiereLigne = in.readLine();
            if (premiereLigne == null) { client.close(); return; }

            String[] parts = premiereLigne.split(" ");
            String methode = parts[0];
            String chemin = parts[1];

            // Lire les headers pour trouver Content-Length
            int contentLength = 0;
            String ligne;
            while ((ligne = in.readLine()) != null && !ligne.isEmpty()) {
                if (ligne.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(ligne.substring(15).trim());
                }
            }

            // Lire le body si present
            String body = "";
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                in.read(buffer, 0, contentLength);
                body = new String(buffer);
            }

            // Requete preflight CORS (le navigateur envoie OPTIONS avant chaque requete)
            if ("OPTIONS".equals(methode)) {
                repondre(out, 204, "");
                client.close();
                return;
            }

            // Chercher quelle route correspond au chemin
            for (Route route : routes) {
                if (route.correspond(chemin)) {
                    String[] resultat = route.traiter(methode, chemin, body);
                    repondre(out, Integer.parseInt(resultat[0]), resultat[1]);
                    client.close();
                    return;
                }
            }

            // Aucune route trouvee
            repondre(out, 404, "{\"erreur\":\"Route inconnue\"}");
            client.close();

        } catch (IOException e) {
            System.out.println("Erreur requete : " + e.getMessage());
        }
    }

    // Envoie une reponse HTTP brute sur le socket
    private void repondre(OutputStream out, int code, String json) throws IOException {
        byte[] contenu = json.getBytes("UTF-8");
        String entete = "HTTP/1.1 " + code + " OK\r\n"
            + "Content-Type: application/json\r\n"
            + "Access-Control-Allow-Origin: *\r\n"
            + "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n"
            + "Access-Control-Allow-Headers: Content-Type\r\n"
            + "Content-Length: " + contenu.length + "\r\n"
            + "\r\n";
        out.write(entete.getBytes("UTF-8"));
        out.write(contenu);
        out.flush();
    }
}
