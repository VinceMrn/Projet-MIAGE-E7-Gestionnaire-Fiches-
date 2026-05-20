package model;

import java.util.ArrayList;
import java.util.List;

public class Equipement extends Module {

    private List<String> equipements;

    // CREATION EQUIPEMENT
    public Equipement(int positionX, int positionY, int largeur, int hauteur) {
        super(positionX, positionY, largeur, hauteur);
        this.equipements = new ArrayList<>();
    }

    // AJOUTER EQUIPEMENT
    public void ajouterEquipement(String nomEquipement) {
        equipements.add(nomEquipement);
    }

    // GET EQUIPEMENTS
    public List<String> getEquipements() {
        return equipements;
    }
}
