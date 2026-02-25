package model;

public class FichePersonnage {

    private int idFichePersonnage;
    private String nomFichePersonnage;
    private Portrait portrait;
    private Biographie biographie;
    private Statistiques statistiques;
    private Competence competence;
    private Equipement equipement;

    public FichePersonnage(int idFichePersonnage, String nomFichePersonnage) {
        this.idFichePersonnage = idFichePersonnage;
        this.nomFichePersonnage = nomFichePersonnage;
        this.portrait = new Portrait(0, 0, 200, 200, "");
        this.biographie = new Biographie(0, 200, 400, 200, "");
        this.statistiques = new Statistiques(200, 0, 300, 200);
        this.competence = new Competence(0, 400, 300, 200);
        this.equipement = new Equipement(300, 400, 300, 200);
    }

    public void modifierPortrait(String imagePortrait) {
        this.portrait.modifierPortrait(imagePortrait);
    }

    public void modifierBiographie(String texteBiographie) {
        this.biographie.modifierBiographie(texteBiographie);
    }

    public void modifierStatistiques(Statistiques statistiques) {
        this.statistiques = statistiques;
    }

    public void modifierCompetence(Competence competence) {
        this.competence = competence;
    }

    public void modifierEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public int getIdFichePersonnage() {
        return idFichePersonnage;
    }

    public String getNomFichePersonnage() {
        return nomFichePersonnage;
    }

    public Portrait getPortrait() {
        return portrait;
    }

    public Biographie getBiographie() {
        return biographie;
    }

    public Statistiques getStatistiques() {
        return statistiques;
    }

    public Competence getCompetence() {
        return competence;
    }

    public Equipement getEquipement() {
        return equipement;
    }
}
