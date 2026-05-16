package model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUtilisateur;
    private String nomUtilisateur;
    //pour la sécurité on ne stocke pas le mot de passe en clair, on le hash
    private String motdepasseHash;
    private String sel; // sel pour le hash du mot de passe
    //pour la question secrète (réponse jamais stockée en clair)
    private String questionSecrete;
    private String reponseSecreteHash;
    private String selReponse; // sel dedie a la reponse, sinon un changement de mdp casserait la verification
    // Paramètres de PBKDF2
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private List<FichePersonnage> fiches;

    // Constructeur principale

    public Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasseEnClair) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.sel = genererSelAleatoire();
        this.motdepasseHash = hacherTexte(motdepasseEnClair, this.sel);
        this.fiches = new ArrayList<>();
    }

    // Constructeur Privé

    private Utilisateur(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.sel = sel;
        this.motdepasseHash = motdepasseHash;
        this.fiches = new ArrayList<>();
    }

    private Utilisateur(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash,
                        String questionSecrete, String reponseSecreteHash, String selReponse) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.sel = sel;
        this.motdepasseHash = motdepasseHash;
        this.questionSecrete = questionSecrete;
        this.reponseSecreteHash = reponseSecreteHash;
        this.selReponse = selReponse;
        this.fiches = new ArrayList<>();
    }

    // Constructeurs statiques (chargement depuis le fichier)

    public static Utilisateur depuisStockage(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash) {
        return new Utilisateur(idUtilisateur, nomUtilisateur, sel, motdepasseHash);
    }

    public static Utilisateur depuisStockage(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash,
                                             String questionSecrete, String reponseSecreteHash, String selReponse) {
        return new Utilisateur(idUtilisateur, nomUtilisateur, sel, motdepasseHash,
                               questionSecrete, reponseSecreteHash, selReponse);
    }

    public boolean verifierMotDePasse(String motdepasseCandidat) {
        if (motdepasseCandidat == null || this.sel == null || this.motdepasseHash == null) return false;
        String hashCandidat = hacherTexte(motdepasseCandidat, this.sel);
        return MessageDigest.isEqual(
            hashCandidat.getBytes(),
            this.motdepasseHash.getBytes()
        );
    }

    private static String genererSelAleatoire() {
        byte[] sel = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(sel);
        return Base64.getEncoder().encodeToString(sel);
    }

    private static String hacherTexte(String texte, String selBase64) {
        try {
            byte[] selBytes = Base64.getDecoder().decode(selBase64);
            KeySpec spec = new PBEKeySpec(texte.toCharArray(), selBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du hash du mot de passe", e);
        }
    }

    public FichePersonnage creerFiche(String nomFichePersonnage) {
        FichePersonnage fiche = new FichePersonnage(prochainIdFiche(), nomFichePersonnage);
        fiches.add(fiche);
        return fiche;
    }

    public int prochainIdFiche() {
        int max = 0;
        for (FichePersonnage f : fiches) if (f.getIdFichePersonnage() > max) max = f.getIdFichePersonnage();
        return max + 1;
    }

    public void supprimerFiche(int idFichePersonnage) {
        fiches.removeIf(fiche -> fiche.getIdFichePersonnage() == idFichePersonnage);
    }

    public void modifierNomUtilisateur(String nouveau) { this.nomUtilisateur = nouveau; }

    public void modifierMotDePasse(String nouveauEnClair) {
        this.sel = genererSelAleatoire();
        this.motdepasseHash = hacherTexte(nouveauEnClair, this.sel);
    }

    // Normalise la reponse pour eviter les pieges majuscules/espaces
    private static String normaliserReponse(String reponse) {
        return reponse == null ? null : reponse.trim().toLowerCase();
    }

    public void definirQuestionSecrete(String question, String reponseEnClair) {
        if (question == null || question.isEmpty()) throw new IllegalArgumentException("Question requise");
        if (reponseEnClair == null || reponseEnClair.isEmpty()) throw new IllegalArgumentException("Reponse requise");
        this.questionSecrete = question;
        this.selReponse = genererSelAleatoire();
        this.reponseSecreteHash = hacherTexte(normaliserReponse(reponseEnClair), this.selReponse);
    }

    public boolean verifierReponseSecrete(String reponseCandidate) {
        if (reponseCandidate == null || this.selReponse == null || this.reponseSecreteHash == null) return false;
        String hashCandidat = hacherTexte(normaliserReponse(reponseCandidate), this.selReponse);
        return MessageDigest.isEqual(
            hashCandidat.getBytes(),
            this.reponseSecreteHash.getBytes()
        );
    }

    public boolean possedeQuestionSecrete() {
        return questionSecrete != null && reponseSecreteHash != null && selReponse != null;
    }

    public int getIdUtilisateur() { return idUtilisateur; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getMotdepasseHash() { return motdepasseHash; }
    public String getSel() { return sel; }
    public String getQuestionSecrete() { return questionSecrete; }
    public String getReponseSecreteHash() { return reponseSecreteHash; }
    public String getSelReponse() { return selReponse; }
    public List<FichePersonnage> getFiches() { return fiches; }
}
