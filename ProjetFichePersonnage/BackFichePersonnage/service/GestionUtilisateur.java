package service;

import model.Utilisateur;
import java.util.List;

public class GestionUtilisateur {

    private List<Utilisateur> utilisateurs;
    private GestionStockage stockage;

    // CREATION GESTION
    public GestionUtilisateur() {
        this.stockage = new GestionStockage();
        this.utilisateurs = stockage.chargerUtilisateurs();
    }

    // CREER COMPTE
    public Utilisateur creerCompte(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) {
                throw new IllegalArgumentException("Nom d'utilisateur déjà pris");
            }
        }
        int id = utilisateurs.size() + 1;
        Utilisateur nouveau = new Utilisateur(id, nomUtilisateur, motdepasse);
        utilisateurs.add(nouveau);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return nouveau;
    }

    // SE CONNECTER
    public Utilisateur seConnecter(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur) && u.verifierMotDePasse(motdepasse)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Identifiants invalides");
    }

    // MODIFIER IDENTIFIANT
    public boolean modifierIdentifiant(Utilisateur u, String nouveauNom) {
        if (u == null || nouveauNom == null || nouveauNom.isEmpty()) return false;
        for (Utilisateur autre : utilisateurs) {
            if (autre != u && autre.getNomUtilisateur().equals(nouveauNom)) return false;
        }
        u.modifierNomUtilisateur(nouveauNom);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return true;
    }

    // MODIFIER MOTDEPASSE
    public boolean modifierMotDePasse(Utilisateur u, String ancienMdp, String nouveauMdp) {
        if (u == null || nouveauMdp == null || nouveauMdp.isEmpty()) return false;
        if (!u.verifierMotDePasse(ancienMdp)) return false;
        u.modifierMotDePasse(nouveauMdp);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return true;
    }

    // DEFINIR QUESTION
    public boolean definirQuestionSecrete(Utilisateur u, String question, String reponse) {
        if (u == null) return false;
        if (question == null || question.isEmpty() || question.length() < 10 || question.contains(";")) return false;
        if (reponse == null || reponse.isEmpty()) return false;
        u.definirQuestionSecrete(question, reponse);
        stockage.sauvegarderUtilisateurs(utilisateurs);
        return true;
    }

    // GET QUESTION
    public String getQuestionSecrete(String nomUtilisateur) {
        Utilisateur u = getUtilisateurParNom(nomUtilisateur);
        if (u == null) return null;
        return u.getQuestionSecrete();
    }

    // GET UTILISATEUR
    private Utilisateur getUtilisateurParNom(String nomUtilisateur) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) return u;
        }
        return null;
    }

    // REINITIALISER MOTDEPASSE
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
