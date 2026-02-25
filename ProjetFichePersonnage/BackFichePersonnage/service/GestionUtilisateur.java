package service;

import model.Utilisateur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service qui gere les comptes utilisateurs.
 *
 * Persistance : fichier texte "data/utilisateurs.txt"
 * Format d'une ligne : id;nomUtilisateur;hashMotDePasse
 * Exemple : 1;Alice;5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8
 *
 * Le separateur ";" est utilise car il n'apparait pas dans les noms ni les hash.
 */
public class GestionUtilisateur {

    private static final String FICHIER_UTILISATEURS = "data/utilisateurs.txt";
    private List<Utilisateur> utilisateurs;
    private Utilisateur utilisateurConnecte;

    public GestionUtilisateur() {
        this.utilisateurs = chargerUtilisateurs();
        this.utilisateurConnecte = null;
    }

    /**
     * Cree un nouveau compte utilisateur.
     * Verifie d'abord que le nom n'est pas deja pris.
     * Sauvegarde automatiquement dans le fichier apres creation.
     */
    public Utilisateur creerCompte(String nomUtilisateur, String motdepasse) {
        // Verification que le nom n'existe pas deja
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) {
                System.out.println("Erreur : le nom d'utilisateur '" + nomUtilisateur + "' existe deja.");
                return null;
            }
        }

        // Generation de l'ID : on prend le max des IDs existants + 1
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

    /**
     * Connecte un utilisateur.
     * Parcourt la liste, verifie nom + mot de passe.
     * Un seul utilisateur peut etre connecte a la fois.
     */
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

    /**
     * Deconnecte l'utilisateur courant.
     */
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

    /**
     * Sauvegarde tous les utilisateurs dans le fichier texte.
     *
     * Fonctionnement :
     * 1. On cree le dossier "data/" s'il n'existe pas (mkdirs)
     * 2. On ouvre un BufferedWriter sur le fichier (ecrase le contenu precedent)
     * 3. Pour chaque utilisateur, on ecrit une ligne : id;nom;hash
     * 4. Le try-with-resources ferme automatiquement le flux
     */
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

    /**
     * Charge les utilisateurs depuis le fichier texte.
     *
     * Fonctionnement :
     * 1. Si le fichier n'existe pas, on retourne une liste vide (premier lancement)
     * 2. On lit ligne par ligne avec BufferedReader
     * 3. On decoupe chaque ligne avec split(";") -> [id, nom, hash]
     * 4. On reconstruit l'objet Utilisateur avec le constructeur "depuis fichier"
     *    (qui ne re-hashe pas le mot de passe puisque c'est deja un hash)
     */
    private List<Utilisateur> chargerUtilisateurs() {
        File fichier = new File(FICHIER_UTILISATEURS);
        List<Utilisateur> liste = new ArrayList<>();

        if (!fichier.exists()) {
            return liste;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                // Decoupe la ligne : "1;Alice;5e884898..." -> ["1", "Alice", "5e884898..."]
                String[] parties = ligne.split(";");
                if (parties.length == 3) {
                    int id = Integer.parseInt(parties[0]);
                    String nom = parties[1];
                    String hash = parties[2];
                    // true = "depuis fichier", le hash est deja calcule
                    liste.add(new Utilisateur(id, nom, hash, true));
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement : " + e.getMessage());
        }

        return liste;
    }
}
