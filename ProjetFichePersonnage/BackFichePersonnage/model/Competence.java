package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Module competences contenant la liste des competences du personnage.
 * Herite de Module qui implemente deja Serializable.
 */
public class Competence extends Module {

    private List<String> competences;

    public Competence(int positionX, int positionY, int largeur, int hauteur) {
        super(positionX, positionY, largeur, hauteur);
        this.competences = new ArrayList<>();
    }

    public void ajouterCompetence(String nomCompetence) {
        competences.add(nomCompetence);
    }

    public void modifierCompetence(String ancienNom, String nouveauNom) {
        int index = competences.indexOf(ancienNom);
        if (index != -1) {
            competences.set(index, nouveauNom);
        }
    }

    public void supprimerCompetence(String nomCompetence) {
        competences.remove(nomCompetence);
    }

    public List<String> getCompetences() {
        return competences;
    }
}
