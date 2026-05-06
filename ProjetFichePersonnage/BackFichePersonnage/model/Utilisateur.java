package model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private int idUtilisateur;
    private String nomUtilisateur;
    private String motdepasseHash;
    private String sel;
    private List<FichePersonnage> fiches;

    public Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasse) {
        this(idUtilisateur, nomUtilisateur, motdepasse, genererSelAleatoire());
    }

    public Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasse, String sel) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.sel = sel;
        this.motdepasseHash = hacherMotDePasse(motdepasse, sel);
        this.fiches = new ArrayList<>();
    }

    private Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasseHash, String sel, boolean donneesDejaHashes) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.motdepasseHash = motdepasseHash;
        this.sel = sel;
        this.fiches = new ArrayList<>();
    }

    public static Utilisateur depuisStockage(int idUtilisateur, String nomUtilisateur, String motdepasseHash, String sel) {
        return new Utilisateur(idUtilisateur, nomUtilisateur, motdepasseHash, sel, true);
    }

    public boolean verifierMotDePasse(String motdepasse) {
        String hashSaisi = hacherMotDePasse(motdepasse, this.sel);
        return MessageDigest.isEqual(
            Base64.getDecoder().decode(this.motdepasseHash),
            Base64.getDecoder().decode(hashSaisi)
        );
    }

    public static String genererSelAleatoire() {
        byte[] sel = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(sel);
        return Base64.getEncoder().encodeToString(sel);
    }

    public static String hacherMotDePasse(String motdepasse, String selBase64) {
        try {
            byte[] sel = Base64.getDecoder().decode(selBase64);
            PBEKeySpec spec = new PBEKeySpec(motdepasse.toCharArray(), sel, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de hasher le mot de passe", e);
        }
    }

    public FichePersonnage creerFiche(String nomFichePersonnage) {
        int id = fiches.size() + 1;
        FichePersonnage fiche = new FichePersonnage(id, nomFichePersonnage);
        fiches.add(fiche);
        return fiche;
    }

    public void supprimerFiche(int idFichePersonnage) {
        fiches.removeIf(fiche -> fiche.getIdFichePersonnage() == idFichePersonnage);
    }

    public int getIdUtilisateur() { return idUtilisateur; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getMotdepasseHash() { return motdepasseHash; }
    public String getSel() { return sel; }
    public List<FichePersonnage> getFiches() { return fiches; }
}
