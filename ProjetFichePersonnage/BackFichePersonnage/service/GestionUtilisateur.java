package service;

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

    public Utilisateur creerCompte(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur)) {
                return null;
            }
        }

        int id = utilisateurs.size() + 1;

        Utilisateur nouveau = new Utilisateur(id, nomUtilisateur, motdepasse);
        utilisateurs.add(nouveau);
        sauvegarderUtilisateurs();
        return nouveau;
    }

    public Utilisateur seConnecter(String nomUtilisateur, String motdepasse) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNomUtilisateur().equals(nomUtilisateur) && u.verifierMotDePasse(motdepasse)) {
                utilisateurConnecte = u;
                return u;
            }
        }
        return null;
    }

    public void seDeconnecter() {
        utilisateurConnecte = null;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    private void sauvegarderUtilisateurs() {
        File fichier = new File(FICHIER_UTILISATEURS);
        fichier.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichier))) {
            for (Utilisateur u : utilisateurs) {
                writer.write(u.getIdUtilisateur() + ";" + u.getNomUtilisateur() + ";" + u.getMotdepasse());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    private List<Utilisateur> chargerUtilisateurs() {
        File fichier = new File(FICHIER_UTILISATEURS);
        List<Utilisateur> liste = new ArrayList<>();

        if (!fichier.exists()) return liste;

        try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(";");
                if (parties.length == 3) {
                    int id = Integer.parseInt(parties[0]);
                    liste.add(new Utilisateur(id, parties[1], parties[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur chargement : " + e.getMessage());
        }

        return liste;
    }
}
