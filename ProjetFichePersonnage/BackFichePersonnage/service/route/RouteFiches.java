package service.route;

import model.FichePersonnage;
import service.GestionFiche;
import service.GestionUtilisateur;
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

    private GestionUtilisateur gestionUtilisateur;
    private GestionFiche gestionFiche;

    public RouteFiches(GestionUtilisateur gestionUtilisateur, GestionFiche gestionFiche) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionFiche = gestionFiche;
    }

    public boolean correspond(String chemin) {
        return chemin.equals("/api/fiches") || chemin.startsWith("/api/fiches/");
    }

    public String[] traiter(String methode, String chemin, String body) {
        // Verification connexion
        if (gestionUtilisateur.getUtilisateurConnecte() == null) {
            return reponse(401, JsonUtils.erreur("Non connecte"));
        }

        // /api/fiches
        if (chemin.equals("/api/fiches")) {
            return traiterListeOuCreation(methode, body);
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
            return traiterFiche(methode, idFiche);
        }

        // /api/fiches/{id}/{ressource}
        if (segments.length >= 5) {
            return traiterRessource(methode, body, idFiche, segments);
        }

        return reponse(404, JsonUtils.erreur("Route inconnue"));
    }

    private String[] traiterListeOuCreation(String methode, String body) {
        if ("GET".equals(methode)) {
            return reponse(200, JsonUtils.listeFichesVersJSON(gestionFiche.listerFiches()));
        }
        if ("POST".equals(methode)) {
            String nom = JsonUtils.extraireString(body, "nom");
            if (nom == null || nom.isEmpty()) {
                return reponse(400, JsonUtils.erreur("Nom requis"));
            }
            FichePersonnage f = gestionFiche.creerFiche(nom);
            return reponse(201, JsonUtils.succesAvecIdNom(f.getIdFichePersonnage(), f.getNomFichePersonnage()));
        }
        return reponse(405, JsonUtils.erreur("Methode non autorisee"));
    }

    private String[] traiterFiche(String methode, int idFiche) {
        if ("GET".equals(methode)) {
            FichePersonnage fiche = gestionFiche.getFiche(idFiche);
            if (fiche == null) return reponse(404, JsonUtils.erreur("Fiche non trouvee"));
            return reponse(200, JsonUtils.ficheVersJSON(fiche));
        }
        if ("DELETE".equals(methode)) {
            boolean ok = gestionFiche.supprimerFiche(idFiche);
            return ok ? reponse(200, JsonUtils.succes()) : reponse(404, JsonUtils.erreur("Fiche non trouvee"));
        }
        return reponse(405, JsonUtils.erreur("Methode non autorisee"));
    }

    private String[] traiterRessource(String methode, String body, int idFiche, String[] segments) {
        String ressource = segments[4];

        switch (ressource) {
            case "portrait":
                if ("PUT".equals(methode)) {
                    gestionFiche.modifierPortrait(idFiche, JsonUtils.extraireString(body, "image"));
                    return reponse(200, JsonUtils.succes());
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "biographie":
                if ("PUT".equals(methode)) {
                    gestionFiche.modifierBiographie(idFiche, JsonUtils.extraireString(body, "texte"));
                    return reponse(200, JsonUtils.succes());
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "statistiques":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    Integer val = JsonUtils.extraireInt(body, "valeur");
                    if (nom != null && val != null) {
                        gestionFiche.ajouterStatistique(idFiche, nom, val);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom et valeur requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "competences":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    if (nom != null) {
                        gestionFiche.ajouterCompetence(idFiche, nom);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "equipements":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    if (nom != null) {
                        gestionFiche.ajouterEquipement(idFiche, nom);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "module":
                return traiterModule(methode, body, idFiche, segments);

            default:
                return reponse(404, JsonUtils.erreur("Route inconnue"));
        }
    }

    private String[] traiterModule(String methode, String body, int idFiche, String[] segments) {
        if (segments.length < 6 || !"PUT".equals(methode)) {
            return reponse(405, JsonUtils.erreur("Methode non autorisee"));
        }

        String action = segments[5];
        String module = JsonUtils.extraireString(body, "module");

        if ("position".equals(action)) {
            Integer x = JsonUtils.extraireInt(body, "posX");
            Integer y = JsonUtils.extraireInt(body, "posY");
            if (module != null && x != null && y != null) {
                boolean ok = gestionFiche.modifierPositionModule(idFiche, module, x, y);
                return ok ? reponse(200, JsonUtils.succes()) : reponse(400, JsonUtils.erreur("Module inconnu"));
            }
            return reponse(400, JsonUtils.erreur("module, posX, posY requis"));
        }

        if ("taille".equals(action)) {
            Integer l = JsonUtils.extraireInt(body, "largeur");
            Integer h = JsonUtils.extraireInt(body, "hauteur");
            if (module != null && l != null && h != null) {
                boolean ok = gestionFiche.modifierTailleModule(idFiche, module, l, h);
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
