package model;

public class Portrait extends Module {

    private String imagePortrait;

    // CREATION PORTRAIT
    public Portrait(int positionX, int positionY, int largeur, int hauteur, String imagePortrait) {
        super(positionX, positionY, largeur, hauteur);
        this.imagePortrait = imagePortrait;
    }

    // MODIFIER IMAGE
    public void modifierPortrait(String imagePortrait) {
        this.imagePortrait = imagePortrait;
    }

    // GET IMAGE
    public String getImagePortrait() {
        return imagePortrait;
    }
}
