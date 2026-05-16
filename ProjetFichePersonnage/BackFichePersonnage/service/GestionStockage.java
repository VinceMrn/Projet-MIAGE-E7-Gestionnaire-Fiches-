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
                StringBuilder ligne = new StringBuilder();
                ligne.append(u.getIdUtilisateur()).append(";")
                     .append(u.getNomUtilisateur()).append(";")
                     .append(u.getSel()).append(";")
                     .append(u.getMotdepasseHash());
                // Les 3 champs de la question secrete ne sont ecrits que si l'user en a une.
                // Cela garde la compatibilite avec les anciennes lignes a 4 champs.
                if (u.possedeQuestionSecrete()) {
                    ligne.append(";").append(u.getQuestionSecrete())
                         .append(";").append(u.getReponseSecreteHash())
                         .append(";").append(u.getSelReponse());
                }
                writer.write(ligne.toString());
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
                // limit = -1 pour conserver les eventuels champs vides en fin de ligne
                String[] parties = ligne.split(";", -1);
                if (parties.length == 7) {
                    // format avec question secrete : id;nom;sel;hashMdp;question;hashRep;selRep
                    int id = Integer.parseInt(parties[0]);
                    liste.add(Utilisateur.depuisStockage(
                        id, parties[1], parties[2], parties[3],
                        parties[4], parties[5], parties[6]
                    ));
                } else if (parties.length == 4) {
                    // format sans question secrete : id;nom;sel;hashMdp
                    int id = Integer.parseInt(parties[0]);
                    liste.add(Utilisateur.depuisStockage(id, parties[1], parties[2], parties[3]));
                } else if (parties.length == 3) {
                    // tres ancien format (mdp en clair) -> migration auto au prochain save
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
