package model;

public class Ennemi {

    private String nom;
    private int hp;
    private int attaque;
    private int defense;

    public Ennemi(String nom, int hp, int attaque, int defense) {
        this.nom = nom;
        this.hp = hp;
        this.attaque = attaque;
        this.defense = defense;
    }

    public String getNom() { return nom; }
    public int getHp() { return hp; }
    public int getAttaque() { return attaque; }
    public int getDefense() { return defense; }
}
