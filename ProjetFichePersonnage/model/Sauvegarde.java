package model;

public class Sauvegarde {

    private String cheminSauvegarde;
    private boolean estCrypter;

    public Sauvegarde(String cheminSauvegarde, boolean estCrypter) {
        this.cheminSauvegarde = cheminSauvegarde;
        this.estCrypter = estCrypter;
    }

    public void sauvegardeFiche(int idFichePersonnage, String cheminSauvegarde) {
        // Logique de sauvegarde de la fiche dans un fichier
        this.cheminSauvegarde = cheminSauvegarde;
    }

    public void crypterSauvegarde() {
        // Logique de cryptage du fichier sauvegardé
        this.estCrypter = true;
    }

    public void decrypterSauvegarde() {
        // Logique de décryptage du fichier sauvegardé
        this.estCrypter = false;
    }

    public String getCheminSauvegarde() {
        return cheminSauvegarde;
    }

    public boolean isEstCrypter() {
        return estCrypter;
    }
}
