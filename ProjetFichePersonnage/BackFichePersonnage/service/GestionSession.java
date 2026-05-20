package service;

import model.Utilisateur;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.spec.SecretKeySpec;

public class GestionSession {

    private final ConcurrentHashMap<String, Utilisateur> sessions;
    private final ConcurrentHashMap<String, SecretKeySpec> cles;

    // CREATION GESTION
    public GestionSession() {
        this.sessions = new ConcurrentHashMap<>();
        this.cles = new ConcurrentHashMap<>();
    }

    // CREER SESSION
    public String creerSession(Utilisateur utilisateur, SecretKeySpec cle) {
        String sessionid = UUID.randomUUID().toString();
        sessions.put(sessionid, utilisateur);
        cles.put(sessionid, cle);
        return sessionid;
    }

    // GET UTILISATEUR
    public Utilisateur getUtilisateurDepuisSession(String sessionid) {
        return sessions.get(sessionid);
    }

    // GET CLE
    public SecretKeySpec getCleDepuisSession(String sessionid) {
        return cles.get(sessionid);
    }

    // SUPPRIMER SESSION
    public void supprimerSession(String sessionid) {
        sessions.remove(sessionid);
        cles.remove(sessionid);
    }
}
