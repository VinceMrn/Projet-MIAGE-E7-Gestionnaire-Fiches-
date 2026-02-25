package model;

import java.io.Serializable;

/**
 * Represente une statistique individuelle (ex: Force, Agilite).
 * Implemente Serializable pour permettre la sauvegarde binaire.
 */
public class Statistique implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idStatistique;
    private String nomStatistique;
    private int valeurStatistique;

    public Statistique(int idStatistique, String nomStatistique, int valeurStatistique) {
        this.idStatistique = idStatistique;
        this.nomStatistique = nomStatistique;
        this.valeurStatistique = valeurStatistique;
    }

    public void modifierNomStatistique(String nomStatistique) {
        this.nomStatistique = nomStatistique;
    }

    public void modifierValeurStatistique(int valeurStatistique) {
        this.valeurStatistique = valeurStatistique;
    }

    public int getIdStatistique() {
        return idStatistique;
    }

    public String getNomStatistique() {
        return nomStatistique;
    }

    public int getValeurStatistique() {
        return valeurStatistique;
    }
}
