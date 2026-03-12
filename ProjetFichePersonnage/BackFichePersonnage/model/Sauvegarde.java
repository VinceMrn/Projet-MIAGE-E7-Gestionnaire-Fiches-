package model;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

public class Sauvegarde {

    private String cheminSauvegarde;
    private boolean estCrypter;

    //pour sauvegarder les fichier ou acceder

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

    //pour crypter et decrypter les fichier 
    public static void chiffrerFichier(String inputFile, String outputFile, SecretKeySpec secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) new File(inputFile).length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }

    public static void dechiffrerFichier(String inputFile, String outputFile, SecretKeySpec secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) new File(inputFile).length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }
}
