package model;

import java.io.*;

public class Sauvegarde {

    public static void sauvegarder(FichePersonnage fiche, String chemin) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chemin));
        oos.writeObject(fiche);
        oos.close();
    }

    public static FichePersonnage charger(String chemin) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(chemin));
        FichePersonnage fiche = (FichePersonnage) ois.readObject();
        ois.close();
        return fiche;
    }
}
