import service.GestionUtilisateur;
import service.GestionFiche;
import service.ServeurAPI;
import model.FichePersonnage;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
        GestionFiche gestionFiche = new GestionFiche(gestionUtilisateur);

        // ============================================================
        // TESTS GESTION UTILISATEUR
        // ============================================================

        System.out.println("=== TEST 1 : Creation de comptes ===");
        gestionUtilisateur.creerCompte("Alice", "motdepasse123");
        gestionUtilisateur.creerCompte("Bob", "secret456");
        gestionUtilisateur.creerCompte("Alice", "autre"); // doit echouer (doublon)

        System.out.println("\n=== TEST 2 : Connexion ===");
        gestionUtilisateur.seConnecter("Alice", "mauvais"); // doit echouer
        gestionUtilisateur.seConnecter("Alice", "motdepasse123"); // doit reussir

        // ============================================================
        // TESTS GESTION FICHE (avec controle des droits)
        // ============================================================

        System.out.println("\n=== TEST 3 : Creation de fiches (Alice connectee) ===");
        gestionFiche.creerFiche("Guerrier des Ombres");
        gestionFiche.creerFiche("Mage de Feu");

        System.out.println("\n=== TEST 4 : Lister les fiches d'Alice ===");
        List<FichePersonnage> fichesAlice = gestionFiche.listerFiches();
        System.out.println("Nombre de fiches : " + fichesAlice.size());
        for (FichePersonnage f : fichesAlice) {
            System.out.println("  - " + f.getNomFichePersonnage() + " (id=" + f.getIdFichePersonnage() + ")");
        }

        System.out.println("\n=== TEST 5 : Modifier les modules d'une fiche ===");
        gestionFiche.modifierPortrait(1, "portrait_guerrier.png");
        gestionFiche.modifierBiographie(1, "Un guerrier sombre ne dans les tenebres.");
        gestionFiche.ajouterStatistique(1, "Force", 18);
        gestionFiche.ajouterStatistique(1, "Agilite", 14);
        gestionFiche.ajouterStatistique(1, "Intelligence", 10);
        gestionFiche.ajouterCompetence(1, "Combat a l'epee");
        gestionFiche.ajouterCompetence(1, "Furtivite");
        gestionFiche.ajouterEquipement(1, "Epee des Ombres");
        gestionFiche.ajouterEquipement(1, "Armure de cuir");

        System.out.println("\n=== TEST 6 : Modifier position/taille d'un module ===");
        gestionFiche.modifierPositionModule(1, "portrait", 50, 50);
        gestionFiche.modifierTailleModule(1, "portrait", 250, 250);

        System.out.println("\n=== TEST 7 : Modifier une statistique existante ===");
        gestionFiche.modifierStatistique(1, 1, "Force", 20);

        System.out.println("\n=== TEST 8 : Supprimer des elements ===");
        gestionFiche.supprimerCompetence(1, "Furtivite");
        gestionFiche.supprimerEquipement(1, "Armure de cuir");
        gestionFiche.supprimerStatistique(1, 3);

        System.out.println("\n=== TEST 9 : Deconnexion et test des droits ===");
        gestionUtilisateur.seDeconnecter();

        System.out.println("-- Tentative de creer une fiche sans etre connecte :");
        gestionFiche.creerFiche("Voleur"); // doit echouer

        System.out.println("-- Tentative de modifier une fiche sans etre connecte :");
        gestionFiche.modifierBiographie(1, "Pirate"); // doit echouer

        System.out.println("\n=== TEST 10 : Bob ne peut pas voir les fiches d'Alice ===");
        gestionUtilisateur.seConnecter("Bob", "secret456");
        List<FichePersonnage> fichesBob = gestionFiche.listerFiches();
        System.out.println("Fiches de Bob : " + fichesBob.size());

        System.out.println("-- Bob tente d'acceder a la fiche 1 d'Alice :");
        FichePersonnage tentative = gestionFiche.getFiche(1);
        if (tentative == null) {
            System.out.println("Acces refuse : Bob ne peut pas voir les fiches d'Alice.");
        }

        System.out.println("\n=== TEST 11 : Suppression d'une fiche ===");
        gestionUtilisateur.seDeconnecter();
        gestionUtilisateur.seConnecter("Alice", "motdepasse123");
        gestionFiche.supprimerFiche(2);
        System.out.println("Fiches restantes : " + gestionFiche.listerFiches().size());

        System.out.println("\n=== TEST 12 : Persistance ===" );
        System.out.println("Les fiches sont sauvegardees dans data/fiches_1.dat (serialisation)");
        System.out.println("Nombre d'utilisateurs sauvegardes : " + gestionUtilisateur.getUtilisateurs().size());

        gestionUtilisateur.seDeconnecter();

        // ============================================================
        // DEMARRAGE DU SERVEUR API (pour le frontend React)
        // ============================================================

        System.out.println("\n=== DEMARRAGE SERVEUR API ===");
        ServeurAPI serveur = new ServeurAPI(gestionUtilisateur, gestionFiche);
        serveur.demarrer();
        System.out.println("Endpoints disponibles :");
        System.out.println("  POST /api/signup, /api/login, /api/logout");
        System.out.println("  GET  /api/fiches, /api/fiches/{id}");
        System.out.println("  PUT  /api/fiches/{id}/portrait, /biographie, /module/position");
        System.out.println("Appuyez sur Entree pour arreter le serveur...");
        System.in.read();
        serveur.arreter();
    }
}
