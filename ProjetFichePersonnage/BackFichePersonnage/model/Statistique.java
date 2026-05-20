package model;

import java.io.Serializable;

public class Statistique implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idStatistique;
    private String nomStatistique;
    private int valeurStatistique;

    // CREATION STATISTIQUE
    public Statistique(int idStatistique, String nomStatistique, int valeurStatistique) {
        this.idStatistique = idStatistique;
        this.nomStatistique = nomStatistique;
        this.valeurStatistique = valeurStatistique;
    }

    // MODIFIER NOM
    public void modifierNomStatistique(String nomStatistique) {
        this.nomStatistique = nomStatistique;
    }

    // MODIFIER VALEUR
    public void modifierValeurStatistique(int valeurStatistique) {
        this.valeurStatistique = valeurStatistique;
    }

    // GET ID
    public int getIdStatistique() { return idStatistique; }

    // GET NOM
    public String getNomStatistique() { return nomStatistique; }

    // GET VALEUR
    public int getValeurStatistique() { return valeurStatistique; }
}
