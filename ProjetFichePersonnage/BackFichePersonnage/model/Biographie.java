package model;

public class Biographie extends Module {

    private String texteBiographie;

    // CREATION BIOGRAPHIE
    public Biographie(int positionX, int positionY, int largeur, int hauteur, String texteBiographie) {
        super(positionX, positionY, largeur, hauteur);
        this.texteBiographie = texteBiographie;
    }

    // MODIFIER TEXTE
    public void modifierBiographie(String texteBiographie) {
        this.texteBiographie = texteBiographie;
    }

    // GET TEXTE
    public String getTexteBiographie() {
        return texteBiographie;
    }
}
