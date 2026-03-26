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
            new RouteAuth(gestionUtilisateur, gestionFiche),
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
        try { serverSocket.close(); } catch (IOException e) { /* ignore */ }
        System.out.println("Serveur arrete.");
    }

    // --- Classe interne pour regrouper les infos d'une requête ---
    private static class RequeteHTTP {
        String methode;
        String chemin;
        String body;
    }

    // --- Lire et parser la requête HTTP depuis le socket ---
    private RequeteHTTP lireRequete(BufferedReader in) throws IOException {
        String premiereLigne = in.readLine();
        if (premiereLigne == null) return null;

        String[] parts = premiereLigne.split(" ");
        RequeteHTTP req = new RequeteHTTP();
        req.methode = parts[0];
        req.chemin = parts[1];

        // Lire les headers pour trouver Content-Length
        int contentLength = 0;
        String ligne;
        while ((ligne = in.readLine()) != null && !ligne.isEmpty()) {
            if (ligne.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(ligne.substring(15).trim());
            }
        }

        // Lire le body si présent
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            req.body = new String(buffer);
        } else {
            req.body = "";
        }

        return req;
    }

    // --- Trouver la route et produire [code, json] ---
    private String[] router(RequeteHTTP req) {
        if ("OPTIONS".equals(req.methode)) {
            return new String[]{"204", ""};
        }

        for (Route route : routes) {
            if (route.correspond(req.chemin)) {
                return route.traiter(req.methode, req.chemin, req.body);
            }
        }

        return new String[]{"404", "{\"erreur\":\"Route inconnue\"}"};
    }

    // --- Méthode principale : lire, router, répondre ---
    private void traiterRequete(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream out = client.getOutputStream()) {

            RequeteHTTP req = lireRequete(in);
            if (req == null) return;

            String[] resultat = router(req);
            repondre(out, Integer.parseInt(resultat[0]), resultat[1]);

        } catch (IOException e) {
            System.out.println("Erreur requete : " + e.getMessage());
        }
    }

    // --- Envoie une réponse HTTP brute ---
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