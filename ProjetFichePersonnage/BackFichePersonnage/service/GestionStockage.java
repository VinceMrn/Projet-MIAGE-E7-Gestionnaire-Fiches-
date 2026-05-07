package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Utilisateur;

public class GestionStockage {
    private static final String FICHIER_UTILISATEURS = "data/utilisateurs.txt";

    public void sauvegarderUtilisateurs(List<Utilisateur> utilisateurs) {
        File fichier = new File(FICHIER_UTILISATEURS);
        fichier.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichier))) {
            for (Utilisateur u : utilisateurs) {
                writer.write(u.getIdUtilisateur() + ";" + u.getNomUtilisateur() + ";" + u.getSel() + ";"
                        + u.getMotdepasseHash());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    public List<Utilisateur> chargerUtilisateurs() {
        File fichier = new File(FICHIER_UTILISATEURS);
        List<Utilisateur> liste = new ArrayList<>();

        if (!fichier.exists())
            return liste;

        try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(";");
                if (parties.length == 4) {
                    // format moderne
                    int id = Integer.parseInt(parties[0]);
                    liste.add(Utilisateur.depuisStockage(id, parties[1], parties[2], parties[3]));
                } else if (parties.length == 3) {
                    // ancien format -> migration auto
                    int id = Integer.parseInt(parties[0]);
                    liste.add(new Utilisateur(id, parties[1], parties[2]));
                    // parties[2] est le mdp en clair, le constructeur va le hasher
                }

            }
        } catch (IOException e) {
            System.out.println("Erreur chargement : " + e.getMessage());
        }

        return liste;
    }
}
