package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Module equipement contenant la liste des equipements du personnage.
 * Herite de Module qui implemente deja Serializable.
 */
public class Equipement extends Module {

    private List<String> equipements;

    public Equipement(int positionX, int positionY, int largeur, int hauteur) {
        super(positionX, positionY, largeur, hauteur);
        this.equipements = new ArrayList<>();
    }

    public void ajouterEquipement(String nomEquipement) {
        equipements.add(nomEquipement);
    }

    public void modifier(String ancienNom, String nouveauNom) {
        int index = equipements.indexOf(ancienNom);
        if (index != -1) {
            equipements.set(index, nouveauNom);
        }
    }

    public void supprimerEquipement(String nomEquipement) {
        equipements.remove(nomEquipement);
    }

    public List<String> getEquipements() {
        return equipements;
    }
}
