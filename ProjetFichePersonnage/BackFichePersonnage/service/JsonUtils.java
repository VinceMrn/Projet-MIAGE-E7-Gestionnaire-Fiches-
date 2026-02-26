package service;

import model.*;
import java.util.List;

/**
 * Utilitaires pour le parsing et la serialisation JSON.
 * Pas de dependance externe (parsing manuel).
 */
public class JsonUtils {

    /**
     * Extraction d'une valeur String depuis un JSON.
     * Cherche "cle":"valeur" et retourne valeur.
     */
    public static String extraireString(String json, String cle) {
        String recherche = "\"" + cle + "\"";
        int index = json.indexOf(recherche);
        if (index == -1) return null;

        index = json.indexOf(":", index) + 1;
        while (index < json.length() && json.charAt(index) == ' ') index++;

        if (index < json.length() && json.charAt(index) == '"') {
            int debut = index + 1;
            int fin = json.indexOf('"', debut);
            return json.substring(debut, fin);
        }
        return null;
    }

    /**
     * Extraction d'une valeur Integer depuis un JSON.
     * Cherche "cle":123 et retourne 123.
     */
    public static Integer extraireInt(String json, String cle) {
        String recherche = "\"" + cle + "\"";
        int index = json.indexOf(recherche);
        if (index == -1) return null;

        index = json.indexOf(":", index) + 1;
        while (index < json.length() && json.charAt(index) == ' ') index++;

        StringBuilder sb = new StringBuilder();
        while (index < json.length() && (Character.isDigit(json.charAt(index)) || json.charAt(index) == '-')) {
            sb.append(json.charAt(index));
            index++;
        }
        if (sb.length() == 0) return null;
        return Integer.parseInt(sb.toString());
    }

    /**
     * Convertit une FichePersonnage complete en JSON.
     */
    public static String ficheVersJSON(FichePersonnage fiche) {
        StringBuilder json = new StringBuilder();
        json.append("{\"id\":").append(fiche.getIdFichePersonnage());
        json.append(",\"nom\":\"").append(fiche.getNomFichePersonnage()).append("\"");

        // Portrait
        Portrait p = fiche.getPortrait();
        json.append(",\"portrait\":{\"image\":\"").append(p.getImagePortrait()).append("\"")
            .append(",\"posX\":").append(p.getPositionX())
            .append(",\"posY\":").append(p.getPositionY())
            .append(",\"largeur\":").append(p.getLargeur())
            .append(",\"hauteur\":").append(p.getHauteur()).append("}");

        // Biographie
        Biographie b = fiche.getBiographie();
        json.append(",\"biographie\":{\"texte\":\"").append(b.getTexteBiographie()).append("\"")
            .append(",\"posX\":").append(b.getPositionX())
            .append(",\"posY\":").append(b.getPositionY())
            .append(",\"largeur\":").append(b.getLargeur())
            .append(",\"hauteur\":").append(b.getHauteur()).append("}");

        // Statistiques
        Statistiques stats = fiche.getStatistiques();
        json.append(",\"statistiques\":{\"posX\":").append(stats.getPositionX())
            .append(",\"posY\":").append(stats.getPositionY())
            .append(",\"largeur\":").append(stats.getLargeur())
            .append(",\"hauteur\":").append(stats.getHauteur())
            .append(",\"liste\":[");
        List<Statistique> listeStats = stats.getStatistiques();
        for (int i = 0; i < listeStats.size(); i++) {
            Statistique s = listeStats.get(i);
            json.append("{\"id\":").append(s.getIdStatistique())
                .append(",\"nom\":\"").append(s.getNomStatistique()).append("\"")
                .append(",\"valeur\":").append(s.getValeurStatistique()).append("}");
            if (i < listeStats.size() - 1) json.append(",");
        }
        json.append("]}");

        // Competences
        Competence comp = fiche.getCompetence();
        json.append(",\"competences\":{\"posX\":").append(comp.getPositionX())
            .append(",\"posY\":").append(comp.getPositionY())
            .append(",\"largeur\":").append(comp.getLargeur())
            .append(",\"hauteur\":").append(comp.getHauteur())
            .append(",\"liste\":[");
        List<String> listeComp = comp.getCompetences();
        for (int i = 0; i < listeComp.size(); i++) {
            json.append("\"").append(listeComp.get(i)).append("\"");
            if (i < listeComp.size() - 1) json.append(",");
        }
        json.append("]}");

        // Equipements
        Equipement equip = fiche.getEquipement();
        json.append(",\"equipements\":{\"posX\":").append(equip.getPositionX())
            .append(",\"posY\":").append(equip.getPositionY())
            .append(",\"largeur\":").append(equip.getLargeur())
            .append(",\"hauteur\":").append(equip.getHauteur())
            .append(",\"liste\":[");
        List<String> listeEquip = equip.getEquipements();
        for (int i = 0; i < listeEquip.size(); i++) {
            json.append("\"").append(listeEquip.get(i)).append("\"");
            if (i < listeEquip.size() - 1) json.append(",");
        }
        json.append("]}");

        json.append("}");
        return json.toString();
    }

    /**
     * Convertit une liste de fiches en JSON leger (id + nom seulement).
     */
    public static String listeFichesVersJSON(List<FichePersonnage> fiches) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < fiches.size(); i++) {
            FichePersonnage f = fiches.get(i);
            json.append("{\"id\":").append(f.getIdFichePersonnage())
                .append(",\"nom\":\"").append(f.getNomFichePersonnage()).append("\"}");
            if (i < fiches.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Construit une reponse JSON de succes.
     */
    public static String succes() {
        return "{\"succes\":true}";
    }

    /**
     * Construit une reponse JSON d'erreur.
     */
    public static String erreur(String message) {
        return "{\"erreur\":\"" + message + "\"}";
    }

    /**
     * Construit une reponse JSON de succes avec id et nom.
     */
    public static String succesAvecIdNom(int id, String nom) {
        return "{\"succes\":true,\"id\":" + id + ",\"nom\":\"" + nom + "\"}";
    }
}
