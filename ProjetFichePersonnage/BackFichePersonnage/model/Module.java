package model;

import java.io.Serializable;

/**
 * Classe abstraite representant un module de fiche.
 * Implemente Serializable pour permettre la sauvegarde binaire.
 */
public abstract class Module implements Serializable {

    private static final long serialVersionUID = 1L;

    private int positionX;
    private int positionY;
    private int largeur;
    private int hauteur;

    public Module(int positionX, int positionY, int largeur, int hauteur) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void modifierPosition(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public void modifierTaille(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }
}
