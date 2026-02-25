package model;

public class Portrait extends Module {

    private String imagePortrait;

    public Portrait(int positionX, int positionY, int largeur, int hauteur, String imagePortrait) {
        super(positionX, positionY, largeur, hauteur);
        this.imagePortrait = imagePortrait;
    }

    public void modifierPortrait(String imagePortrait) {
        this.imagePortrait = imagePortrait;
    }

    public String getImagePortrait() {
        return imagePortrait;
    }
}
