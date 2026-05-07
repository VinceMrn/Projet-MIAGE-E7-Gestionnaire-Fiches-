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

}
