package model;

import java.io.Serializable;

public abstract class Module implements Serializable {

    private static final long serialVersionUID = 1L;

    private int positionX;
    private int positionY;
    private int largeur;
    private int hauteur;

    // CREATION MODULE
    public Module(int positionX, int positionY, int largeur, int hauteur) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    // MODIFIER POSITION
    public void modifierPosition(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    // MODIFIER TAILLE
    public void modifierTaille(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    // GET POSITIONX
    public int getPositionX() { return positionX; }

    // GET POSITIONY
    public int getPositionY() { return positionY; }

    // GET LARGEUR
    public int getLargeur() { return largeur; }

    // GET HAUTEUR
    public int getHauteur() { return hauteur; }
}
