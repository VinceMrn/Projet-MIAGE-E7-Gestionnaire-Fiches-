package service.route;

public interface Route {

    // CORRESPOND CHEMIN
    boolean correspond(String chemin);

    // TRAITER REQUETE
    String[] traiter(String methode, String chemin, String body, String sessionId) throws Exception;
}
