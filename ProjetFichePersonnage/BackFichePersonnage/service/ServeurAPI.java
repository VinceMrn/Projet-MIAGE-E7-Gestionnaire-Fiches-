package service;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Serveur HTTP simple pour exposer les endpoints de gestion utilisateur et fiches.
 * Utilise le HttpServer integre a Java (pas de dependance externe).
 *
 * Endpoints Utilisateur :
 * POST /api/signup      - Creer un compte
 * POST /api/login       - Se connecter
 * POST /api/logout      - Se deconnecter
 * GET  /api/utilisateur - Info utilisateur connecte
 *
 * Endpoints Fiches :
 * GET    /api/fiches           - Liste des fiches de l'utilisateur
 * POST   /api/fiches           - Creer une nouvelle fiche
 * GET    /api/fiches/{id}      - Detail d'une fiche
 * DELETE /api/fiches/{id}      - Supprimer une fiche
 * PUT    /api/fiches/{id}/portrait    - Modifier le portrait
 * PUT    /api/fiches/{id}/biographie  - Modifier la biographie
 * POST   /api/fiches/{id}/statistiques     - Ajouter une statistique
 * POST   /api/fiches/{id}/competences      - Ajouter une competence
 * POST   /api/fiches/{id}/equipements      - Ajouter un equipement
 * PUT    /api/fiches/{id}/module/position  - Modifier position d'un module
 */
public class ServeurAPI {

    private HttpServer serveur;
    private GestionUtilisateur gestionUtilisateur;
    private GestionFiche gestionFiche;
    private static final int PORT = 8080;

