package service;

import model.Utilisateur;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

public class GestionSecurite {
    
}

class CleUtilisateur extends Utilisateur {
    public CleUtilisateur(int idUtilisateur, String nomUtilisateur, String motdepasseHash) {
        super(idUtilisateur, nomUtilisateur, motdepasseHash);
    }

    public static SecretKeySpec genererCleDepuisHash(String passwordHash) throws Exception {
    // On transforme le String (hexadécimal ou base64) en octets
    byte[] keyBytes = passwordHash.getBytes(StandardCharsets.UTF_8);
    
    // On repasse un petit coup de MessageDigest pour garantir 
    // que la clé de chiffrement est différente du hash de login
    MessageDigest sha = MessageDigest.getInstance("SHA-256");
    byte[] finalKey = sha.digest(keyBytes);
    
    // AES-128 demande 16 octets
    finalKey = Arrays.copyOf(finalKey, 16); 
    
    return new SecretKeySpec(finalKey, "AES");
    }
    
}

class CrypterDecrypterAES {
    
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
