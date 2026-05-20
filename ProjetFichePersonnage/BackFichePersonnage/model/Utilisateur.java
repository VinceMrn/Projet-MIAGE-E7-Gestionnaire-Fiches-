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
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private int idUtilisateur;
    private String nomUtilisateur;
    private String motdepasseHash;
    private String sel;
    private String questionSecrete;
    private String reponseSecreteHash;
    private String selReponse;
    private List<FichePersonnage> fiches;

    // CREATION UTILISATEUR
    public Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasseEnClair) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.sel = genererSelAleatoire();
        this.motdepasseHash = hacherTexte(motdepasseEnClair, this.sel);
        this.fiches = new ArrayList<>();
    }

    // CREATION INTERNE
    private Utilisateur(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.sel = sel;
        this.motdepasseHash = motdepasseHash;
        this.fiches = new ArrayList<>();
    }

    // CREATION COMPLETE
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

    // CHARGER UTILISATEUR
    public static Utilisateur depuisStockage(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash) {
        return new Utilisateur(idUtilisateur, nomUtilisateur, sel, motdepasseHash);
    }

    // CHARGER COMPLET
    public static Utilisateur depuisStockage(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash,
                                             String questionSecrete, String reponseSecreteHash, String selReponse) {
        return new Utilisateur(idUtilisateur, nomUtilisateur, sel, motdepasseHash,
                               questionSecrete, reponseSecreteHash, selReponse);
    }

    // VERIFIER MOTDEPASSE
    public boolean verifierMotDePasse(String motdepasseCandidat) {
        if (motdepasseCandidat == null || this.sel == null || this.motdepasseHash == null) return false;
        String hashCandidat = hacherTexte(motdepasseCandidat, this.sel);
        return MessageDigest.isEqual(hashCandidat.getBytes(), this.motdepasseHash.getBytes());
    }

    // GENERER SEL
    private static String genererSelAleatoire() {
        byte[] sel = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(sel);
        return Base64.getEncoder().encodeToString(sel);
    }

    // HACHER TEXTE
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

    // CREER FICHE
    public FichePersonnage creerFiche(String nomFichePersonnage) {
        FichePersonnage fiche = new FichePersonnage(prochainIdFiche(), nomFichePersonnage);
        fiches.add(fiche);
        return fiche;
    }

    // PROCHAIN ID
    public int prochainIdFiche() {
        int max = 0;
        for (FichePersonnage f : fiches) if (f.getIdFichePersonnage() > max) max = f.getIdFichePersonnage();
        return max + 1;
    }

    // SUPPRIMER FICHE
    public void supprimerFiche(int idFichePersonnage) {
        fiches.removeIf(fiche -> fiche.getIdFichePersonnage() == idFichePersonnage);
    }

    // MODIFIER NOM
    public void modifierNomUtilisateur(String nouveau) {
        this.nomUtilisateur = nouveau;
    }

    // MODIFIER MOTDEPASSE
    public void modifierMotDePasse(String nouveauEnClair) {
        this.sel = genererSelAleatoire();
        this.motdepasseHash = hacherTexte(nouveauEnClair, this.sel);
    }

    // NORMALISER REPONSE
    private static String normaliserReponse(String reponse) {
        return reponse == null ? null : reponse.trim().toLowerCase();
    }

    // DEFINIR QUESTION
    public void definirQuestionSecrete(String question, String reponseEnClair) {
        if (question == null || question.isEmpty()) throw new IllegalArgumentException("Question requise");
        if (reponseEnClair == null || reponseEnClair.isEmpty()) throw new IllegalArgumentException("Reponse requise");
        this.questionSecrete = question;
        this.selReponse = genererSelAleatoire();
        this.reponseSecreteHash = hacherTexte(normaliserReponse(reponseEnClair), this.selReponse);
    }

    // VERIFIER REPONSE
    public boolean verifierReponseSecrete(String reponseCandidate) {
        if (reponseCandidate == null || this.selReponse == null || this.reponseSecreteHash == null) return false;
        String hashCandidat = hacherTexte(normaliserReponse(reponseCandidate), this.selReponse);
        return MessageDigest.isEqual(hashCandidat.getBytes(), this.reponseSecreteHash.getBytes());
    }

    // POSSEDE QUESTION
    public boolean possedeQuestionSecrete() {
        return questionSecrete != null && reponseSecreteHash != null && selReponse != null;
    }

    // GET ID
    public int getIdUtilisateur() { return idUtilisateur; }

    // GET NOM
    public String getNomUtilisateur() { return nomUtilisateur; }

    // GET HASH
    public String getMotdepasseHash() { return motdepasseHash; }

    // GET SEL
    public String getSel() { return sel; }

    // GET QUESTION
    public String getQuestionSecrete() { return questionSecrete; }

    // GET HASHREPONSE
    public String getReponseSecreteHash() { return reponseSecreteHash; }

    // GET SELREPONSE
    public String getSelReponse() { return selReponse; }

    // GET FICHES
    public List<FichePersonnage> getFiches() { return fiches; }
}
