package service.route;

import model.Utilisateur;
import service.GestionFiche;
import service.GestionUtilisateur;
import service.GestionSession;
import service.JsonUtils;
// pour le chiffrement
import javax.crypto.spec.SecretKeySpec;
import service.GestionChiffrement;


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
                || chemin.equals("/api/logout")
                || chemin.equals("/api/utilisateur/identifiant")
                || chemin.equals("/api/utilisateur/motdepasse")
                || chemin.equals("/api/utilisateur/questionsecrete")
                || chemin.equals("/api/utilisateur/getquestionsecrete")
                || chemin.equals("/api/utilisateur/reinitialisermotdepasse");
    }

    public String[] traiter(String methode, String chemin, String body, String sessionId) throws Exception {
        switch (chemin) {
            case "/api/signup": {
                String nom = JsonUtils.extraireString(body, "nom");
                String mdp = JsonUtils.extraireString(body, "motdepasse");
                Utilisateur u = gestionUtilisateur.creerCompte(nom, mdp);
                SecretKeySpec cle = GestionChiffrement.genererCleDepuisHash(u);
                // plus de "if u != null" car creerCompte lance une exception en cas de nom deja
                // pris ou d'erreur d'inscription
                return new String[] { "201", JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur(),
                        gestionSession.creerSession(u, cle)) };

                // return new String[]{"400", JsonUtils.erreur("Nom d'utilisateur deja pris")};
                // se return ne sera jamais atteint car creerCompte lance une exception en cas de nom deja pris
            }

            case "/api/login": {
                String nom = JsonUtils.extraireString(body, "nom");
                String mdp = JsonUtils.extraireString(body, "motdepasse");
                gestionSession.supprimerSession(sessionId); // supprime session precedente si existante
                Utilisateur u = gestionUtilisateur.seConnecter(nom, mdp);
                SecretKeySpec cle = GestionChiffrement.genererCleDepuisHash(u);
                String newSessionId = gestionSession.creerSession(u, cle);

                gestionFiche.chargerFiches(u, cle);
                return new String[] { "200",
                        JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur(), newSessionId) };

                // return new String[]{"401", JsonUtils.erreur("Nom ou mot de passe
                // incorrect")};
                // se return ne sera jamais atteint car seConnecter lance une exception en cas
                // d'identifiants invalides
            }

            case "/api/logout": {
                gestionSession.supprimerSession(sessionId);
                return new String[] { "200", JsonUtils.succes() };
            }

            case "/api/utilisateur/identifiant": {
                if (!"PUT".equals(methode))
                    return new String[] { "405", JsonUtils.erreur("Methode non autorisee") };
                Utilisateur u = gestionSession.getUtilisateurDepuisSession(sessionId);
                if (u == null)
                    return new String[] { "401", JsonUtils.erreur("Non connecte") };
                String nom = JsonUtils.extraireString(body, "nom");
                if (nom == null || nom.isEmpty())
                    return new String[] { "400", JsonUtils.erreur("Nom requis") };
                boolean ok = gestionUtilisateur.modifierIdentifiant(u, nom);
                if (!ok)
                    return new String[] { "400", JsonUtils.erreur("Nom deja pris") };
                return new String[] { "200",
                        JsonUtils.succesAvecIdNom(u.getIdUtilisateur(), u.getNomUtilisateur(), sessionId) };
            }

            case "/api/utilisateur/motdepasse": {
                if (!"PUT".equals(methode))
                    return new String[] { "405", JsonUtils.erreur("Methode non autorisee") };
                Utilisateur u = gestionSession.getUtilisateurDepuisSession(sessionId);
                if (u == null)
                    return new String[] { "401", JsonUtils.erreur("Non connecte") };
                String ancien = JsonUtils.extraireString(body, "ancien");
                String nouveau = JsonUtils.extraireString(body, "nouveau");
                if (ancien == null || nouveau == null || nouveau.isEmpty())
                    return new String[] { "400", JsonUtils.erreur("Ancien et nouveau mot de passe requis") };
                boolean ok = gestionUtilisateur.modifierMotDePasse(u, ancien, nouveau);
                if (!ok)
                    return new String[] { "400", JsonUtils.erreur("Ancien mot de passe incorrect") };
                return new String[] { "200", JsonUtils.succes() };
            }
            
            //ajout de route pour la question secrete
            case "/api/utilisateur/questionsecrete": {
                if (!"PUT".equals(methode))
                    return new String[] { "405", JsonUtils.erreur("Methode non autorisee") };
                Utilisateur u = gestionSession.getUtilisateurDepuisSession(sessionId);
                if (u == null)
                    return new String[] { "401", JsonUtils.erreur("Non connecte") };
                String question = JsonUtils.extraireString(body, "question");
                String reponse = JsonUtils.extraireString(body, "reponse");
                if (question == null || question.isEmpty() || reponse == null || reponse.isEmpty())
                    return new String[] { "400", JsonUtils.erreur("Question et reponse secrete requises") };
                gestionUtilisateur.definirQuestionSecrete(u, question, reponse);
                return new String[] { "200", JsonUtils.succesAvecQuestionSecrete(question) };
            }

            //recuperation de la question secrete pour aider a la recuperation des fiches en cas d'oubli du mot de passe (route a definir dans RouteAuth)
            case "/api/utilisateur/getquestionsecrete": {
                if (!"POST".equals(methode))
                    return new String[] { "405", JsonUtils.erreur("Methode non autorisee") };
                String nom = JsonUtils.extraireString(body, "nom");
                if (nom == null || nom.isEmpty())
                    return new String[] { "400", JsonUtils.erreur("Nom requis") };
                String question = gestionUtilisateur.getQuestionSecrete(nom);
                if (question == null)
                    return new String[] { "404", JsonUtils.erreur("Utilisateur non trouve ou pas de question secrete") };
                return new String[] { "200", JsonUtils.succesAvecQuestionSecrete(question) };
            }

            //recuperation du mot de passe via la question secrete (route a definir dans RouteAuth)
             case "/api/utilisateur/reinitialisermotdepasse": {
                if (!"POST".equals(methode))
                    return new String[] { "405", JsonUtils.erreur("Methode non autorisee") };
                String nom = JsonUtils.extraireString(body, "nom");
                String reponse = JsonUtils.extraireString(body, "reponse");
                String nouveauMdp = JsonUtils.extraireString(body, "nouveau");
                if (nom == null || nom.isEmpty() || reponse == null || reponse.isEmpty() || nouveauMdp == null || nouveauMdp.isEmpty())
                    return new String[] { "400", JsonUtils.erreur("Nom, reponse et nouveau mot de passe requis") };
                boolean ok = gestionUtilisateur.reinitialiserMotDePasseAvecQuestionSecrete(nom, reponse, nouveauMdp);
                if (!ok)
                    return new String[] { "400", JsonUtils.erreur("Nom d'utilisateur incorrect ou reponse secrete incorrecte") };
                return new String[] { "200", JsonUtils.succes() };
            }

            default:
                return new String[] { "404", JsonUtils.erreur("Route inconnue") };
        }
    }
}
