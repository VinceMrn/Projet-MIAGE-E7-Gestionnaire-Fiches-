package service.route;

import model.FichePersonnage;
import model.Utilisateur;
import service.GestionFiche;
import service.GestionSession;
import service.JsonUtils;

/**
 * Routes des fiches de personnage :
 * GET/POST /api/fiches
 * GET/DELETE /api/fiches/{id}
 * PUT /api/fiches/{id}/portrait, /biographie
 * POST /api/fiches/{id}/statistiques, /competences, /equipements
 * PUT /api/fiches/{id}/module/position, /module/taille
 */
public class RouteFiches implements Route {

    private GestionFiche gestionFiche;
    private GestionSession gestionSession;

    public RouteFiches(GestionFiche gestionFiche, GestionSession gestionSession) {
        this.gestionFiche = gestionFiche;
        this.gestionSession = gestionSession;
    }

    public boolean correspond(String chemin) {
        return chemin.equals("/api/fiches") || chemin.startsWith("/api/fiches/");
    }

    public String[] traiter(String methode, String chemin, String body, String sessionId) {
        // Verification connexion
        Utilisateur u = gestionSession.getUtilisateurDepuisSession(sessionId);
        if (u == null) {
            return reponse(401, JsonUtils.erreur("Non connecte"));
        }

        // /api/fiches
        if (chemin.equals("/api/fiches")) {
            return traiterListeOuCreation(methode, body, u);
        }

        // /api/fiches/{id}...
        String[] segments = chemin.split("/");
        int idFiche;
        try {
            idFiche = Integer.parseInt(segments[3]);
        } catch (Exception e) {
            return reponse(400, JsonUtils.erreur("ID invalide"));
        }

        // /api/fiches/{id}
        if (segments.length == 4) {
            return traiterFiche(methode, idFiche, u);
        }

        // /api/fiches/{id}/{ressource}
        if (segments.length >= 5) {
            return traiterRessource(methode, body, idFiche, segments, u);
        }

        return reponse(404, JsonUtils.erreur("Route inconnue"));
    }

    private String[] traiterListeOuCreation(String methode, String body, Utilisateur u) {
        if ("GET".equals(methode)) {
            return reponse(200, JsonUtils.listeFichesVersJSON(gestionFiche.listerFiches(u)));
        }
        if ("POST".equals(methode)) {
            String nom = JsonUtils.extraireString(body, "nom");
            if (nom == null || nom.isEmpty()) {
                return reponse(400, JsonUtils.erreur("Nom requis"));
            }
            FichePersonnage f = gestionFiche.creerFiche(u, nom);
            return reponse(201, JsonUtils.succesAvecIdNom(f.getIdFichePersonnage(), f.getNomFichePersonnage()));
        }
        return reponse(405, JsonUtils.erreur("Methode non autorisee"));
    }

    private String[] traiterFiche(String methode, int idFiche, Utilisateur u) {
        if ("GET".equals(methode)) {
            FichePersonnage fiche = gestionFiche.getFiche(u, idFiche);
            if (fiche == null) return reponse(404, JsonUtils.erreur("Fiche non trouvee"));
            return reponse(200, JsonUtils.ficheVersJSON(fiche));
        }
        if ("DELETE".equals(methode)) {
            boolean ok = gestionFiche.supprimerFiche(u, idFiche);
            return ok ? reponse(200, JsonUtils.succes()) : reponse(404, JsonUtils.erreur("Fiche non trouvee"));
        }
        return reponse(405, JsonUtils.erreur("Methode non autorisee"));
    }

