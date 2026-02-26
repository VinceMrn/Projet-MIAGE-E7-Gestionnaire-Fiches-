package service.route;

public interface Route {

    /** Retourne true si cette route gere le chemin donne */
    boolean correspond(String chemin);

    /** Traite la requete et retourne la reponse [code, json] */
    String[] traiter(String methode, String chemin, String body);
}
