package service;

import model.*;
import java.util.List;
import java.util.ArrayList;

public class JsonUtils {

    // EXTRAIRE STRING
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

    // EXTRAIRE INT
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

    // FICHE JSON
    public static String ficheVersJSON(FichePersonnage fiche) {
        StringBuilder json = new StringBuilder();
        json.append("{\"id\":").append(fiche.getIdFichePersonnage());
        json.append(",\"nom\":\"").append(fiche.getNomFichePersonnage()).append("\"");

        Portrait p = fiche.getPortrait();
        json.append(",\"portrait\":{\"image\":\"").append(p.getImagePortrait()).append("\"")
            .append(",\"posX\":").append(p.getPositionX())
            .append(",\"posY\":").append(p.getPositionY())
            .append(",\"largeur\":").append(p.getLargeur())
            .append(",\"hauteur\":").append(p.getHauteur()).append("}");

        Biographie b = fiche.getBiographie();
        json.append(",\"biographie\":{\"texte\":\"").append(b.getTexteBiographie()).append("\"")
            .append(",\"posX\":").append(b.getPositionX())
            .append(",\"posY\":").append(b.getPositionY())
            .append(",\"largeur\":").append(b.getLargeur())
            .append(",\"hauteur\":").append(b.getHauteur()).append("}");

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

        List<ModulePersonnalise> modulesPerso = fiche.getModulesPersonnalises();
        json.append(",\"modulesPersonnalises\":[");
        for (int i = 0; i < modulesPerso.size(); i++) {
            ModulePersonnalise mp = modulesPerso.get(i);
            json.append("{");
            json.append("\"id\":\"").append(mp.getId()).append("\"");
            json.append(",\"nom\":\"").append(mp.getNom()).append("\"");
            json.append(",\"type\":\"").append(mp.getType()).append("\"");
            if (mp.getContenuTexte() != null) {
                json.append(",\"contenuTexte\":\"").append(mp.getContenuTexte()).append("\"");
            }
            json.append(",\"contenuListe\":[");
            List<String> cl = mp.getContenuListe();
            for (int j = 0; j < cl.size(); j++) {
                json.append("\"").append(cl.get(j)).append("\"");
                if (j < cl.size() - 1) json.append(",");
            }
            json.append("]");
            json.append(",\"contenuStats\":[");
            List<Statistique> cs = mp.getContenuStats();
            for (int j = 0; j < cs.size(); j++) {
                Statistique s = cs.get(j);
                json.append("{\"nom\":\"").append(s.getNomStatistique()).append("\",\"valeur\":").append(s.getValeurStatistique()).append("}");
                if (j < cs.size() - 1) json.append(",");
            }
            json.append("]");
            json.append("}");
            if (i < modulesPerso.size() - 1) json.append(",");
        }
        json.append("]");

        json.append(",\"canvas\":{\"largeur\":").append(FichePersonnage.CANVAS_LARGEUR)
            .append(",\"hauteur\":").append(FichePersonnage.CANVAS_HAUTEUR).append("}");

        json.append("}");
        return json.toString();
    }

    // EXTRAIRE ARRAYSTRING
    public static List<String> extraireArrayStrings(String json, String cle) {
        List<String> res = new ArrayList<>();
        String recherche = "\"" + cle + "\"";
        int idx = json.indexOf(recherche);
        if (idx == -1) return null;
        idx = json.indexOf('[', idx);
        if (idx == -1) return null;
        int fin = json.indexOf(']', idx);
        if (fin == -1) return null;
        String contenu = json.substring(idx + 1, fin).trim();
        if (contenu.isEmpty()) return res;
        int i = 0;
        while (i < contenu.length()) {
            while (i < contenu.length() && contenu.charAt(i) != '"') i++;
            if (i >= contenu.length()) break;
            int debut = i + 1;
            int finq = contenu.indexOf('"', debut);
            if (finq == -1) break;
            res.add(contenu.substring(debut, finq));
            i = finq + 1;
        }
        return res;
    }

    // EXTRAIRE ARRAYSTATS
    public static List<Statistique> extraireArrayStatistiques(String json, String cle) {
        List<Statistique> res = new ArrayList<>();
        String recherche = "\"" + cle + "\"";
        int idx = json.indexOf(recherche);
        if (idx == -1) return null;
        idx = json.indexOf('[', idx);
        if (idx == -1) return null;
        int fin = json.indexOf(']', idx);
        if (fin == -1) return null;
        String contenu = json.substring(idx + 1, fin).trim();
        if (contenu.isEmpty()) return res;
        int i = 0;
        while (i < contenu.length()) {
            int objStart = contenu.indexOf('{', i);
            if (objStart == -1) break;
            int objEnd = contenu.indexOf('}', objStart);
            if (objEnd == -1) break;
            String obj = contenu.substring(objStart + 1, objEnd);
            String nom = extraireString("{" + obj + "}", "nom");
            Integer val = extraireInt("{" + obj + "}", "valeur");
            if (nom != null && val != null) res.add(new Statistique(0, nom, val));
            i = objEnd + 1;
        }
        return res;
    }

    // LISTE JSON
    public static String listeFichesVersJSON(List<FichePersonnage> fiches) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < fiches.size(); i++) {
            FichePersonnage f = fiches.get(i);
            String image = f.getPortrait() != null ? f.getPortrait().getImagePortrait() : "";
            if (image == null) image = "";
            json.append("{\"id\":").append(f.getIdFichePersonnage())
                .append(",\"nom\":\"").append(f.getNomFichePersonnage()).append("\"")
                .append(",\"portrait\":\"").append(image).append("\"}");
            if (i < fiches.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    // REPONSE SUCCES
    public static String succes() {
        return "{\"succes\":true}";
    }

    // REPONSE ERREUR
    public static String erreur(String message) {
        return "{\"erreur\":\"" + message + "\"}";
    }

    // SUCCES IDNOM
    public static String succesAvecIdNom(int id, String nom) {
        return "{\"succes\":true,\"id\":" + id + ",\"nom\":\"" + nom + "\"}";
    }

    // SUCCES SESSION
    public static String succesAvecIdNom(int id, String nom, String sessionId) {
        return "{\"succes\":true,\"id\":" + id + ",\"nom\":\"" + nom + "\",\"sessionId\":\"" + sessionId + "\"}";
    }

    // EXPORT JSON
    public static String exportFicheVersJSON(String nom, String base64) {
        return "{\"nom\":\"" + nom + "\",\"data\":\"" + base64 + "\"}";
    }

    // SUCCES QUESTION
    public static String succesAvecQuestionSecrete(String question) {
        return "{\"succes\":true,\"question\":\"" + question + "\"}";
    }
}
