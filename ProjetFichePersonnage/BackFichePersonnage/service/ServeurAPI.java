package service;

import model.Utilisateur;
import model.FichePersonnage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServeurAPI {

    private ServerSocket serverSocket;
    private GestionUtilisateur gestionUtilisateur;
    private GestionFiche gestionFiche;
    private boolean enMarche;

    public ServeurAPI(GestionUtilisateur gestionUtilisateur, GestionFiche gestionFiche) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionFiche = gestionFiche;
    }

    public void demarrer() throws IOException {
        serverSocket = new ServerSocket(8080);
        enMarche = true;
        System.out.println("Serveur API demarre sur http://localhost:8080");

        Thread t = new Thread(() -> {
            while (enMarche) {
                try {
                    traiterRequete(serverSocket.accept());
                } catch (IOException e) {
                    if (enMarche) System.out.println("Erreur : " + e.getMessage());
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void arreter() {
        enMarche = false;
        try { serverSocket.close(); } catch (IOException e) {}
        System.out.println("Serveur API arrete.");
    }

    private void traiterRequete(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream out = client.getOutputStream()) {

            String premiereLigne = in.readLine();
            if (premiereLigne == null) { client.close(); return; }

            String[] parts = premiereLigne.split(" ");
            String methode = parts[0];
            String chemin = parts[1];

            int contentLength = 0;
            String ligne;
            while ((ligne = in.readLine()) != null && !ligne.isEmpty()) {
                if (ligne.startsWith("Content-Length:"))
                    contentLength = Integer.parseInt(ligne.substring(15).trim());
            }

            String body = "";
            if (contentLength > 0) {
                char[] buf = new char[contentLength];
                in.read(buf, 0, contentLength);
                body = new String(buf);
            }

            if ("OPTIONS".equals(methode)) { repondre(out, 204, ""); client.close(); return; }

            String json;
            int code;

            // Routes utilisateur
            switch (chemin) {
                case "/api/signup": {
                    String nom = jsonVal(body, "nom");
                    String mdp = jsonVal(body, "motdepasse");
                    Utilisateur u = (nom != null && mdp != null) ? gestionUtilisateur.creerCompte(nom, mdp) : null;
                    if (u != null) {
                        json = "{\"succes\":true,\"id\":" + u.getIdUtilisateur() + ",\"nom\":\"" + u.getNomUtilisateur() + "\"}";
                        code = 201;
                    } else {
                        json = "{\"erreur\":\"Inscription echouee\"}";
                        code = 400;
                    }
                    repondre(out, code, json); client.close(); return;
                }
                case "/api/login": {
                    String nom = jsonVal(body, "nom");
                    String mdp = jsonVal(body, "motdepasse");
                    if (gestionUtilisateur.getUtilisateurConnecte() != null) gestionUtilisateur.seDeconnecter();
                    Utilisateur u = (nom != null && mdp != null) ? gestionUtilisateur.seConnecter(nom, mdp) : null;
                    if (u != null) {
                        json = "{\"succes\":true,\"id\":" + u.getIdUtilisateur() + ",\"nom\":\"" + u.getNomUtilisateur() + "\"}";
                        code = 200;
                    } else {
                        json = "{\"erreur\":\"Nom ou mot de passe incorrect\"}";
                        code = 401;
                    }
                    repondre(out, code, json); client.close(); return;
                }
                case "/api/logout": {
                    if (gestionUtilisateur.getUtilisateurConnecte() != null) {
                        gestionUtilisateur.seDeconnecter();
                        json = "{\"succes\":true}";
                    } else {
                        json = "{\"erreur\":\"Aucun utilisateur connecte\"}";
                    }
                    repondre(out, 200, json); client.close(); return;
                }
                case "/api/utilisateur": {
                    Utilisateur c = gestionUtilisateur.getUtilisateurConnecte();
                    json = (c != null)
                        ? "{\"connecte\":true,\"id\":" + c.getIdUtilisateur() + ",\"nom\":\"" + c.getNomUtilisateur() + "\"}"
                        : "{\"connecte\":false}";
                    repondre(out, 200, json); client.close(); return;
                }
                case "/api/fiches": {
                    if (gestionUtilisateur.getUtilisateurConnecte() == null) {
                        repondre(out, 401, "{\"erreur\":\"Non connecte\"}"); client.close(); return;
                    }
                    if ("GET".equals(methode)) {
                        json = JsonUtils.listeFichesVersJSON(gestionFiche.listerFiches());
                        code = 200;
                    } else if ("POST".equals(methode)) {
                        String nom = jsonVal(body, "nom");
                        if (nom == null || nom.isEmpty()) {
                            json = "{\"erreur\":\"Nom requis\"}"; code = 400;
                        } else {
                            FichePersonnage f = gestionFiche.creerFiche(nom);
                            json = "{\"succes\":true,\"id\":" + f.getIdFichePersonnage() + ",\"nom\":\"" + f.getNomFichePersonnage() + "\"}";
                            code = 201;
                        }
                    } else {
                        json = "{\"erreur\":\"Methode non autorisee\"}"; code = 405;
                    }
                    repondre(out, code, json); client.close(); return;
                }
            }

            // Routes dynamiques /api/fiches/{id}...
            if (chemin.startsWith("/api/fiches/")) {
                if (gestionUtilisateur.getUtilisateurConnecte() == null) {
                    repondre(out, 401, "{\"erreur\":\"Non connecte\"}"); client.close(); return;
                }

                String[] segments = chemin.split("/");
                int idFiche;
                try {
                    idFiche = Integer.parseInt(segments[3]);
                } catch (Exception e) {
                    repondre(out, 400, "{\"erreur\":\"ID invalide\"}"); client.close(); return;
                }

                FichePersonnage fiche = gestionFiche.getFiche(idFiche);

                // GET/DELETE /api/fiches/{id}
                if (segments.length == 4) {
                    if ("GET".equals(methode)) {
                        if (fiche == null) { repondre(out, 404, "{\"erreur\":\"Fiche non trouvee\"}"); }
                        else { repondre(out, 200, JsonUtils.ficheVersJSON(fiche)); }
                    } else if ("DELETE".equals(methode)) {
                        boolean ok = gestionFiche.supprimerFiche(idFiche);
                        repondre(out, ok ? 200 : 404, ok ? "{\"succes\":true}" : "{\"erreur\":\"Fiche non trouvee\"}");
                    } else {
                        repondre(out, 405, "{\"erreur\":\"Methode non autorisee\"}");
                    }
                    client.close(); return;
                }

                // Routes /api/fiches/{id}/{ressource}
                if (segments.length >= 5) {
                    String ressource = segments[4];
                    switch (ressource) {
                        case "portrait":
                            if ("PUT".equals(methode)) {
                                gestionFiche.modifierPortrait(idFiche, jsonVal(body, "image"));
                                repondre(out, 200, "{\"succes\":true}");
                            } else repondre(out, 405, "{\"erreur\":\"Methode non autorisee\"}");
                            break;
                        case "biographie":
                            if ("PUT".equals(methode)) {
                                gestionFiche.modifierBiographie(idFiche, jsonVal(body, "texte"));
                                repondre(out, 200, "{\"succes\":true}");
                            } else repondre(out, 405, "{\"erreur\":\"Methode non autorisee\"}");
                            break;
                        case "statistiques":
                            if ("POST".equals(methode)) {
                                String nom = jsonVal(body, "nom");
                                Integer val = jsonInt(body, "valeur");
                                if (nom != null && val != null) {
                                    gestionFiche.ajouterStatistique(idFiche, nom, val);
                                    repondre(out, 201, "{\"succes\":true}");
                                } else repondre(out, 400, "{\"erreur\":\"nom et valeur requis\"}");
                            } else repondre(out, 405, "{\"erreur\":\"Methode non autorisee\"}");
                            break;
                        case "competences":
                            if ("POST".equals(methode)) {
                                String nom = jsonVal(body, "nom");
                                if (nom != null) {
                                    gestionFiche.ajouterCompetence(idFiche, nom);
                                    repondre(out, 201, "{\"succes\":true}");
                                } else repondre(out, 400, "{\"erreur\":\"nom requis\"}");
                            } else repondre(out, 405, "{\"erreur\":\"Methode non autorisee\"}");
                            break;
                        case "equipements":
                            if ("POST".equals(methode)) {
                                String nom = jsonVal(body, "nom");
                                if (nom != null) {
                                    gestionFiche.ajouterEquipement(idFiche, nom);
                                    repondre(out, 201, "{\"succes\":true}");
                                } else repondre(out, 400, "{\"erreur\":\"nom requis\"}");
                            } else repondre(out, 405, "{\"erreur\":\"Methode non autorisee\"}");
                            break;
                        case "module":
                            if (segments.length >= 6 && "PUT".equals(methode)) {
                                String action = segments[5];
                                String module = jsonVal(body, "module");
                                if ("position".equals(action)) {
                                    Integer x = jsonInt(body, "posX"), y = jsonInt(body, "posY");
                                    if (module != null && x != null && y != null) {
                                        boolean ok = gestionFiche.modifierPositionModule(idFiche, module, x, y);
                                        repondre(out, ok ? 200 : 400, ok ? "{\"succes\":true}" : "{\"erreur\":\"Module inconnu\"}");
                                    } else repondre(out, 400, "{\"erreur\":\"module, posX, posY requis\"}");
                                } else if ("taille".equals(action)) {
                                    Integer l = jsonInt(body, "largeur"), h = jsonInt(body, "hauteur");
                                    if (module != null && l != null && h != null) {
                                        boolean ok = gestionFiche.modifierTailleModule(idFiche, module, l, h);
                                        repondre(out, ok ? 200 : 400, ok ? "{\"succes\":true}" : "{\"erreur\":\"Module inconnu\"}");
                                    } else repondre(out, 400, "{\"erreur\":\"module, largeur, hauteur requis\"}");
                                } else repondre(out, 404, "{\"erreur\":\"Route inconnue\"}");
                            } else repondre(out, 405, "{\"erreur\":\"Methode non autorisee\"}");
                            break;
                        default:
                            repondre(out, 404, "{\"erreur\":\"Route inconnue\"}");
                    }
                    client.close(); return;
                }
            }

            repondre(out, 404, "{\"erreur\":\"Route inconnue\"}");
            client.close();
        } catch (IOException e) {
            System.out.println("Erreur requete : " + e.getMessage());
        }
    }

    private void repondre(OutputStream out, int code, String json) throws IOException {
        String r = "HTTP/1.1 " + code + " OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n"
                + "Access-Control-Allow-Headers: Content-Type\r\n"
                + "Content-Length: " + json.getBytes("UTF-8").length + "\r\n"
                + "\r\n"
                + json;
        out.write(r.getBytes("UTF-8"));
        out.flush();
    }

    private String jsonVal(String json, String cle) {
        int i = json.indexOf("\"" + cle + "\"");
        if (i == -1) return null;
        i = json.indexOf(":", i) + 1;
        while (i < json.length() && json.charAt(i) == ' ') i++;
        if (i >= json.length() || json.charAt(i) != '"') return null;
        int fin = json.indexOf('"', i + 1);
        return (fin == -1) ? null : json.substring(i + 1, fin);
    }

    private Integer jsonInt(String json, String cle) {
        int i = json.indexOf("\"" + cle + "\"");
        if (i == -1) return null;
        i = json.indexOf(":", i) + 1;
        while (i < json.length() && json.charAt(i) == ' ') i++;
        StringBuilder sb = new StringBuilder();
        while (i < json.length() && (Character.isDigit(json.charAt(i)) || json.charAt(i) == '-')) {
            sb.append(json.charAt(i++));
        }
        return sb.length() == 0 ? null : Integer.parseInt(sb.toString());
    }
}
