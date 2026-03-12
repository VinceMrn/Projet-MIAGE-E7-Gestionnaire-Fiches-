package service;

import model.Sauvegarde;
import model.Utilisateur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestionUtilisateur {

    private static final String FICHIER_UTILISATEURS = "data/utilisateurs.txt";
    private List<Utilisateur> utilisateurs;
    private Utilisateur utilisateurConnecte;

    public GestionUtilisateur() {
        this.utilisateurs = chargerUtilisateurs();
        this.utilisateurConnecte = null;
    }

    // Cree un nouveau compte utilisateur.
    public Utilisateur creerCompte(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) {
                System.out.println("Erreur : le nom d'utilisateur '" + nomUtilisateur + "' existe deja.");
                return null;
            }
        }

        int id = 1;
        for (Utilisateur u : utilisateurs) {
            if (u.getIdUtilisateur() >= id) {
                id = u.getIdUtilisateur() + 1;
            }
        }

        Utilisateur nouveau = new Utilisateur(id, nomUtilisateur, motdepasse);
        utilisateurs.add(nouveau);
        sauvegarderUtilisateurs();
        System.out.println("Compte cree avec succes pour : " + nomUtilisateur + " (id=" + id + ")");
        return nouveau;
    }

    // Connecte un utilisateur.
    public Utilisateur seConnecter(String nomUtilisateur, String motdepasse) {
        if (utilisateurConnecte != null) {
            System.out.println("Erreur : " + utilisateurConnecte.getNomUtilisateur() + " est deja connecte. Deconnectez-vous d'abord.");
            return null;
        }

        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur) && u.verifierMotDePasse(motdepasse)) {
                utilisateurConnecte = u;
                System.out.println("Connexion reussie. Bienvenue " + nomUtilisateur + " !");
                return u;
            }
        }

        System.out.println("Erreur : nom d'utilisateur ou mot de passe incorrect.");
        return null;
    }

    // Deconnecte l'utilisateur courant.
    public void seDeconnecter() {
        if (utilisateurConnecte == null) {
            System.out.println("Erreur : aucun utilisateur connecte.");
            return;
        }
        System.out.println("Deconnexion de " + utilisateurConnecte.getNomUtilisateur() + ".");
        utilisateurConnecte = null;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }



    // Sauvegarde tous les utilisateurs dans le fichier texte.
    private void sauvegarderUtilisateurs() {
        File fichier = new File(FICHIER_UTILISATEURS);
        fichier.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichier))) {
            for (Utilisateur u : utilisateurs) {
                // On ecrit : id;nom;hashMotDePasse
                writer.write(u.getIdUtilisateur() + ";" + u.getNomUtilisateur() + ";" + u.getMotdepasseHash());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    // Charge les utilisateurs depuis le fichier texte.
    private List<Utilisateur> chargerUtilisateurs() {
        File fichier = new File(FICHIER_UTILISATEURS);
        List<Utilisateur> liste = new ArrayList<>();

        if (!fichier.exists()) {
            return liste;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(";");
                if (parties.length == 3) {
                    int id = Integer.parseInt(parties[0]);
                    String nom = parties[1];
                    String hash = parties[2];
                    liste.add(new Utilisateur(id, nom, hash, true));
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement : " + e.getMessage());
        }

        return liste;
    }
}
