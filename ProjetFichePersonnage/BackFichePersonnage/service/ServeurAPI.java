package service;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Utilisateur;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Serveur HTTP simple pour exposer les endpoints de gestion utilisateur.
 * Utilise le HttpServer integre a Java (pas de dependance externe).
 *
 * Endpoints :
 * POST /api/signup   - Creer un compte  (body: {"nom":"...", "motdepasse":"..."})
 * POST /api/login    - Se connecter      (body: {"nom":"...", "motdepasse":"..."})
 * POST /api/logout   - Se deconnecter
 * GET  /api/utilisateur - Info utilisateur connecte
 *
 * Le serveur ecrit aussi un fichier data/session.json pour le partage avec le frontend.
 */
public class ServeurAPI {

    private HttpServer serveur;
    private GestionUtilisateur gestionUtilisateur;
    private static final int PORT = 8080;

    public ServeurAPI(GestionUtilisateur gestionUtilisateur) {
        this.gestionUtilisateur = gestionUtilisateur;
    }

    public void demarrer() throws IOException {
        serveur = HttpServer.create(new InetSocketAddress(PORT), 0);

        serveur.createContext("/api/signup", new SignupHandler());
        serveur.createContext("/api/login", new LoginHandler());
        serveur.createContext("/api/logout", new LogoutHandler());
        serveur.createContext("/api/utilisateur", new UtilisateurHandler());

        serveur.start();
        System.out.println("Serveur API demarre sur http://localhost:" + PORT);
    }

    public void arreter() {
        if (serveur != null) {
            serveur.stop(0);
            System.out.println("Serveur API arrete.");
        }
    }

    /**
     * Ajoute les headers CORS pour autoriser les requetes depuis le frontend React.
     */
    private void ajouterHeadersCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
    }

    /**
     * Envoie une reponse JSON.
     */
    private void envoyerReponse(HttpExchange exchange, int code, String json) throws IOException {
        ajouterHeadersCORS(exchange);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    /**
     * Lit le body d'une requete POST.
     */
    private String lireBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String ligne;
        while ((ligne = reader.readLine()) != null) {
            sb.append(ligne);
        }
        return sb.toString();
    }

    /**
     * Extraction simple d'une valeur JSON sans bibliotheque externe.
     * Cherche "cle":"valeur" dans le JSON.
     */
    private String extraireValeurJSON(String json, String cle) {
        String recherche = "\"" + cle + "\"";
        int index = json.indexOf(recherche);
        if (index == -1) return null;

        // Avance apres la cle et les ":"
        index = json.indexOf(":", index) + 1;
        // Passe les espaces
        while (index < json.length() && json.charAt(index) == ' ') index++;

        if (json.charAt(index) == '"') {
            // Valeur entre guillemets
            int debut = index + 1;
            int fin = json.indexOf('"', debut);
            return json.substring(debut, fin);
        }
        return null;
    }

    /**
     * Sauvegarde l'etat de la session dans un fichier JSON partage.
     */
    private void sauvegarderSessionJSON() {
        File fichier = new File("data/session.json");
        fichier.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichier))) {
            Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
            if (connecte != null) {
                writer.write("{");
                writer.newLine();
                writer.write("  \"connecte\": true,");
                writer.newLine();
                writer.write("  \"id\": " + connecte.getIdUtilisateur() + ",");
                writer.newLine();
                writer.write("  \"nom\": \"" + connecte.getNomUtilisateur() + "\"");
                writer.newLine();
                writer.write("}");
            } else {
                writer.write("{");
                writer.newLine();
                writer.write("  \"connecte\": false");
                writer.newLine();
                writer.write("}");
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde session JSON : " + e.getMessage());
        }
    }

    // ========== HANDLERS ==========

    /**
     * POST /api/signup
     * Body : {"nom":"...", "motdepasse":"..."}
     */
    class SignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                ajouterHeadersCORS(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                return;
            }

            String body = lireBody(exchange);
            String nom = extraireValeurJSON(body, "nom");
            String motdepasse = extraireValeurJSON(body, "motdepasse");

            if (nom == null || motdepasse == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Nom et mot de passe requis\"}");
                return;
            }

            Utilisateur nouveau = gestionUtilisateur.creerCompte(nom, motdepasse);
            if (nouveau == null) {
                envoyerReponse(exchange, 409, "{\"erreur\":\"Le nom d'utilisateur existe deja\"}");
                return;
            }

            String json = "{\"succes\":true, \"id\":" + nouveau.getIdUtilisateur()
                    + ", \"nom\":\"" + nouveau.getNomUtilisateur() + "\"}";
            envoyerReponse(exchange, 201, json);
        }
    }

    /**
     * POST /api/login
     * Body : {"nom":"...", "motdepasse":"..."}
     */
    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                ajouterHeadersCORS(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                return;
            }

            String body = lireBody(exchange);
            String nom = extraireValeurJSON(body, "nom");
            String motdepasse = extraireValeurJSON(body, "motdepasse");

            if (nom == null || motdepasse == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Nom et mot de passe requis\"}");
                return;
            }

            // Si quelqu'un est deja connecte, on le deconnecte d'abord
            if (gestionUtilisateur.getUtilisateurConnecte() != null) {
                gestionUtilisateur.seDeconnecter();
            }

            Utilisateur utilisateur = gestionUtilisateur.seConnecter(nom, motdepasse);
            if (utilisateur == null) {
                envoyerReponse(exchange, 401, "{\"erreur\":\"Nom ou mot de passe incorrect\"}");
                return;
            }

            sauvegarderSessionJSON();
            String json = "{\"succes\":true, \"id\":" + utilisateur.getIdUtilisateur()
                    + ", \"nom\":\"" + utilisateur.getNomUtilisateur() + "\"}";
            envoyerReponse(exchange, 200, json);
        }
    }

    /**
     * POST /api/logout
     */
    class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                ajouterHeadersCORS(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                return;
            }

            if (gestionUtilisateur.getUtilisateurConnecte() == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Aucun utilisateur connecte\"}");
                return;
            }

            gestionUtilisateur.seDeconnecter();
            sauvegarderSessionJSON();
            envoyerReponse(exchange, 200, "{\"succes\":true}");
        }
    }

    /**
     * GET /api/utilisateur
     * Retourne les infos de l'utilisateur connecte.
     */
    class UtilisateurHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                ajouterHeadersCORS(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                return;
            }

            Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
            if (connecte == null) {
                envoyerReponse(exchange, 200, "{\"connecte\":false}");
                return;
            }

            String json = "{\"connecte\":true, \"id\":" + connecte.getIdUtilisateur()
                    + ", \"nom\":\"" + connecte.getNomUtilisateur() + "\"}";
            envoyerReponse(exchange, 200, json);
        }
    }
}
