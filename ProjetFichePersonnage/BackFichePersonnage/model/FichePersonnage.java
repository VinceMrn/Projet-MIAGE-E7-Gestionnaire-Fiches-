package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FichePersonnage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CANVAS_LARGEUR = 1350;
    public static final int CANVAS_HAUTEUR = 600;

    private int idFichePersonnage;
    private String nomFichePersonnage;
    private Portrait portrait;
    private Biographie biographie;
    private Statistiques statistiques;
    private Competence competence;
    private Equipement equipement;
    private List<ModulePersonnalise> modulesPersonnalises;

    // CREATION FICHE
    public FichePersonnage(int idFichePersonnage, String nomFichePersonnage) {
        this.idFichePersonnage = idFichePersonnage;
        this.nomFichePersonnage = nomFichePersonnage;
        this.portrait = new Portrait(0, 0, 200, 200, "");
        this.biographie = new Biographie(0, 200, 400, 200, "");
        this.statistiques = new Statistiques(200, 0, 300, 200);
        this.competence = new Competence(0, 400, 300, 200);
        this.equipement = new Equipement(300, 400, 300, 200);
        this.modulesPersonnalises = new ArrayList<>();
    }

    // MODIFIER PORTRAIT
    public void modifierPortrait(String imagePortrait) {
        this.portrait.modifierPortrait(imagePortrait);
    }

    // MODIFIER BIOGRAPHIE
    public void modifierBiographie(String texteBiographie) {
        this.biographie.modifierBiographie(texteBiographie);
    }

    // GET ID
    public int getIdFichePersonnage() {
        return idFichePersonnage;
    }

    // SET ID
    public void setIdFichePersonnage(int idFichePersonnage) {
        this.idFichePersonnage = idFichePersonnage;
    }

    // GET NOM
    public String getNomFichePersonnage() {
        return nomFichePersonnage;
    }

    // GET PORTRAIT
    public Portrait getPortrait() {
        return portrait;
    }

    // GET BIOGRAPHIE
    public Biographie getBiographie() {
        return biographie;
    }

    // GET STATISTIQUES
    public Statistiques getStatistiques() {
        return statistiques;
    }

    // GET COMPETENCE
    public Competence getCompetence() {
        return competence;
    }

    // GET EQUIPEMENT
    public Equipement getEquipement() {
        return equipement;
    }

    // GET MODULES
    public List<ModulePersonnalise> getModulesPersonnalises() {
        if (modulesPersonnalises == null) {
            modulesPersonnalises = new ArrayList<>();
        }
        return modulesPersonnalises;
    }
}
