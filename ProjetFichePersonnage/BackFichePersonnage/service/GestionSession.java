package service;

import model.Utilisateur;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
//pour le chiffrement, pour chaque sessionId, on stocke la clé de chiffrement associée à l'utilisateur de cette session
import javax.crypto.spec.SecretKeySpec;

public class GestionSession {
    private final ConcurrentHashMap<String, Utilisateur> sessions ;
    //nouveau map pour stocker les clés de chiffrement associées à chaque utilisateur/session
    private final ConcurrentHashMap<String, SecretKeySpec> cles;
    
    public GestionSession() {
        this.sessions = new ConcurrentHashMap<>();
        this.cles = new ConcurrentHashMap<>();
    }
    
    //methodes 

    public String creerSession(Utilisateur utilisateur, SecretKeySpec cle) {
        String sessionid = UUID.randomUUID().toString();
        sessions.put(sessionid, utilisateur);
        cles.put(sessionid, cle);
        return sessionid;
    }

    public Utilisateur getUtilisateurDepuisSession(String sessionid) {
        return sessions.get(sessionid);
    }

    //nouvelle méthode pour récupérer la clé de chiffrement associée à une session
    public SecretKeySpec getCleDepuisSession(String sessionid) {
        return cles.get(sessionid);
    }

    public void supprimerSession(String sessionid) {
        sessions.remove(sessionid);
        cles.remove(sessionid); // important de supprimer aussi la clé associée à la session pour éviter les fuites de mémoire ou les risques de sécurité liés à des clés inutilisées
    }

    public boolean sessionExiste(String sessionid) {
        return sessionid != null && sessions.containsKey(sessionid);
    }
}
