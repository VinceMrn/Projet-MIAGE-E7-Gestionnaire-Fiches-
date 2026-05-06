package service.route;

import model.Utilisateur;
import service.GestionFiche;
import service.GestionUtilisateur;
import service.JsonUtils;
import service.Result;
import service.GestionSession;

public class RouteAuth implements Route {

    private GestionUtilisateur gestionUtilisateur;
    private GestionFiche gestionFiche;
    private GestionSession gestionSession;

    public RouteAuth(GestionUtilisateur gestionUtilisateur, GestionFiche gestionFiche, GestionSession gestionSession) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionFiche = gestionFiche;
        this.gestionSession = gestionSession;
    }

    public boolean correspond(String chemin) {
        return chemin.equals("/api/signup")
            || chemin.equals("/api/login")
            || chemin.equals("/api/logout");
    }

    public String[] traiter(String methode, String chemin, String body, String sessionId) {
        switch (chemin) {
            case "/api/signup": {
                String nom = JsonUtils.extraireString(body, "nom");
                String mdp = JsonUtils.extraireString(body, "motdepasse");
                Result<Utilisateur> resultat = gestionUtilisateur.creerCompte(nom, mdp);
                if (resultat.estSucces()) {
                    Utilisateur u = resultat.getDonnees();
                    return new String[]{"201", JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur())};
                }
                return new String[]{"400", JsonUtils.erreur(resultat.getMessage())};
            }

            case "/api/login": {
                String nom = JsonUtils.extraireString(body, "nom");
                String mdp = JsonUtils.extraireString(body, "motdepasse");
                gestionSession.supprimerSession(sessionId);
                Result<Utilisateur> resultat = gestionUtilisateur.seConnecter(nom, mdp);
                if (resultat.estSucces()) {
                    Utilisateur u = resultat.getDonnees();
                    String newSessionId = gestionSession.creerSession(u);
                    gestionFiche.chargerFiches(u);
                    return new String[]{"200", JsonUtils.succesAvecIdNomSessionId(u.getIdUtilisateur(), u.getNomUtilisateur(), newSessionId)};
                }
                return new String[]{"401", JsonUtils.erreur(resultat.getMessage())};
            }

            case "/api/logout": {
                gestionSession.supprimerSession(sessionId);
                return new String[]{"200", JsonUtils.succes()};
            }

            default:
                return new String[]{"404", JsonUtils.erreur("Route inconnue")};
        }
    }
}
