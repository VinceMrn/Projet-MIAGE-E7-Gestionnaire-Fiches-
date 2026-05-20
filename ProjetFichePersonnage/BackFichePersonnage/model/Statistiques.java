package model;

import java.util.ArrayList;
import java.util.List;

public class Statistiques extends Module {

    private List<Statistique> statistiques;

    // CREATION STATISTIQUES
    public Statistiques(int positionX, int positionY, int largeur, int hauteur) {
        super(positionX, positionY, largeur, hauteur);
        this.statistiques = new ArrayList<>();
    }

    // AJOUTER STATISTIQUE
    public void ajouterStatistique(String nomStatistique, int valeurStatistique) {
        int id = statistiques.size() + 1;
        statistiques.add(new Statistique(id, nomStatistique, valeurStatistique));
    }

    // GET STATISTIQUES
    public List<Statistique> getStatistiques() {
        return statistiques;
    }
}
