package service.route;

import model.Utilisateur;
import service.GestionFiche;
import service.GestionUtilisateur;
import service.JsonUtils;

public class RouteAuth implements Route {

    private GestionUtilisateur gestionUtilisateur;
    private GestionFiche gestionFiche;

    public RouteAuth(GestionUtilisateur gestionUtilisateur, GestionFiche gestionFiche) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionFiche = gestionFiche;
    }

    public boolean correspond(String chemin) {
        return chemin.equals("/api/signup")
            || chemin.equals("/api/login")
            || chemin.equals("/api/logout");
    }

    public String[] traiter(String methode, String chemin, String body) {
        switch (chemin) {
            case "/api/signup": {
                String nom = JsonUtils.extraireString(body, "nom");
                String mdp = JsonUtils.extraireString(body, "motdepasse");
                Utilisateur u = gestionUtilisateur.creerCompte(nom, mdp);
                if (u != null) {
                    return new String[]{"201", JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur())};
                }
                return new String[]{"400", JsonUtils.erreur("Inscription echouee")};
            }

            case "/api/login": {
                String nom = JsonUtils.extraireString(body, "nom");
                String mdp = JsonUtils.extraireString(body, "motdepasse");
                gestionUtilisateur.seDeconnecter();
                Utilisateur u = gestionUtilisateur.seConnecter(nom, mdp);
                if (u != null) {
                    gestionFiche.chargerFiches(u);
                    return new String[]{"200", JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur())};
                }
                return new String[]{"401", JsonUtils.erreur("Nom ou mot de passe incorrect")};
            }

            case "/api/logout": {
                gestionUtilisateur.seDeconnecter();
                return new String[]{"200", JsonUtils.succes()};
            }

            default:
                return new String[]{"404", JsonUtils.erreur("Route inconnue")};
        }
    }
}
