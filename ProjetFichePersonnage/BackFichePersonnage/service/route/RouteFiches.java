package service.route;

import model.FichePersonnage;
import model.ModulePersonnalise;
import model.Statistique;
import model.Utilisateur;
import service.GestionFiche;
import service.GestionSession;
import service.JsonUtils;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class RouteFiches implements Route {

    private GestionFiche gestionFiche;
    private GestionSession gestionSession;

    // CREATION ROUTE
    public RouteFiches(GestionFiche gestionFiche, GestionSession gestionSession) {
        this.gestionFiche = gestionFiche;
        this.gestionSession = gestionSession;
    }

    // CORRESPOND CHEMIN
    public boolean correspond(String chemin) {
        return chemin.equals("/api/fiches") || chemin.startsWith("/api/fiches/");
    }

    // TRAITER REQUETE
    public String[] traiter(String methode, String chemin, String body, String sessionId) throws Exception {
        Utilisateur u = gestionSession.getUtilisateurDepuisSession(sessionId);
        SecretKeySpec cle = gestionSession.getCleDepuisSession(sessionId);
        if (u == null || cle == null) {
            return reponse(401, JsonUtils.erreur("Non connecte"));
        }

        if (chemin.equals("/api/fiches")) {
            return traiterListeOuCreation(methode, body, u, cle);
        }

        String[] segments = chemin.split("/");

        if (segments.length >= 5 && "elements".equals(segments[3])) {
            return traiterElements(methode, segments[4], u);
        }

        if (segments.length == 4 && "import".equals(segments[3])) {
            return traiterImport(methode, body, u, cle);
        }

        int idFiche;
        try {
            idFiche = Integer.parseInt(segments[3]);
        } catch (Exception e) {
            return reponse(400, JsonUtils.erreur("ID invalide"));
        }

        if (segments.length == 4) {
            return traiterFiche(methode, idFiche, u, cle);
        }

        if (segments.length >= 5) {
            return traiterRessource(methode, body, idFiche, segments, u, cle);
        }

        return reponse(404, JsonUtils.erreur("Route inconnue"));
    }

    // LISTE CREATION
    private String[] traiterListeOuCreation(String methode, String body, Utilisateur u, SecretKeySpec cle) {
        if ("GET".equals(methode)) {
            return reponse(200, JsonUtils.listeFichesVersJSON(gestionFiche.listerFiches(u)));
        }
        if ("POST".equals(methode)) {
            String nom = JsonUtils.extraireString(body, "nom");
            if (nom == null || nom.isEmpty()) {
                return reponse(400, JsonUtils.erreur("Nom requis"));
            }
            FichePersonnage f = gestionFiche.creerFiche(u, cle, nom);
            return reponse(201, JsonUtils.succesAvecIdNom(f.getIdFichePersonnage(), f.getNomFichePersonnage()));
        }
        return reponse(405, JsonUtils.erreur("Methode non autorisee"));
    }

    // TRAITER FICHE
    private String[] traiterFiche(String methode, int idFiche, Utilisateur u, SecretKeySpec cle) {
        if ("GET".equals(methode)) {
            FichePersonnage fiche = gestionFiche.getFiche(u, idFiche);
            if (fiche == null) return reponse(404, JsonUtils.erreur("Fiche non trouvee"));
            return reponse(200, JsonUtils.ficheVersJSON(fiche));
        }
        if ("DELETE".equals(methode)) {
            boolean ok = gestionFiche.supprimerFiche(u, cle, idFiche);
            return ok ? reponse(200, JsonUtils.succes()) : reponse(404, JsonUtils.erreur("Fiche non trouvee"));
        }
        return reponse(405, JsonUtils.erreur("Methode non autorisee"));
    }

    // TRAITER RESSOURCE
    private String[] traiterRessource(String methode, String body, int idFiche, String[] segments, Utilisateur u, SecretKeySpec cle) throws Exception {
        String ressource = segments[4];

        switch (ressource) {
            case "portrait":
                if ("PUT".equals(methode)) {
                    gestionFiche.modifierPortrait(u, cle, idFiche, JsonUtils.extraireString(body, "image"));
                    return reponse(200, JsonUtils.succes());
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "biographie":
                if ("PUT".equals(methode)) {
                    gestionFiche.modifierBiographie(u, cle, idFiche, JsonUtils.extraireString(body, "texte"));
                    return reponse(200, JsonUtils.succes());
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "statistiques":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    Integer val = JsonUtils.extraireInt(body, "valeur");
                    if (nom != null && val != null) {
                        gestionFiche.ajouterStatistique(u, cle, idFiche, nom, val);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom et valeur requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "competences":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    if (nom != null) {
                        gestionFiche.ajouterCompetence(u, cle, idFiche, nom);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "equipements":
                if ("POST".equals(methode)) {
                    String nom = JsonUtils.extraireString(body, "nom");
                    if (nom != null) {
                        gestionFiche.ajouterEquipement(u, cle, idFiche, nom);
                        return reponse(201, JsonUtils.succes());
                    }
                    return reponse(400, JsonUtils.erreur("nom requis"));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "module":
                return traiterModule(methode, body, idFiche, segments, u, cle);

            case "export":
                if ("GET".equals(methode)) {
                    byte[] data = gestionFiche.exporterFiche(u, idFiche);
                    if (data == null) return reponse(404, JsonUtils.erreur("Fiche non trouvee"));
                    FichePersonnage f = gestionFiche.getFiche(u, idFiche);
                    String base64 = Base64.getEncoder().encodeToString(data);
                    return reponse(200, JsonUtils.exportFicheVersJSON(f.getNomFichePersonnage(), base64));
                }
                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            case "modules-personnalises":
                if ("POST".equals(methode)) {
                    String mid = JsonUtils.extraireString(body, "id");
                    String mnom = JsonUtils.extraireString(body, "nom");
                    String mtype = JsonUtils.extraireString(body, "type");
                    if (mnom == null || mnom.isEmpty() || mtype == null || mtype.isEmpty()) {
                        return reponse(400, JsonUtils.erreur("nom et type requis"));
                    }
                    ModulePersonnalise mp = new ModulePersonnalise(mid != null ? mid : UUID.randomUUID().toString(), mnom, mtype);
                    String texte = JsonUtils.extraireString(body, "contenuTexte");
                    if (texte != null) mp.setContenuTexte(texte);
                    List<String> liste = JsonUtils.extraireArrayStrings(body, "contenuListe");
                    if (liste != null) mp.setContenuListe(liste);
                    List<Statistique> stats = JsonUtils.extraireArrayStatistiques(body, "contenuStats");
                    if (stats != null) mp.setContenuStats(stats);
                    gestionFiche.ajouterModulePersonnalise(u, cle, idFiche, mp);
                    return reponse(201, JsonUtils.succes());
                }

                if (segments.length >= 6 && "PUT".equals(methode)) {
                    String idModule = segments[5];
                    String mid = JsonUtils.extraireString(body, "id");
                    String mnom = JsonUtils.extraireString(body, "nom");
                    String mtype = JsonUtils.extraireString(body, "type");
                    if (mnom == null || mnom.isEmpty()) mnom = "Module";
                    ModulePersonnalise mp = new ModulePersonnalise(mid != null ? mid : idModule, mnom, mtype != null ? mtype : "texte");
                    String texte = JsonUtils.extraireString(body, "contenuTexte");
                    if (texte != null) mp.setContenuTexte(texte);
                    List<String> liste = JsonUtils.extraireArrayStrings(body, "contenuListe");
                    if (liste != null) mp.setContenuListe(liste);
                    List<Statistique> stats = JsonUtils.extraireArrayStatistiques(body, "contenuStats");
                    if (stats != null) mp.setContenuStats(stats);
                    gestionFiche.modifierModulePersonnalise(u, cle, idFiche, idModule, mp);
                    return reponse(200, JsonUtils.succes());
                }

                if (segments.length >= 6 && "DELETE".equals(methode)) {
                    String idModule = segments[5];
                    gestionFiche.supprimerModulePersonnalise(u, cle, idFiche, idModule);
                    return reponse(200, JsonUtils.succes());
                }

                return reponse(405, JsonUtils.erreur("Methode non autorisee"));

            default:
                return reponse(404, JsonUtils.erreur("Route inconnue"));
        }
    }

    // TRAITER MODULE
    private String[] traiterModule(String methode, String body, int idFiche, String[] segments, Utilisateur u, SecretKeySpec cle) {
        if (segments.length < 6 || !"PUT".equals(methode)) {
            return reponse(405, JsonUtils.erreur("Methode non autorisee"));
        }

        String action = segments[5];
        String module = JsonUtils.extraireString(body, "module");

        if ("position".equals(action)) {
            Integer x = JsonUtils.extraireInt(body, "posX");
            Integer y = JsonUtils.extraireInt(body, "posY");
            if (module != null && x != null && y != null) {
                boolean ok = gestionFiche.modifierPositionModule(u, cle, idFiche, module, x, y);
                return ok ? reponse(200, JsonUtils.succes()) : reponse(400, JsonUtils.erreur("Collision ou module inconnu"));
            }
            return reponse(400, JsonUtils.erreur("module, posX, posY requis"));
        }

        if ("taille".equals(action)) {
            Integer l = JsonUtils.extraireInt(body, "largeur");
            Integer h = JsonUtils.extraireInt(body, "hauteur");
            if (module != null && l != null && h != null) {
                boolean ok = gestionFiche.modifierTailleModule(u, cle, idFiche, module, l, h);
                return ok ? reponse(200, JsonUtils.succes()) : reponse(400, JsonUtils.erreur("Collision ou module inconnu"));
            }
            return reponse(400, JsonUtils.erreur("module, largeur, hauteur requis"));
        }

        return reponse(404, JsonUtils.erreur("Route inconnue"));
    }

    // TRAITER ELEMENTS
    private String[] traiterElements(String methode, String type, Utilisateur u) {
        if (!"GET".equals(methode)) {
            return reponse(405, JsonUtils.erreur("Methode non autorisee"));
        }
        StringBuilder json = new StringBuilder("[");
        if ("competences".equals(type)) {
            List<String> liste = gestionFiche.listerCompetencesUtilisateur(u);
            for (int i = 0; i < liste.size(); i++) {
                json.append("\"").append(liste.get(i)).append("\"");
                if (i < liste.size() - 1) json.append(",");
            }
        } else if ("equipements".equals(type)) {
            List<String> liste = gestionFiche.listerEquipementsUtilisateur(u);
            for (int i = 0; i < liste.size(); i++) {
                json.append("\"").append(liste.get(i)).append("\"");
                if (i < liste.size() - 1) json.append(",");
            }
        } else if ("statistiques".equals(type)) {
            List<Statistique> liste = gestionFiche.listerStatistiquesUtilisateur(u);
            for (int i = 0; i < liste.size(); i++) {
                Statistique s = liste.get(i);
                json.append("{\"nom\":\"").append(s.getNomStatistique())
                    .append("\",\"valeur\":").append(s.getValeurStatistique()).append("}");
                if (i < liste.size() - 1) json.append(",");
            }
        } else {
            return reponse(404, JsonUtils.erreur("Type inconnu"));
        }
        json.append("]");
        return reponse(200, json.toString());
    }

    // TRAITER IMPORT
    private String[] traiterImport(String methode, String body, Utilisateur u, SecretKeySpec cle) throws Exception {
        if (!"POST".equals(methode)) {
            return reponse(405, JsonUtils.erreur("Methode non autorisee"));
        }
        String base64 = JsonUtils.extraireString(body, "data");
        if (base64 == null || base64.isEmpty()) {
            return reponse(400, JsonUtils.erreur("data (base64) requis"));
        }
        byte[] data;
        try {
            data = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return reponse(400, JsonUtils.erreur("Base64 invalide"));
        }
        FichePersonnage fiche = gestionFiche.importerFiche(u, cle, data);
        if (fiche == null) return reponse(400, JsonUtils.erreur("Import echoue"));
        return reponse(201, JsonUtils.succesAvecIdNom(fiche.getIdFichePersonnage(), fiche.getNomFichePersonnage()));
    }

    // BUILD REPONSE
    private String[] reponse(int code, String json) {
        return new String[]{ String.valueOf(code), json };
    }
}