    private String[] traiterRessource(String methode, String body, int idFiche, String[] segments, Utilisateur u) {
        String ressource = segments[4];

        switch (ressource) {
            case "portrait":
                if ("PUT".equals(methode)) {
                    gestionFiche.modifierPortrait(idFiche, JsonUtils.extraireString(body, "image"), u);
                    return reponse(200, JsonUtils.succes());
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "biographie":
                if ("PUT".equals(methode)) {
                    gestionFiche.modifierBiographie(idFiche, JsonUtils.extraireString(body, "texte"), u);
                    return reponse(200, JsonUtils.succes());
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "statistiques":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    Integer val = JsonUtils.extraireInt(body, "valeur");
                    if (nom != null && val != null) {
                        gestionFiche.ajouterStatistique(idFiche, nom, val, u);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom et valeur requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "competences":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    if (nom != null) {
                        gestionFiche.ajouterCompetence(idFiche, nom, u);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "equipements":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    if (nom != null) {
                        gestionFiche.ajouterEquipement(idFiche, nom, u);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "module":
                return traiterModule(methode, body, idFiche, segments, u);

            case "rename":
                if ("PUT".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    if (nom == null || nom.isEmpty()) {
                        return reponse(400, JsonUtils.erreur("Nom requis"));
                    }
                    boolean ok = gestionFiche.modifierNomFiche(idFiche, nom, u);
                    return ok ? reponse(200, JsonUtils.succes()) : reponse(404, JsonUtils.erreur("Fiche non trouvee"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "modules-personnalises":
                // POST /api/fiches/{id}/modules-personnalises
                if ("POST".equals(methode)) {
                    String mid = JsonUtils.extraireString(body, "id");
                    String mnom = JsonUtils.extraireString(body, "nom");
                    String mtype = JsonUtils.extraireString(body, "type");
                    if (mnom == null || mnom.isEmpty() || mtype == null || mtype.isEmpty()) {
                        return reponse(400, JsonUtils.erreur("nom et type requis"));
                    }
                    model.ModulePersonnalise mp = new model.ModulePersonnalise(mid != null ? mid : java.util.UUID.randomUUID().toString(), mnom, mtype);
                    String texte = JsonUtils.extraireString(body, "contenuTexte");
                    if (texte != null) mp.setContenuTexte(texte);
                    java.util.List<String> liste = JsonUtils.extraireArrayStrings(body, "contenuListe");
                    if (liste != null) mp.setContenuListe(liste);
                    java.util.List<model.Statistique> stats = JsonUtils.extraireArrayStatistiques(body, "contenuStats");
                    if (stats != null) mp.setContenuStats(stats);
                    gestionFiche.ajouterModulePersonnalise(idFiche, mp, u);
                    return reponse(201, JsonUtils.succes());
                }

                // PUT /api/fiches/{id}/modules-personnalises/{idModule}
                if (segments.length >= 6 && "PUT".equals(methode)) {
                    String idModule = segments[5];
                    String mid = JsonUtils.extraireString(body, "id");
                    String mnom = JsonUtils.extraireString(body, "nom");
                    String mtype = JsonUtils.extraireString(body, "type");
                    if (mnom == null || mnom.isEmpty()) mnom = "Module";
                    model.ModulePersonnalise mp = new model.ModulePersonnalise(mid != null ? mid : idModule, mnom, mtype != null ? mtype : "texte");
                    String texte = JsonUtils.extraireString(body, "contenuTexte");
                    if (texte != null) mp.setContenuTexte(texte);
                    java.util.List<String> liste = JsonUtils.extraireArrayStrings(body, "contenuListe");
                    if (liste != null) mp.setContenuListe(liste);
                    java.util.List<model.Statistique> stats = JsonUtils.extraireArrayStatistiques(body, "contenuStats");
                    if (stats != null) mp.setContenuStats(stats);
                    gestionFiche.modifierModulePersonnalise(idFiche, idModule, mp, u);
                    return reponse(200, JsonUtils.succes());
                }

                // DELETE /api/fiches/{id}/modules-personnalises/{idModule}
                if (segments.length >= 6 && "DELETE".equals(methode)) {
                    String idModule = segments[5];
                    gestionFiche.supprimerModulePersonnalise(idFiche, idModule, u);
                    return reponse(200, JsonUtils.succes());
                }

                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            default:
                return reponse(404, JsonUtils.erreur("Route inconnue"));
        }
    }

    private String[] traiterModule(String methode, String body, int idFiche, String[] segments, Utilisateur u) {
        if (segments.length < 6 || !"PUT".equals(methode)) {
            return reponse(405, JsonUtils.erreur("Methode non autorisee"));
        }

        String action = segments[5];
        String module = JsonUtils.extraireString(body, "module");

        if ("position".equals(action)) {
            Integer x = JsonUtils.extraireInt(body, "posX");
            Integer y = JsonUtils.extraireInt(body, "posY");
            if (module != null && x != null && y != null) {
                boolean ok = gestionFiche.modifierPositionModule(idFiche, module, x, y, u);
                return ok ? reponse(200, JsonUtils.succes()) : reponse(400, JsonUtils.erreur("Module inconnu"));
            }
            return reponse(400, JsonUtils.erreur("module, posX, posY requis"));
        }

        if ("taille".equals(action)) {
            Integer l = JsonUtils.extraireInt(body, "largeur");
            Integer h = JsonUtils.extraireInt(body, "hauteur");
            if (module != null && l != null && h != null) {
                boolean ok = gestionFiche.modifierTailleModule(idFiche, module, l, h, u);
                return ok ? reponse(200, JsonUtils.succes()) : reponse(400, JsonUtils.erreur("Module inconnu"));
            }
            return reponse(400, JsonUtils.erreur("module, largeur, hauteur requis"));
        }

        return reponse(404, JsonUtils.erreur("Route inconnue"));
    }

    private String[] reponse(int code, String json) {
        return new String[]{ String.valueOf(code), json };
    }
}
