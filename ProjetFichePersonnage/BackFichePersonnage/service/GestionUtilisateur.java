package service;

import model.Utilisateur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionUtilisateur {

    private GestionStockage stockage;
    private List<Utilisateur> utilisateurs;

    public GestionUtilisateur() {
        this.stockage = new GestionStockage();
        this.utilisateurs = this.stockage.chargerUtilisateurs();
    }

    public Result<Utilisateur> creerCompte(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) {
                return Result.erreur("Le nom d'utilisateur '" + nomUtilisateur + "' est déjà pris.");
            }
        }

        int id = utilisateurs.size() + 1;

        Utilisateur nouveau = new Utilisateur(id, nomUtilisateur, motdepasse);
        utilisateurs.add(nouveau);
        this.stockage.sauvegarderUtilisateurs(utilisateurs);

        return Result.succes(nouveau);
    }

    public Result<Utilisateur> seConnecter(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)
                    && u.verifierMotDePasse(motdepasse)) {
                return Result.succes(u);
            }
        }

        return Result.erreur("Identifiants incorrects (nom ou mot de passe invalide).");
    }

    public List<Utilisateur> getUtilisateurs() {
        return Collections.unmodifiableList(new ArrayList<>(utilisateurs));
    }
}
