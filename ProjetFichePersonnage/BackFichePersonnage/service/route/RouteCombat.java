package service.route;

import model.Ennemi;
import service.GestionCombat;
import service.JsonUtils;

public class RouteCombat implements Route {

    private GestionCombat gestionCombat;

    public RouteCombat(GestionCombat gestionCombat) {
        this.gestionCombat = gestionCombat;
    }

    public boolean correspond(String chemin) {
        return chemin.equals("/api/combat") || chemin.startsWith("/api/combat/");
    }

    public String[] traiter(String methode, String chemin, String body, String sessionId) throws Exception {
        String[] segments = chemin.split("/");
        if (segments.length < 4) {
            return reponse(404, JsonUtils.erreur("Route combat inconnue"));
        }

        switch (segments[3]) {
            case "ennemi":   return traiterEnnemi(methode, body);
            case "attaquer": return traiterAttaque(methode, body);
            case "de":       return traiterDe(methode, body);
            default:         return reponse(404, JsonUtils.erreur("Route combat inconnue"));
        }
    }

    // POST /api/combat/ennemi
    // Corps : {"niveau":2}
    private String[] traiterEnnemi(String methode, String body) {
        if (!"POST".equals(methode)) return reponse(405, JsonUtils.erreur("Methode non autorisee"));

        Integer niveau = JsonUtils.extraireInt(body, "niveau");
        if (niveau == null) niveau = 1;

        Ennemi e = gestionCombat.genererEnnemi(niveau);

        String json = "{\"nom\":\""  + e.getNom()     + "\""
                + ",\"hp\":"         + e.getHp()
                + ",\"attaque\":"    + e.getAttaque()
                + ",\"defense\":"    + e.getDefense()  + "}";

        return reponse(200, json);
    }

    // POST /api/combat/attaquer
    // Corps : {"valeurStatAttaque":12,"valeurStatDefense":8,"bonusDefense":0,
    //          "defenseEnnemi":7,"attaqueEnnemi":9}
    // Réponse : {..., "typeJoueur":2, "typeEnnemi":1, "description":"..."}
    private String[] traiterAttaque(String methode, String body) {
        if (!"POST".equals(methode)) return reponse(405, JsonUtils.erreur("Methode non autorisee"));

        Integer valeurStatAttaque = JsonUtils.extraireInt(body, "valeurStatAttaque");
        Integer valeurStatDefense = JsonUtils.extraireInt(body, "valeurStatDefense");
        Integer bonusDefense      = JsonUtils.extraireInt(body, "bonusDefense");
        Integer defenseEnnemi     = JsonUtils.extraireInt(body, "defenseEnnemi");
        Integer attaqueEnnemi     = JsonUtils.extraireInt(body, "attaqueEnnemi");

        if (valeurStatAttaque == null || valeurStatDefense == null
                || defenseEnnemi == null || attaqueEnnemi == null) {
            return reponse(400, JsonUtils.erreur("valeurStatAttaque, valeurStatDefense, defenseEnnemi et attaqueEnnemi requis"));
        }
        if (bonusDefense == null) bonusDefense = 0;

        // tour[0]=degatsJ, [1]=degatsE, [2]=deJ, [3]=deE, [4]=totalJ, [5]=totalE, [6]=typeJ, [7]=typeE
        int[] tour = gestionCombat.calculerTour(
                valeurStatAttaque, valeurStatDefense, bonusDefense, defenseEnnemi, attaqueEnnemi);

        String description = construireDescription(
                tour[2], valeurStatAttaque, tour[4], tour[0], tour[6],
                tour[3], attaqueEnnemi,    tour[5], tour[1], tour[7]);

        String json = "{\"degatsJoueur\":"  + tour[0]
                + ",\"degatsEnnemi\":"      + tour[1]
                + ",\"deJoueur\":"          + tour[2]
                + ",\"deEnnemi\":"          + tour[3]
                + ",\"totalJoueur\":"       + tour[4]
                + ",\"totalEnnemi\":"       + tour[5]
                + ",\"typeJoueur\":"        + tour[6]
                + ",\"typeEnnemi\":"        + tour[7]
                + ",\"description\":\""    + description + "\"}";

        return reponse(200, json);
    }

    // POST /api/combat/de
    // Corps : {"faces":20,"modificateur":5}
    private String[] traiterDe(String methode, String body) {
        if (!"POST".equals(methode)) return reponse(405, JsonUtils.erreur("Methode non autorisee"));

        Integer faces        = JsonUtils.extraireInt(body, "faces");
        Integer modificateur = JsonUtils.extraireInt(body, "modificateur");
        if (faces == null || faces < 2) faces = 20;
        if (modificateur == null) modificateur = 0;

        int de       = gestionCombat.lancerDe(faces);
        int resultat = de + modificateur;

        String json = "{\"de\":"           + de
                + ",\"modificateur\":"     + modificateur
                + ",\"resultat\":"         + resultat
                + ",\"description\":\"D"  + faces + " (" + de + ") + " + modificateur + " = " + resultat + "\"}";

        return reponse(200, json);
    }

    // Construit la description textuelle du tour.
    // Les tags [CRITIQUE !] et [ECHEC CRITIQUE] sont lus par React pour colorier les lignes du log.
    private String construireDescription(int deJ, int statJ, int totalJ, int degJ, int typeJ,
                                          int deE, int statE, int totalE, int degE, int typeE) {
        String texteJoueur;
        if (typeJ == -1) {
            texteJoueur = "Echec critique ! Vous trebuchet (D20:1) [ECHEC CRITIQUE]";
        } else if (typeJ == 2) {
            texteJoueur = "Vous frappez pour " + degJ + " degats (D20:20 CRIT +" + statJ + ") [CRITIQUE !]";
        } else if (degJ > 0) {
            texteJoueur = "Vous frappez pour " + degJ + " degats (D20:" + deJ + "+" + statJ + "=" + totalJ + ")";
        } else {
            texteJoueur = "Votre attaque rate (D20:" + deJ + "+" + statJ + "=" + totalJ + ")";
        }

        String texteEnnemi;
        if (typeE == 2) {
            texteEnnemi = "L ennemi frappe en critique pour " + degE + " degats [CRITIQUE !]";
        } else if (degE > 0) {
            texteEnnemi = "L ennemi riposte pour " + degE + " degats (D20:" + deE + "+" + statE + "=" + totalE + ")";
        } else {
            texteEnnemi = "L ennemi vous rate";
        }

        return texteJoueur + ". " + texteEnnemi + ".";
    }

    private String[] reponse(int code, String json) {
        return new String[]{ String.valueOf(code), json };
    }
}
