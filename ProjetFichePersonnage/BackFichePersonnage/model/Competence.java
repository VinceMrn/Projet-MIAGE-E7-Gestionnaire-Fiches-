package model;

import java.util.ArrayList;
import java.util.List;

public class Competence extends Module {

    private List<String> competences;

    // CREATION COMPETENCE
    public Competence(int positionX, int positionY, int largeur, int hauteur) {
        super(positionX, positionY, largeur, hauteur);
        this.competences = new ArrayList<>();
    }

    // AJOUTER COMPETENCE
    public void ajouterCompetence(String nomCompetence) {
        competences.add(nomCompetence);
    }

    // GET COMPETENCES
    public List<String> getCompetences() {
        return competences;
    }
}
