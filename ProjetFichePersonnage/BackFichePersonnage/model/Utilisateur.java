package model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represente un utilisateur avec son compte protege par mot de passe.
 * Le mot de passe est stocke sous forme de hash SHA-256 (jamais en clair).
 * Implemente Serializable pour permettre la sauvegarde binaire.
 */
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUtilisateur;
    private String nomUtilisateur;
    private String motdepasseHash; // hash SHA-256, jamais le mot de passe en clair
    private List<FichePersonnage> fiches;

    /**
     * Constructeur pour creer un NOUVEAU compte.
     * Le mot de passe est automatiquement hashe avant stockage.
     */
    public Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasse) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.motdepasseHash = hasherMotDePasse(motdepasse);
        this.fiches = new ArrayList<>();
    }

    /**
     * Constructeur pour RECHARGER un utilisateur depuis le fichier.
     * Le hash est deja calcule, on le prend tel quel.
     * Le boolean sert juste a differencier les deux constructeurs.
     */
    public Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasseHash, boolean depuisFichier) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.motdepasseHash = motdepasseHash;
        this.fiches = new ArrayList<>();
    }

    /**
     * Verifie si le mot de passe fourni correspond au hash stocke.
     * On hashe le mot de passe fourni et on compare les deux hash.
     */
    public boolean verifierMotDePasse(String motdepasse) {
        return this.motdepasseHash.equals(hasherMotDePasse(motdepasse));
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

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public String getMotdepasseHash() {
        return motdepasseHash;
    }

    public List<FichePersonnage> getFiches() {
        return fiches;
    }

    /**
     * Hashe un mot de passe avec SHA-256.
     *
     * Fonctionnement :
     * 1. On recupere une instance de l'algorithme SHA-256
     * 2. On convertit le mot de passe en tableau d'octets (bytes)
     * 3. SHA-256 produit un hash de 32 octets (256 bits)
     * 4. On convertit chaque octet en hexadecimal (2 caracteres par octet)
     * 5. Resultat : une chaine de 64 caracteres hexadecimaux
     *
     * Exemple : "password" -> "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
     */
    private static String hasherMotDePasse(String motdepasse) {
        try {
            // MessageDigest est la classe Java standard pour le hashage
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() : convertit le texte en hash (tableau de 32 octets)
            byte[] hash = md.digest(motdepasse.getBytes());

            // Conversion des octets en chaine hexadecimale lisible
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                // %02x : formate un octet en 2 caracteres hexa (ex: 0a, ff, 3b)
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 non disponible", e);
        }
    }
}
