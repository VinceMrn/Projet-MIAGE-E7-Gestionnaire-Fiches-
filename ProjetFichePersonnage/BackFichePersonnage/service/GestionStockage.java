package service;

import model.Utilisateur;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class GestionStockage {
    
    private static final String FICHIER_UTILISATEURS = "data/utilisateurs.txt";

    public void sauvegarderUtilisateurs(List<Utilisateur> utilisateurs) {
        File fichier = new File(FICHIER_UTILISATEURS);
        fichier.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichier), StandardCharsets.UTF_8))) {
            for (Utilisateur u : utilisateurs) {
                writer.write(u.getIdUtilisateur() + ";" + u.getNomUtilisateur() + ";" + u.getSel() + ";" + u.getMotdepasseHash());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    public List<Utilisateur> chargerUtilisateurs() {
        File fichier = new File(FICHIER_UTILISATEURS);
        List<Utilisateur> liste = new ArrayList<>();

        if (!fichier.exists()) return liste;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), StandardCharsets.UTF_8))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(";");
                if (parties.length == 4) {
                    int id = Integer.parseInt(parties[0]);
                    liste.add(Utilisateur.depuisStockage(id, parties[1], parties[3], parties[2]));
                } else if (parties.length == 3) {
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
