package service;

import model.Utilisateur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionUtilisateur {

    private List<Utilisateur> utilisateurs;
    private GestionStockage stockage;

    public GestionUtilisateur() {
        this.stockage = new GestionStockage();
        this.utilisateurs = stockage.chargerUtilisateurs();
    }

    public Utilisateur creerCompte(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) {
                // pour ne pas retourner null en cas de nom déjà pris, on peut aussi lancer une exception
                throw new IllegalArgumentException("Nom d'utilisateur déjà pris");
            }
        }

        int id = utilisateurs.size() + 1;

        Utilisateur nouveau = new Utilisateur(id, nomUtilisateur, motdepasse);
        utilisateurs.add(nouveau);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return nouveau;
    }

    public Utilisateur seConnecter(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur) && u.verifierMotDePasse(motdepasse)) {
               // u = u;
                return u;
            }
        }
        //pour ne pas retourner null en cas d'identifiants invalides, on peut aussi lancer une exception
        throw new IllegalArgumentException("Identifiants invalides");
    }

    /**
     * Modifie l'identifiant de l'utilisateur connecte.
     * Echoue si non connecte ou si le nom est deja pris par un autre utilisateur.
     */
    public boolean modifierIdentifiant(Utilisateur u, String nouveauNom) {
        if (u == null) return false;
        if (nouveauNom == null || nouveauNom.isEmpty()) return false;
        // verifie unicite (sauf pour soi-meme)
        for (Utilisateur autre : utilisateurs) {
            if (autre != u && autre.getNomUtilisateur().equals(nouveauNom)) {
                return false;
            }
        }
        u.modifierNomUtilisateur(nouveauNom);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return true;
    }

    /**
     * Modifie le mot de passe de l'utilisateur connecte.
     * Echoue si non connecte ou si l'ancien mot de passe ne correspond pas.
     */
    public boolean modifierMotDePasse(Utilisateur u, String ancienMdp, String nouveauMdp) {
        if (u == null) return false;
        if (nouveauMdp == null || nouveauMdp.isEmpty()) return false;
        if (!u.verifierMotDePasse(ancienMdp)) return false;
        u.modifierMotDePasse(nouveauMdp);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return true;
    }

    public List<Utilisateur> getUtilisateurs() {
        // "photo immuable" retourne une liste non modifiable pour éviter les modifications externes en copiant la liste interne
        return Collections.unmodifiableList(new ArrayList<>(utilisateurs));
    }

    // methodes pour la question secrete
    // pour la definition de la question secrète on veut verifier que l'utilisateur n'est pas vide, que la question ne soit pas vide, et qu'elle fasse minimum 10 caracteres pour eviter les questions trop simples et on interdit les ';' pour eviter les problemes de parsing dans le fichier de stockage
    public boolean definirQuestionSecrete(Utilisateur u, String question, String reponse) {
        if (u == null) return false;
        if (question == null || question.isEmpty() || question.length() < 10 || question.contains(";")) return false;
        if (reponse == null || reponse.isEmpty()) return false;
        u.definirQuestionSecrete(question, reponse);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return true;
    }

    //getquestion secrete pour aider a la recuperation des fiches en cas d'oubli du mot de passe (route a definir dans RouteAuth)
    public String getQuestionSecrete(String nomUtilisateur) {
        Utilisateur u = getUtilisateurParNom(nomUtilisateur);
        if (u == null) return null;
        return u.getQuestionSecrete();
    }

    private Utilisateur getUtilisateurParNom(String nomUtilisateur) {
        //on pourrait match le nom dans la liste des utilisateurs pour trouver l'utilisateur correspondant
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) {
                return u;
            }
        }
        return null;
    }
    
    //reinitialiser mot de passe en utilisant la question secrete, pour aider a la recuperation des fiches en cas d'oubli du mot de passe (route a definir dans RouteAuth)
    public boolean reinitialiserMotDePasseAvecQuestionSecrete(String nomUtilisateur, String reponse, String nouveauMdp) {
        Utilisateur u = getUtilisateurParNom(nomUtilisateur);
        if (u == null) return false;
        if (nouveauMdp == null || nouveauMdp.isEmpty()) return false;
        if (!u.verifierReponseSecrete(reponse)) return false;
        u.modifierMotDePasse(nouveauMdp);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return true;
    }
}
