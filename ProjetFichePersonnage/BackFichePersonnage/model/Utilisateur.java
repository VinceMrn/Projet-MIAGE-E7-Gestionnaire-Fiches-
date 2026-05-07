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
    //pour la sécurité on ne stocke pas le mot de passe en clair
    private String motdepasseHash;
    private String sel;
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
        this.motdepasseHash = hacherMotDePasse(motdepasseEnClair, this.sel);
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

    // Constructeur statique

    public static Utilisateur depuisStockage(int idUtilisateur, String nomUtilisateur, String sel, String motdepasseHash) {
        return new Utilisateur(idUtilisateur, nomUtilisateur, sel, motdepasseHash);
    }

    public boolean verifierMotDePasse(String motdepasseCandidat) {
        if (motdepasseCandidat == null || this.sel == null || this.motdepasseHash == null) return false;
        String hashCandidat = hacherMotDePasse(motdepasseCandidat, this.sel);
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

    private static String hacherMotDePasse(String motdepasse, String selBase64) {
        try {
            byte[] selBytes = Base64.getDecoder().decode(selBase64);
            KeySpec spec = new PBEKeySpec(motdepasse.toCharArray(), selBytes, ITERATIONS, KEY_LENGTH);
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
        this.motdepasseHash = hacherMotDePasse(nouveauEnClair, this.sel);
    }

    public int getIdUtilisateur() { return idUtilisateur; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getMotdepasseHash() { return motdepasseHash; }
    public String getSel() { return sel; }
    public List<FichePersonnage> getFiches() { return fiches; }
}
