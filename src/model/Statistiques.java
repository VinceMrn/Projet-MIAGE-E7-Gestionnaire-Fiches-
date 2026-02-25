package model;

import java.util.ArrayList;
import java.util.List;

public class Statistiques extends Module {

    private List<Statistique> statistiques;

    public Statistiques(int positionX, int positionY, int largeur, int hauteur) {
        super(positionX, positionY, largeur, hauteur);
        this.statistiques = new ArrayList<>();
    }

    public void ajouterStatistique(String nomStatistique, int valeurStatistique) {
        int id = statistiques.size() + 1;
        statistiques.add(new Statistique(id, nomStatistique, valeurStatistique));
    }

    public void modifierStatistique(int idStatistique, String nomStatistique, int valeurStatistique) {
        for (Statistique stat : statistiques) {
            if (stat.getIdStatistique() == idStatistique) {
                stat.modifierNomStatistique(nomStatistique);
                stat.modifierValeurStatistique(valeurStatistique);
                return;
            }
        }
    }

    public void supprimerStatistique(int idStatistique) {
        statistiques.removeIf(stat -> stat.getIdStatistique() == idStatistique);
    }

    public List<Statistique> getStatistiques() {
        return statistiques;
    }
}
