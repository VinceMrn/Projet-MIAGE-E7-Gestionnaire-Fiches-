
import service.GestionUtilisateur;
import service.GestionFiche;
import service.ServeurAPI;

public class Main {

    public static void main(String[] args) throws Exception {
        GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
        GestionFiche gestionFiche = new GestionFiche(gestionUtilisateur);

        ServeurAPI serveur = new ServeurAPI(gestionUtilisateur, gestionFiche);
        serveur.demarrer();
        System.out.println("Appuyez sur Entree pour arreter le serveur...");
        System.in.read();
        serveur.arreter();
    }
}


/*
 ============================================================
 DEMO : Gestion spatiale des modules en ligne de commande
 ============================================================

import model.FichePersonnage;
import model.Module;
import model.Sauvegarde;

import java.util.Scanner;

public class Main {

    static FichePersonnage fiche = new FichePersonnage(1, "Test");
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        boolean running = true;
        while (running) {
            System.out.println("\n--- GESTION SPATIALE DES MODULES ---");
            System.out.println("1. Afficher l'etat des modules");
            System.out.println("2. Deplacer un module");
            System.out.println("3. Redimensionner un module");
            System.out.println("4. Sauvegarder");
            System.out.println("5. Recharger");
            System.out.println("0. Quitter");
            System.out.print("> ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1": afficher(); break;
                case "2": deplacer(); break;
                case "3": redimensionner(); break;
                case "4": sauvegarder(); break;
                case "5": recharger(); break;
                case "0": running = false; break;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    static Module choisirModule() {
        System.out.println("1. Portrait  2. Biographie  3. Statistiques  4. Competence  5. Equipement");
        System.out.print("> ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": return fiche.getPortrait();
            case "2": return fiche.getBiographie();
            case "3": return fiche.getStatistiques();
            case "4": return fiche.getCompetence();
            case "5": return fiche.getEquipement();
            default: return null;
        }
    }

    static void afficher() {
        afficherModule("Portrait     ", fiche.getPortrait());
        afficherModule("Biographie   ", fiche.getBiographie());
        afficherModule("Statistiques ", fiche.getStatistiques());
        afficherModule("Competence   ", fiche.getCompetence());
        afficherModule("Equipement   ", fiche.getEquipement());
    }

    static void afficherModule(String nom, Module m) {
        System.out.println(nom + " | x=" + m.getPositionX() + " y=" + m.getPositionY()
                + " | l=" + m.getLargeur() + " h=" + m.getHauteur());
    }

    static void deplacer() {
        Module m = choisirModule();
        if (m == null) { System.out.println("Module invalide."); return; }
        System.out.print("x : "); int x = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("y : "); int y = Integer.parseInt(scanner.nextLine().trim());
        m.modifierPosition(x, y);
    }

    static void redimensionner() {
        Module m = choisirModule();
        if (m == null) { System.out.println("Module invalide."); return; }
        System.out.print("largeur : "); int l = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("hauteur : "); int h = Integer.parseInt(scanner.nextLine().trim());
        m.modifierTaille(l, h);
    }

    static void sauvegarder() throws Exception {
        System.out.print("Chemin : ");
        String chemin = scanner.nextLine().trim();
        Sauvegarde.sauvegarder(fiche, chemin);
        System.out.println("Sauvegarde OK.");
    }

    static void recharger() throws Exception {
        System.out.print("Chemin : ");
        String chemin = scanner.nextLine().trim();
        fiche = Sauvegarde.charger(chemin);
        System.out.println("Chargement OK.");
    }
}
*/
