package model;

public class Biographie extends Module {

    private String texteBiographie;

    public Biographie(int positionX, int positionY, int largeur, int hauteur, String texteBiographie) {
        super(positionX, positionY, largeur, hauteur);
        this.texteBiographie = texteBiographie;
    }

    public void modifierBiographie(String texteBiographie) {
        this.texteBiographie = texteBiographie;
    }

    public String getTexteBiographie() {
        return texteBiographie;
    }
}
