package service.route;

import model.FichePersonnage;
import service.GestionFiche;
import service.GestionUtilisateur;
import service.JsonUtils;

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
        if (gestionUtilisateur.getUtilisateurConnecte() == null) {
            return r(401, JsonUtils.erreur("Non connecte"));
        }

        String[] s = chemin.split("/"); // ["", "api", "fiches", ...]

        // GET /api/fiches → liste
        if (chemin.equals("/api/fiches") && "GET".equals(methode)) {
            return r(200, JsonUtils.listeFichesVersJSON(gestionFiche.listerFiches()));
        }

        // POST /api/fiches → creer
        if (chemin.equals("/api/fiches") && "POST".equals(methode)) {
            String nom = JsonUtils.extraireString(body, "nom");
            FichePersonnage f = gestionFiche.creerFiche(nom);
            return r(201, JsonUtils.succesAvecIdNom(f.getIdFichePersonnage(), f.getNomFichePersonnage()));
        }

        // A partir d'ici on a besoin d'un ID : /api/fiches/{id}/...
        if (s.length < 4) return r(404, JsonUtils.erreur("Route inconnue"));
        int id = Integer.parseInt(s[3]);

        // GET /api/fiches/{id}
        if (s.length == 4 && "GET".equals(methode)) {
            FichePersonnage f = gestionFiche.getFiche(id);
            return f != null ? r(200, JsonUtils.ficheVersJSON(f)) : r(404, JsonUtils.erreur("Fiche non trouvee"));
        }

        // DELETE /api/fiches/{id}
        if (s.length == 4 && "DELETE".equals(methode)) {
            return gestionFiche.supprimerFiche(id) ? r(200, JsonUtils.succes()) : r(404, JsonUtils.erreur("Fiche non trouvee"));
        }

        if (s.length < 5) return r(404, JsonUtils.erreur("Route inconnue"));
        String ressource = s[4];

        // PUT /api/fiches/{id}/portrait
        if ("portrait".equals(ressource) && "PUT".equals(methode)) {
            gestionFiche.modifierPortrait(id, JsonUtils.extraireString(body, "image"));
            return r(200, JsonUtils.succes());
        }

        // PUT /api/fiches/{id}/biographie
        if ("biographie".equals(ressource) && "PUT".equals(methode)) {
            gestionFiche.modifierBiographie(id, JsonUtils.extraireString(body, "texte"));
            return r(200, JsonUtils.succes());
        }

        // POST /api/fiches/{id}/statistiques
        if ("statistiques".equals(ressource) && "POST".equals(methode)) {
            gestionFiche.ajouterStatistique(id, JsonUtils.extraireString(body, "nom"), JsonUtils.extraireInt(body, "valeur"));
            return r(201, JsonUtils.succes());
        }

        // POST /api/fiches/{id}/competences
        if ("competences".equals(ressource) && "POST".equals(methode)) {
            gestionFiche.ajouterCompetence(id, JsonUtils.extraireString(body, "nom"));
            return r(201, JsonUtils.succes());
        }

        // POST /api/fiches/{id}/equipements
        if ("equipements".equals(ressource) && "POST".equals(methode)) {
            gestionFiche.ajouterEquipement(id, JsonUtils.extraireString(body, "nom"));
            return r(201, JsonUtils.succes());
        }

        // PUT /api/fiches/{id}/module/position
        if ("module".equals(ressource) && s.length >= 6 && "PUT".equals(methode)) {
            String module = JsonUtils.extraireString(body, "module");
            if ("position".equals(s[5])) {
                gestionFiche.modifierPositionModule(id, module, JsonUtils.extraireInt(body, "posX"), JsonUtils.extraireInt(body, "posY"));
                return r(200, JsonUtils.succes());
            }
            if ("taille".equals(s[5])) {
                gestionFiche.modifierTailleModule(id, module, JsonUtils.extraireInt(body, "largeur"), JsonUtils.extraireInt(body, "hauteur"));
                return r(200, JsonUtils.succes());
            }
        }

        return r(404, JsonUtils.erreur("Route inconnue"));
    }

    private String[] r(int code, String json) {
        return new String[]{ String.valueOf(code), json };
    }
}
