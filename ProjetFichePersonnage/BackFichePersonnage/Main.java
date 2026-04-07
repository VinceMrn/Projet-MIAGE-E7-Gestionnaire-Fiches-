
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
 DEMO : Gestion en ligne de commande sans serveur 
 ============================================================
*/

/*
import service.GestionUtilisateur;
import service.GestionFiche;
import model.FichePersonnage;
import java.util.List;
import java.util.Scanner;

public class Main {

    static GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
    static GestionFiche gestionFiche = new GestionFiche(gestionUtilisateur);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Gestionnaire de Fiches de Personnages ===");

        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            if (gestionUtilisateur.getUtilisateurConnecte() != null) {
                System.out.println("Connecte : " + gestionUtilisateur.getUtilisateurConnecte().getNomUtilisateur());
            }
            System.out.println("1. Inscription");
            System.out.println("2. Connexion");
            System.out.println("3. Deconnexion");
            System.out.println("4. Creer une fiche");
            System.out.println("5. Lister mes fiches");
            System.out.println("6. Voir une fiche");
            System.out.println("7. Supprimer une fiche");
            System.out.println("8. Modifier la biographie d'une fiche");
            System.out.println("9. Ajouter une competence");
            System.out.println("10. Ajouter un equipement");
            System.out.println("11. Ajouter une statistique");
            System.out.println("0. Quitter");
            System.out.print("> ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1": inscription(); break;
                case "2": connexion(); break;
                case "3": deconnexion(); break;
                case "4": creerFiche(); break;
                case "5": listerFiches(); break;
                case "6": voirFiche(); break;
                case "7": supprimerFiche(); break;
                case "8": modifierBiographie(); break;
                case "9": ajouterCompetence(); break;
                case "10": ajouterEquipement(); break;
                case "11": ajouterStatistique(); break;
                case "0": running = false; System.out.println("Au revoir !"); break;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    static void inscription() {
        System.out.print("Nom d'utilisateur : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Mot de passe : ");
        String mdp = scanner.nextLine().trim();
        var u = gestionUtilisateur.creerCompte(nom, mdp);
        if (u != null) System.out.println("Compte cree pour " + u.getNomUtilisateur());
        else System.out.println("Erreur : nom deja pris.");
    }

    static void connexion() {
        System.out.print("Nom d'utilisateur : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Mot de passe : ");
        String mdp = scanner.nextLine().trim();
        var u = gestionUtilisateur.seConnecter(nom, mdp);
        if (u != null) {
            gestionFiche.chargerFiches(u);
            System.out.println("Connecte en tant que " + u.getNomUtilisateur());
        } else {
            System.out.println("Erreur : identifiants incorrects.");
        }
    }

    static void deconnexion() {
        gestionUtilisateur.seDeconnecter();
        System.out.println("Deconnecte.");
    }

    static void creerFiche() {
        System.out.print("Nom de la fiche : ");
        String nom = scanner.nextLine().trim();
        gestionFiche.creerFiche(nom);
    }

    static void listerFiches() {
        List<FichePersonnage> fiches = gestionFiche.listerFiches();
        if (fiches.isEmpty()) { System.out.println("Aucune fiche."); return; }
        for (FichePersonnage f : fiches) {
            System.out.println("  [" + f.getIdFichePersonnage() + "] " + f.getNomFichePersonnage());
        }
    }

    static void voirFiche() {
        System.out.print("ID de la fiche : ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        FichePersonnage f = gestionFiche.getFiche(id);
        if (f == null) return;
        System.out.println("Nom        : " + f.getNomFichePersonnage());
        System.out.println("Biographie : " + f.getBiographie().getTexteBiographie());
        System.out.println("Stats      : " + f.getStatistiques().getStatistiques());
        System.out.println("Competences: " + f.getCompetence().getCompetences());
        System.out.println("Equipements: " + f.getEquipement().getEquipements());
    }

    static void supprimerFiche() {
        System.out.print("ID de la fiche a supprimer : ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        gestionFiche.supprimerFiche(id);
    }

    static void modifierBiographie() {
        System.out.print("ID de la fiche : ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Nouvelle biographie : ");
        String texte = scanner.nextLine().trim();
        gestionFiche.modifierBiographie(id, texte);
    }

    static void ajouterCompetence() {
        System.out.print("ID de la fiche : ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Nom de la competence : ");
        String nom = scanner.nextLine().trim();
        gestionFiche.ajouterCompetence(id, nom);
    }

    static void ajouterEquipement() {
        System.out.print("ID de la fiche : ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Nom de l'equipement : ");
        String nom = scanner.nextLine().trim();
        gestionFiche.ajouterEquipement(id, nom);
    }

    static void ajouterStatistique() {
        System.out.print("ID de la fiche : ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Nom de la statistique : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Valeur : ");
        int val = Integer.parseInt(scanner.nextLine().trim());
        gestionFiche.ajouterStatistique(id, nom, val);
    }
}

*/