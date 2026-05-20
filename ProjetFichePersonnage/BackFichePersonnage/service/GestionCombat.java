package service;

import model.Ennemi;
import java.util.Random;

public class GestionCombat {

    private Random random = new Random();

    private static final String[][] NOMS_ENNEMIS = {
        {"Gobelin", "Rat des Egouts", "Squelette"},
        {"Orc Pillard", "Loup Sauvage", "Zombie"},
        {"Troll des Marais", "Vampyr", "Chevalier Maudit"},
        {"Demon Cornu", "Liche", "Dragon Juvenile"},
        {"Dragon Ancien", "Seigneur des Ombres", "Dieu Dechu"}
    };

    public int lancerDe(int faces) {
        return random.nextInt(faces) + 1;
    }

    // Génère un ennemi selon le niveau (1 à 5) avec des stats significativement plus hautes
    public Ennemi genererEnnemi(int niveau) {
        if (niveau < 1) niveau = 1;
        if (niveau > 5) niveau = 5;

        String[] noms = NOMS_ENNEMIS[niveau - 1];
        String nom = noms[random.nextInt(noms.length)];

        // Niveau 1 : HP ~40, ATK ~7, DEF ~5
        // Niveau 5 : HP ~160, ATK ~23, DEF ~17
        int hp      = 35 + (niveau - 1) * 30 + random.nextInt(11);
        int attaque = 5  + (niveau - 1) * 4  + random.nextInt(5);
        int defense = 3  + (niveau - 1) * 3  + random.nextInt(4);

        return new Ennemi(nom, hp, attaque, defense);
    }

    // Calcule un tour complet avec stat d'attaque ET de défense du joueur.
    // Retourne un tableau de 8 valeurs :
    // [0] dégâts infligés à l'ennemi (après critique éventuel)
    // [1] dégâts reçus par le joueur (après critique ennemi éventuel)
    // [2] dé du joueur (D20)
    // [3] dé de l'ennemi (D20)
    // [4] total attaque joueur (dé + statAttaque)
    // [5] total attaque ennemi (dé + attaqueEnnemi)
    // [6] type joueur : 2=critique, -1=echec critique, 1=normal
    // [7] type ennemi : 2=critique, 1=normal
    public int[] calculerTour(int valeurStatAttaque, int valeurStatDefense, int bonusDefense,
                               int defenseEnnemi, int attaqueEnnemi) {

        // Défense du joueur : base 10 + moitié de la stat de défense + bonus actif (Bouclier)
        int defenseJoueur = 10 + (valeurStatDefense / 2) + bonusDefense;

        // ─ Attaque du joueur ─
        int deJoueur    = lancerDe(20);
        int totalJoueur = deJoueur + valeurStatAttaque;
        int typeJoueur;
        int degatsJoueur;

        if (deJoueur == 20) {
            // Critique : dégâts doublés (minimum 1)
            degatsJoueur = Math.max(1, (totalJoueur - defenseEnnemi)) * 2;
            typeJoueur = 2;
        } else if (deJoueur == 1) {
            // Échec critique : aucun dégât
            degatsJoueur = 0;
            typeJoueur = -1;
        } else {
            degatsJoueur = Math.max(0, totalJoueur - defenseEnnemi);
            typeJoueur = 1;
        }

        // ─ Riposte de l'ennemi ─
        int deEnnemi    = lancerDe(20);
        int totalEnnemi = deEnnemi + attaqueEnnemi;
        int typeEnnemi;
        int degatsEnnemi;

        if (deEnnemi == 20) {
            // Ennemi critique aussi
            degatsEnnemi = Math.max(1, (totalEnnemi - defenseJoueur)) * 2;
            typeEnnemi = 2;
        } else {
            degatsEnnemi = Math.max(0, totalEnnemi - defenseJoueur);
            typeEnnemi = 1;
        }

        return new int[]{degatsJoueur, degatsEnnemi, deJoueur, deEnnemi,
                         totalJoueur, totalEnnemi, typeJoueur, typeEnnemi};
    }
}
