package service.route;

import model.Utilisateur;
import service.GestionUtilisateur;
import service.JsonUtils;

/**
 * Routes d'authentification :
 * POST /api/signup, POST /api/login, POST /api/logout, GET /api/utilisateur
 */
public class RouteAuth implements Route {

    private GestionUtilisateur gestionUtilisateur;

    public RouteAuth(GestionUtilisateur gestionUtilisateur) {
        this.gestionUtilisateur = gestionUtilisateur;
    }

    public boolean correspond(String chemin) {
        return chemin.equals("/api/signup")
            || chemin.equals("/api/login")
            || chemin.equals("/api/logout")
            || chemin.equals("/api/utilisateur");
    }

    public String[] traiter(String methode, String chemin, String body) {
        switch (chemin) {
            case "/api/signup": return signup(body);
            case "/api/login": return login(body);
            case "/api/logout": return logout();
            case "/api/utilisateur": return utilisateur();
            default: return reponse(404, "{\"erreur\":\"Route inconnue\"}");
        }
    }

    private String[] signup(String body) {
        String nom = JsonUtils.extraireString(body, "nom");
        String mdp = JsonUtils.extraireString(body, "motdepasse");
        Utilisateur u = (nom != null && mdp != null) ? gestionUtilisateur.creerCompte(nom, mdp) : null;
        if (u != null) {
            return reponse(201, JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur()));
        }
        return reponse(400, JsonUtils.erreur("Inscription echouee"));
    }

    private String[] login(String body) {
        String nom = JsonUtils.extraireString(body, "nom");
        String mdp = JsonUtils.extraireString(body, "motdepasse");
        if (gestionUtilisateur.getUtilisateurConnecte() != null) gestionUtilisateur.seDeconnecter();
        Utilisateur u = (nom != null && mdp != null) ? gestionUtilisateur.seConnecter(nom, mdp) : null;
        if (u != null) {
            return reponse(200, JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur()));
        }
        return reponse(401, JsonUtils.erreur("Nom ou mot de passe incorrect"));
    }

    private String[] logout() {
        if (gestionUtilisateur.getUtilisateurConnecte() != null) {
            gestionUtilisateur.seDeconnecter();
            return reponse(200, JsonUtils.succes());
        }
        return reponse(200, JsonUtils.erreur("Aucun utilisateur connecte"));
    }

    private String[] utilisateur() {
        Utilisateur c = gestionUtilisateur.getUtilisateurConnecte();
        if (c != null) {
            return reponse(200, "{\"connecte\":true,\"id\":" + c.getIdUtilisateur() + ",\"nom\":\"" + c.getNomUtilisateur() + "\"}");
        }
        return reponse(200, "{\"connecte\":false}");
    }

    private String[] reponse(int code, String json) {
        return new String[]{ String.valueOf(code), json };
    }
}
