package service;

import model.Utilisateur;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.*;


public class GestionChiffrement {
 public static SecretKeySpec genererCleDepuisHash(Utilisateur u) throws Exception {
    // On transforme le String (hexadécimal ou base64) en octets
    byte[] keyBytes = u.getMotdepasseHash().getBytes(StandardCharsets.UTF_8);
    
    // On repasse un petit coup de MessageDigest pour garantir 
    // que la clé de chiffrement est différente du hash de login
    MessageDigest sha = MessageDigest.getInstance("SHA-256");
    byte[] finalKey = sha.digest(keyBytes);
    
    // AES-128 demande 16 octets
    finalKey = Arrays.copyOf(finalKey, 16); 
    
    return new SecretKeySpec(finalKey, "AES");
    }   

    public static byte[] chiffrer(byte[] input, SecretKeySpec secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        return cipher.doFinal(input);
    }

    public static byte[] dechiffrer(byte[] inputchiffre, SecretKeySpec secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        return cipher.doFinal(inputchiffre);
    }
}