    public ServeurAPI(GestionUtilisateur gestionUtilisateur, GestionFiche gestionFiche) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionFiche = gestionFiche;
    }

    public void demarrer() throws IOException {
        serveur = HttpServer.create(new InetSocketAddress(PORT), 0);

        serveur.createContext("/api/signup", new SignupHandler());
        serveur.createContext("/api/login", new LoginHandler());
        serveur.createContext("/api/logout", new LogoutHandler());
        serveur.createContext("/api/utilisateur", new UtilisateurHandler());
        serveur.createContext("/api/fiches", new FichesHandler());

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
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
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
     * Extraction d'une valeur numerique entiere du JSON.
     */
    private Integer extraireValeurIntJSON(String json, String cle) {
        String recherche = "\"" + cle + "\"";
        int index = json.indexOf(recherche);
        if (index == -1) return null;

        index = json.indexOf(":", index) + 1;
        while (index < json.length() && json.charAt(index) == ' ') index++;

        StringBuilder sb = new StringBuilder();
        while (index < json.length() && (Character.isDigit(json.charAt(index)) || json.charAt(index) == '-')) {
            sb.append(json.charAt(index));
            index++;
        }
        if (sb.length() == 0) return null;
        return Integer.parseInt(sb.toString());
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

    // ========== HANDLER FICHES ==========

    /**
     * Gere toutes les requetes sur /api/fiches et /api/fiches/{id}/...
     */
    class FichesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                ajouterHeadersCORS(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            // Verification connexion
            if (gestionUtilisateur.getUtilisateurConnecte() == null) {
                envoyerReponse(exchange, 401, "{\"erreur\":\"Vous devez etre connecte\"}");
                return;
            }

            // GET /api/fiches - Liste des fiches
            if (path.equals("/api/fiches") && "GET".equals(method)) {
                listerFiches(exchange);
                return;
            }

            // POST /api/fiches - Creer une fiche
            if (path.equals("/api/fiches") && "POST".equals(method)) {
                creerFiche(exchange);
                return;
            }

            // Extraction de l'ID depuis /api/fiches/{id}...
            String[] segments = path.split("/");
            if (segments.length < 4) {
                envoyerReponse(exchange, 404, "{\"erreur\":\"Endpoint non trouve\"}");
                return;
            }

            int idFiche;
            try {
                idFiche = Integer.parseInt(segments[3]);
            } catch (NumberFormatException e) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"ID de fiche invalide\"}");
                return;
            }

            // GET /api/fiches/{id}
            if (segments.length == 4 && "GET".equals(method)) {
                detailFiche(exchange, idFiche);
                return;
            }

            // DELETE /api/fiches/{id}
            if (segments.length == 4 && "DELETE".equals(method)) {
                supprimerFiche(exchange, idFiche);
                return;
            }

            // Routes /api/fiches/{id}/{ressource}
            if (segments.length >= 5) {
                String ressource = segments[4];
                switch (ressource) {
                    case "portrait":
                        if ("PUT".equals(method)) modifierPortrait(exchange, idFiche);
                        else envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                        return;
                    case "biographie":
                        if ("PUT".equals(method)) modifierBiographie(exchange, idFiche);
                        else envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                        return;
                    case "statistiques":
                        if ("POST".equals(method)) ajouterStatistique(exchange, idFiche);
                        else envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                        return;
                    case "competences":
                        if ("POST".equals(method)) ajouterCompetence(exchange, idFiche);
                        else envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                        return;
                    case "equipements":
                        if ("POST".equals(method)) ajouterEquipement(exchange, idFiche);
                        else envoyerReponse(exchange, 405, "{\"erreur\":\"Methode non autorisee\"}");
                        return;
                    case "module":
                        if (segments.length >= 6 && "position".equals(segments[5]) && "PUT".equals(method)) {
                            modifierPositionModule(exchange, idFiche);
                        } else if (segments.length >= 6 && "taille".equals(segments[5]) && "PUT".equals(method)) {
                            modifierTailleModule(exchange, idFiche);
                        } else {
                            envoyerReponse(exchange, 404, "{\"erreur\":\"Endpoint non trouve\"}");
                        }
                        return;
                }
            }
            envoyerReponse(exchange, 404, "{\"erreur\":\"Endpoint non trouve\"}");
        }

        private void listerFiches(HttpExchange exchange) throws IOException {
            List<FichePersonnage> fiches = gestionFiche.listerFiches();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < fiches.size(); i++) {
                FichePersonnage f = fiches.get(i);
                json.append("{\"id\":").append(f.getIdFichePersonnage())
                    .append(",\"nom\":\"").append(f.getNomFichePersonnage()).append("\"}");
                if (i < fiches.size() - 1) json.append(",");
            }
            json.append("]");
            envoyerReponse(exchange, 200, json.toString());
        }

        private void creerFiche(HttpExchange exchange) throws IOException {
            String body = lireBody(exchange);
            String nom = extraireValeurJSON(body, "nom");
            if (nom == null || nom.isEmpty()) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Nom de fiche requis\"}");
                return;
            }
            FichePersonnage fiche = gestionFiche.creerFiche(nom);
            String json = "{\"succes\":true,\"id\":" + fiche.getIdFichePersonnage()
                    + ",\"nom\":\"" + fiche.getNomFichePersonnage() + "\"}";
            envoyerReponse(exchange, 201, json);
        }

        private void detailFiche(HttpExchange exchange, int idFiche) throws IOException {
            FichePersonnage fiche = gestionFiche.getFiche(idFiche);
            if (fiche == null) {
                envoyerReponse(exchange, 404, "{\"erreur\":\"Fiche non trouvee\"}");
                return;
            }
            envoyerReponse(exchange, 200, ficheVersJSON(fiche));
        }

        private void supprimerFiche(HttpExchange exchange, int idFiche) throws IOException {
            boolean ok = gestionFiche.supprimerFiche(idFiche);
            if (!ok) {
                envoyerReponse(exchange, 404, "{\"erreur\":\"Fiche non trouvee\"}");
                return;
            }
            envoyerReponse(exchange, 200, "{\"succes\":true}");
        }

        private void modifierPortrait(HttpExchange exchange, int idFiche) throws IOException {
            String body = lireBody(exchange);
            String image = extraireValeurJSON(body, "image");
            gestionFiche.modifierPortrait(idFiche, image != null ? image : "");
            envoyerReponse(exchange, 200, "{\"succes\":true}");
        }

        private void modifierBiographie(HttpExchange exchange, int idFiche) throws IOException {
            String body = lireBody(exchange);
            String texte = extraireValeurJSON(body, "texte");
            gestionFiche.modifierBiographie(idFiche, texte != null ? texte : "");
            envoyerReponse(exchange, 200, "{\"succes\":true}");
        }

        private void ajouterStatistique(HttpExchange exchange, int idFiche) throws IOException {
            String body = lireBody(exchange);
            String nom = extraireValeurJSON(body, "nom");
            Integer valeur = extraireValeurIntJSON(body, "valeur");
            if (nom == null || valeur == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Nom et valeur requis\"}");
                return;
            }
            gestionFiche.ajouterStatistique(idFiche, nom, valeur);
            envoyerReponse(exchange, 201, "{\"succes\":true}");
        }

        private void ajouterCompetence(HttpExchange exchange, int idFiche) throws IOException {
            String body = lireBody(exchange);
            String nom = extraireValeurJSON(body, "nom");
            if (nom == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Nom requis\"}");
                return;
            }
            gestionFiche.ajouterCompetence(idFiche, nom);
            envoyerReponse(exchange, 201, "{\"succes\":true}");
        }

        private void ajouterEquipement(HttpExchange exchange, int idFiche) throws IOException {
            String body = lireBody(exchange);
            String nom = extraireValeurJSON(body, "nom");
            if (nom == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Nom requis\"}");
                return;
            }
            gestionFiche.ajouterEquipement(idFiche, nom);
            envoyerReponse(exchange, 201, "{\"succes\":true}");
        }

        private void modifierPositionModule(HttpExchange exchange, int idFiche) throws IOException {
            String body = lireBody(exchange);
            String module = extraireValeurJSON(body, "module");
            Integer posX = extraireValeurIntJSON(body, "posX");
            Integer posY = extraireValeurIntJSON(body, "posY");
            if (module == null || posX == null || posY == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"module, posX et posY requis\"}");
                return;
            }
            boolean ok = gestionFiche.modifierPositionModule(idFiche, module, posX, posY);
            if (!ok) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Module inconnu\"}");
                return;
            }
            envoyerReponse(exchange, 200, "{\"succes\":true}");
        }

        private void modifierTailleModule(HttpExchange exchange, int idFiche) throws IOException {
            String body = lireBody(exchange);
            String module = extraireValeurJSON(body, "module");
            Integer largeur = extraireValeurIntJSON(body, "largeur");
            Integer hauteur = extraireValeurIntJSON(body, "hauteur");
            if (module == null || largeur == null || hauteur == null) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"module, largeur et hauteur requis\"}");
                return;
            }
            boolean ok = gestionFiche.modifierTailleModule(idFiche, module, largeur, hauteur);
            if (!ok) {
                envoyerReponse(exchange, 400, "{\"erreur\":\"Module inconnu\"}");
                return;
            }
            envoyerReponse(exchange, 200, "{\"succes\":true}");
        }

        private String ficheVersJSON(FichePersonnage fiche) {
            StringBuilder json = new StringBuilder();
            json.append("{\"id\":").append(fiche.getIdFichePersonnage());
            json.append(",\"nom\":\"").append(fiche.getNomFichePersonnage()).append("\"");

            Portrait p = fiche.getPortrait();
            json.append(",\"portrait\":{\"image\":\"").append(p.getImagePortrait()).append("\"")
                .append(",\"posX\":").append(p.getPositionX())
                .append(",\"posY\":").append(p.getPositionY())
                .append(",\"largeur\":").append(p.getLargeur())
                .append(",\"hauteur\":").append(p.getHauteur()).append("}");

            Biographie b = fiche.getBiographie();
            json.append(",\"biographie\":{\"texte\":\"").append(b.getTexteBiographie()).append("\"")
                .append(",\"posX\":").append(b.getPositionX())
                .append(",\"posY\":").append(b.getPositionY())
                .append(",\"largeur\":").append(b.getLargeur())
                .append(",\"hauteur\":").append(b.getHauteur()).append("}");

            Statistiques stats = fiche.getStatistiques();
            json.append(",\"statistiques\":{\"posX\":").append(stats.getPositionX())
                .append(",\"posY\":").append(stats.getPositionY())
                .append(",\"largeur\":").append(stats.getLargeur())
                .append(",\"hauteur\":").append(stats.getHauteur())
                .append(",\"liste\":[");
            List<Statistique> listeStats = stats.getStatistiques();
            for (int i = 0; i < listeStats.size(); i++) {
                Statistique s = listeStats.get(i);
                json.append("{\"id\":").append(s.getIdStatistique())
                    .append(",\"nom\":\"").append(s.getNomStatistique()).append("\"")
                    .append(",\"valeur\":").append(s.getValeurStatistique()).append("}");
                if (i < listeStats.size() - 1) json.append(",");
            }
            json.append("]}");

            Competence comp = fiche.getCompetence();
            json.append(",\"competences\":{\"posX\":").append(comp.getPositionX())
                .append(",\"posY\":").append(comp.getPositionY())
                .append(",\"largeur\":").append(comp.getLargeur())
                .append(",\"hauteur\":").append(comp.getHauteur())
                .append(",\"liste\":[");
            List<String> listeComp = comp.getCompetences();
            for (int i = 0; i < listeComp.size(); i++) {
                json.append("\"").append(listeComp.get(i)).append("\"");
                if (i < listeComp.size() - 1) json.append(",");
            }
            json.append("]}");

            Equipement equip = fiche.getEquipement();
            json.append(",\"equipements\":{\"posX\":").append(equip.getPositionX())
                .append(",\"posY\":").append(equip.getPositionY())
                .append(",\"largeur\":").append(equip.getLargeur())
                .append(",\"hauteur\":").append(equip.getHauteur())
                .append(",\"liste\":[");
            List<String> listeEquip = equip.getEquipements();
            for (int i = 0; i < listeEquip.size(); i++) {
                json.append("\"").append(listeEquip.get(i)).append("\"");
                if (i < listeEquip.size() - 1) json.append(",");
            }
            json.append("]}");

            json.append("}");
            return json.toString();
        }
    }
}
