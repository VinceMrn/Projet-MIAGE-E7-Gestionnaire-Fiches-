package service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import model.Utilisateur;

public class GestionSession {

    private ConcurrentHashMap<String, Utilisateur> sessions;

    public GestionSession() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public String creerSession(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, utilisateur);
        return sessionId;
    }

    public Utilisateur getUtilisateurDepuisSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }

        return sessions.get(sessionId);
    }

    public void supprimerSession(String sessionId) {
        if (sessionId != null) {
            sessions.remove(sessionId);
        }
    }

    public boolean sessionExiste(String sessionId) {
        return sessionId != null && sessions.containsKey(sessionId);
    }

    public int nombreSessionsActives() {
        return sessions.size();
    }
}