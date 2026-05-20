
import service.GestionUtilisateur;
import service.GestionFiche;
import service.GestionSession;
import service.ServeurAPI;

public class Main {

    public static void main(String[] args) throws Exception {
        GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
        GestionSession gestionSession = new GestionSession();
        GestionFiche gestionFiche = new GestionFiche();

        ServeurAPI serveur = new ServeurAPI(gestionUtilisateur, gestionFiche, gestionSession);
        serveur.demarrer();
        System.out.println("Appuyez sur Entree pour arreter le serveur...");
        System.in.read();
        serveur.arreter();
    }
}

// ===========

// import model.Ennemi;
// import model.FichePersonnage;
// import model.Module;
// import model.Statistique;
// import model.Utilisateur;
// import service.GestionChiffrement;
// import service.GestionCombat;
// import service.GestionFiche;
// import service.GestionSession;
// import service.GestionUtilisateur;

// import java.util.List;
// import java.util.Scanner;
// import javax.crypto.spec.SecretKeySpec;

// public class Main {

//     private static final Scanner scanner = new Scanner(System.in);
//     private static final GestionUtilisateur gestionUtilisateur = new GestionUtilisateur();
//     private static final GestionSession gestionSession = new GestionSession();
//     private static final GestionFiche gestionFiche = new GestionFiche();
//     private static final GestionCombat gestionCombat = new GestionCombat();
//     private static Utilisateur utilisateurConnecte;
//     private static String sessionId;
//     private static SecretKeySpec cleSession;
//     private static Integer ficheSelectionneeId;

//     public static void main(String[] args) {
//         boolean running = true;
//         while (running) {
//             try {
//                 afficherMenu();
//                 String choix = scanner.nextLine().trim();

//                 if (utilisateurConnecte == null) {
//                     switch (choix) {
//                         case "1": creerCompte(); break;
//                         case "2": seConnecter(); break;
//                         case "3": reinitialiserMotDePasse(); break;
//                         case "0": running = false; break;
//                         default: System.out.println("Choix invalide.");
//                     }
//                 } else {
//                     switch (choix) {
//                         case "1": afficherFiches(); break;
//                         case "2": selectionnerFiche(); break;
//                         case "3": creerFiche(); break;
//                         case "4": afficherFicheSelectionnee(); break;
//                         case "5": menuModificationFiche(); break;
//                         case "6": lancerCombatDemo(); break;
//                         case "7": menuCompte(); break;
//                         case "8": seDeconnecter(); break;
//                         case "0": running = false; break;
//                         default: System.out.println("Choix invalide.");
//                     }
//                 }
//             } catch (Exception exception) {
//                 System.out.println("Erreur : " + exception.getMessage());
//             }
//         }
//     }

//     private static void afficherMenu() {
//         System.out.println("\n=== DEMO GESTIONNAIRE ===");
//         if (utilisateurConnecte == null) {
//             System.out.println("Etat : deconnecte");
//             System.out.println("1. Creer un compte");
//             System.out.println("2. Se connecter");
//             System.out.println("3. Reinitialiser un mot de passe");
//             System.out.println("0. Quitter");
//         } else {
//             System.out.println("Connecte : " + utilisateurConnecte.getNomUtilisateur() + " (id " + utilisateurConnecte.getIdUtilisateur() + ")");
//             System.out.println("Session : " + (sessionId == null ? "aucune" : sessionId));
//             System.out.println("Fiche selectionnee : " + (ficheSelectionneeId == null ? "aucune" : ficheSelectionneeId));
//             System.out.println("Canvas : " + FichePersonnage.CANVAS_LARGEUR + " x " + FichePersonnage.CANVAS_HAUTEUR);
//             System.out.println("1. Lister mes fiches");
//             System.out.println("2. Selectionner une fiche");
//             System.out.println("3. Creer une fiche");
//             System.out.println("4. Voir la fiche selectionnee");
//             System.out.println("5. Modifier la fiche selectionnee");
//             System.out.println("6. Lancer une demo de combat");
//             System.out.println("7. Gerer le compte");
//             System.out.println("8. Se deconnecter");
//             System.out.println("0. Quitter");
//         }
//         System.out.print("> ");
//     }

//     private static void creerCompte() throws Exception {
//         System.out.print("Nom utilisateur : ");
//         String nomUtilisateur = scanner.nextLine().trim();
//         System.out.print("Mot de passe : ");
//         String motDePasse = scanner.nextLine().trim();

//         Utilisateur nouvelUtilisateur = gestionUtilisateur.creerCompte(nomUtilisateur, motDePasse);
//         System.out.println("Compte cree pour " + nouvelUtilisateur.getNomUtilisateur() + ".");

//         if (demanderOuiNon("Definir une question secrete maintenant ?")) {
//             System.out.print("Question secrete : ");
//             String question = scanner.nextLine().trim();
//             System.out.print("Reponse secrete : ");
//             String reponse = scanner.nextLine().trim();
//             gestionUtilisateur.definirQuestionSecrete(nouvelUtilisateur, question, reponse);
//             System.out.println("Question secrete enregistree.");
//         }

//         connecterUtilisateur(nouvelUtilisateur, motDePasse);
//     }

//     private static void seConnecter() throws Exception {
//         System.out.print("Nom utilisateur : ");
//         String nomUtilisateur = scanner.nextLine().trim();
//         System.out.print("Mot de passe : ");
//         String motDePasse = scanner.nextLine().trim();

//         Utilisateur utilisateur = gestionUtilisateur.seConnecter(nomUtilisateur, motDePasse);
//         connecterUtilisateur(utilisateur, motDePasse);
//     }

//     private static void connecterUtilisateur(Utilisateur utilisateur, String motDePasse) throws Exception {
//         utilisateurConnecte = utilisateur;
//         cleSession = GestionChiffrement.genererCleDepuisHash(utilisateur);
//         sessionId = gestionSession.creerSession(utilisateurConnecte, cleSession);
//         gestionFiche.chargerFiches(utilisateurConnecte, cleSession);

//         if (!utilisateurConnecte.getFiches().isEmpty()) {
//             ficheSelectionneeId = utilisateurConnecte.getFiches().get(0).getIdFichePersonnage();
//         } else {
//             ficheSelectionneeId = null;
//         }

//         System.out.println("Connecte avec la session " + sessionId + ".");
//     }

//     private static void seDeconnecter() {
//         if (sessionId != null) {
//             gestionSession.supprimerSession(sessionId);
//         }
//         utilisateurConnecte = null;
//         sessionId = null;
//         cleSession = null;
//         ficheSelectionneeId = null;
//         System.out.println("Deconnecte.");
//     }

//     private static void reinitialiserMotDePasse() {
//         System.out.print("Nom utilisateur : ");
//         String nomUtilisateur = scanner.nextLine().trim();
//         String question = gestionUtilisateur.getQuestionSecrete(nomUtilisateur);
//         if (question == null) {
//             System.out.println("Aucune question secrete trouvee.");
//             return;
//         }

//         System.out.println("Question : " + question);
//         System.out.print("Reponse : ");
//         String reponse = scanner.nextLine().trim();
//         System.out.print("Nouveau mot de passe : ");
//         String nouveauMotDePasse = scanner.nextLine().trim();

//         if (gestionUtilisateur.reinitialiserMotDePasseAvecQuestionSecrete(nomUtilisateur, reponse, nouveauMotDePasse)) {
//             System.out.println("Mot de passe reinitialise.");
//         } else {
//             System.out.println("Impossible de reinitialiser le mot de passe.");
//         }
//     }

//     private static void afficherFiches() {
//         List<FichePersonnage> fiches = gestionFiche.listerFiches(utilisateurConnecte);
//         if (fiches.isEmpty()) {
//             System.out.println("Aucune fiche.");
//             return;
//         }

//         System.out.println("\n--- MES FICHES ---");
//         for (FichePersonnage fiche : fiches) {
//             System.out.println("- [" + fiche.getIdFichePersonnage() + "] " + fiche.getNomFichePersonnage());
//         }
//     }

//     private static void selectionnerFiche() {
//         afficherFiches();
//         if (utilisateurConnecte.getFiches().isEmpty()) {
//             return;
//         }

//         int idFiche = lireEntier("Id de la fiche", utilisateurConnecte.getFiches().get(0).getIdFichePersonnage());
//         if (gestionFiche.getFiche(utilisateurConnecte, idFiche) == null) {
//             System.out.println("Fiche introuvable.");
//             return;
//         }

//         ficheSelectionneeId = idFiche;
//         System.out.println("Fiche selectionnee : " + idFiche);
//     }

//     private static void creerFiche() {
//         System.out.print("Nom de la fiche : ");
//         String nomFiche = scanner.nextLine().trim();
//         FichePersonnage fiche = gestionFiche.creerFiche(utilisateurConnecte, cleSession, nomFiche);
//         ficheSelectionneeId = fiche.getIdFichePersonnage();
//         System.out.println("Fiche creee avec l'id " + ficheSelectionneeId + ".");
//     }

//     private static void afficherFicheSelectionnee() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         System.out.println("\n--- FICHE SELECTIONNEE ---");
//         System.out.println("Nom : " + fiche.getNomFichePersonnage());
//         afficherModule("Portrait", fiche.getPortrait());
//         System.out.println("   image = " + fiche.getPortrait().getImagePortrait());
//         afficherModule("Biographie", fiche.getBiographie());
//         System.out.println("   texte = " + fiche.getBiographie().getTexteBiographie());
//         afficherModule("Statistiques", fiche.getStatistiques());
//         afficherStatistiques(fiche.getStatistiques().getStatistiques());
//         afficherModule("Competence", fiche.getCompetence());
//         System.out.println("   competences = " + fiche.getCompetence().getCompetences());
//         afficherModule("Equipement", fiche.getEquipement());
//         System.out.println("   equipements = " + fiche.getEquipement().getEquipements());

//         // Custom modules display removed
//     }

//     private static void menuModificationFiche() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         System.out.println("1. Modifier le portrait");
//         System.out.println("2. Modifier la biographie");
//         System.out.println("3. Ajouter une statistique");
//         System.out.println("4. Ajouter une competence");
//         System.out.println("5. Ajouter un equipement");
//         System.out.println("6. Deplacer un module");
//         System.out.println("7. Redimensionner un module");
//         System.out.print("> ");

//         switch (scanner.nextLine().trim()) {
//             case "1":
//                 System.out.print("Image portrait : ");
//                 if (gestionFiche.modifierPortrait(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage(), scanner.nextLine().trim())) {
//                     System.out.println("Portrait mis a jour.");
//                 } else {
//                     System.out.println("Modification impossible.");
//                 }
//                 break;
//             case "2":
//                 System.out.print("Texte biographie : ");
//                 if (gestionFiche.modifierBiographie(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage(), scanner.nextLine().trim())) {
//                     System.out.println("Biographie mise a jour.");
//                 } else {
//                     System.out.println("Modification impossible.");
//                 }
//                 break;
//             case "3":
//                 System.out.print("Nom statistique : ");
//                 String nomStatistique = scanner.nextLine().trim();
//                 int valeurStatistique = lireEntier("Valeur", 0);
//                 if (gestionFiche.ajouterStatistique(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage(), nomStatistique, valeurStatistique)) {
//                     System.out.println("Statistique ajoutee.");
//                 } else {
//                     System.out.println("Ajout impossible.");
//                 }
//                 break;
//             case "4":
//                 System.out.print("Nom competence : ");
//                 if (gestionFiche.ajouterCompetence(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage(), scanner.nextLine().trim())) {
//                     System.out.println("Competence ajoutee.");
//                 } else {
//                     System.out.println("Ajout impossible.");
//                 }
//                 break;
//             case "5":
//                 System.out.print("Nom equipement : ");
//                 if (gestionFiche.ajouterEquipement(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage(), scanner.nextLine().trim())) {
//                     System.out.println("Equipement ajoute.");
//                 } else {
//                     System.out.println("Ajout impossible.");
//                 }
//                 break;
//             case "6":
//                 deplacerModule();
//                 break;
//             case "7":
//                 redimensionnerModule();
//                 break;
//             default:
//                 System.out.println("Choix invalide.");
//         }
//     }

//     private static void deplacerModule() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         String nomModule = choisirNomModule();
//         if (nomModule == null) {
//             return;
//         }

//         int x = lireEntier("Nouvelle position X", 0);
//         int y = lireEntier("Nouvelle position Y", 0);
//         boolean ok = gestionFiche.modifierPositionModule(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage(), nomModule, x, y);
//         System.out.println(ok ? "Position mise a jour." : "Position refusee par les contraintes du canvas.");
//     }

//     private static void redimensionnerModule() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         String nomModule = choisirNomModule();
//         if (nomModule == null) {
//             return;
//         }

//         int largeur = lireEntier("Nouvelle largeur", 300);
//         int hauteur = lireEntier("Nouvelle hauteur", 200);
//         boolean ok = gestionFiche.modifierTailleModule(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage(), nomModule, largeur, hauteur);
//         System.out.println(ok ? "Taille mise a jour." : "Taille refusee par les contraintes du canvas.");
//     }

//     private static void menuCompte() throws Exception {
//         System.out.println("1. Modifier le nom d'utilisateur");
//         System.out.println("2. Modifier le mot de passe");
//         System.out.println("3. Definir ou modifier la question secrete");
//         System.out.print("> ");

//         switch (scanner.nextLine().trim()) {
//             case "1":
//                 System.out.print("Nouveau nom : ");
//                 if (gestionUtilisateur.modifierIdentifiant(utilisateurConnecte, scanner.nextLine().trim())) {
//                     System.out.println("Nom modifie.");
//                 } else {
//                     System.out.println("Modification impossible.");
//                 }
//                 break;
//             case "2":
//                 System.out.print("Ancien mot de passe : ");
//                 String ancienMotDePasse = scanner.nextLine().trim();
//                 System.out.print("Nouveau mot de passe : ");
//                 String nouveauMotDePasse = scanner.nextLine().trim();
//                 if (gestionUtilisateur.modifierMotDePasse(utilisateurConnecte, ancienMotDePasse, nouveauMotDePasse)) {
//                     cleSession = GestionChiffrement.genererCleDepuisHash(utilisateurConnecte);
//                     if (sessionId != null) {
//                         gestionSession.supprimerSession(sessionId);
//                     }
//                     sessionId = gestionSession.creerSession(utilisateurConnecte, cleSession);
//                     System.out.println("Mot de passe modifie.");
//                 } else {
//                     System.out.println("Modification impossible.");
//                 }
//                 break;
//             case "3":
//                 System.out.print("Question secrete : ");
//                 String question = scanner.nextLine().trim();
//                 System.out.print("Reponse secrete : ");
//                 String reponse = scanner.nextLine().trim();
//                 if (gestionUtilisateur.definirQuestionSecrete(utilisateurConnecte, question, reponse)) {
//                     System.out.println("Question secrete enregistree.");
//                 } else {
//                     System.out.println("Modification impossible.");
//                 }
//                 break;
//             default:
//                 System.out.println("Choix invalide.");
//         }
//     }

//     private static void lancerCombatDemo() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         System.out.println("\n--- DEMO COMBAT ---");
//         int niveau = lireEntier("Niveau ennemi (1-5)", 1);
//         Ennemi ennemi = gestionCombat.genererEnnemi(niveau);

//         int attaque = lireStatistique(fiche, "Attaque", 12);
//         int defense = lireStatistique(fiche, "Defense", 8);
//         int bonusDefense = fiche.getEquipement().getEquipements().size();

//         int[] resultat = gestionCombat.calculerTour(attaque, defense, bonusDefense, ennemi.getDefense(), ennemi.getAttaque());

//         System.out.println("Ennemi genere : " + ennemi.getNom()
//                 + " | HP=" + ennemi.getHp()
//                 + " | ATK=" + ennemi.getAttaque()
//                 + " | DEF=" + ennemi.getDefense());
//         System.out.println("Joueur : attaque=" + attaque + " defense=" + defense + " bonus=" + bonusDefense);
//         System.out.println("Degats joueur -> ennemi : " + resultat[0]);
//         System.out.println("Degats ennemi -> joueur : " + resultat[1]);
//         System.out.println("De joueur / ennemi : " + resultat[2] + " / " + resultat[3]);
//         System.out.println("Total joueur / ennemi : " + resultat[4] + " / " + resultat[5]);
//         System.out.println("Type joueur / ennemi : " + typeJet(resultat[6]) + " / " + typeJet(resultat[7]));
//     }

//     private static String typeJet(int type) {
//         switch (type) {
//             case 2: return "critique";
//             case 1: return "normal";
//             case -1: return "echec critique";
//             default: return "inconnu";
//         }
//     }

//     private static FichePersonnage ficheCourante() {
//         if (utilisateurConnecte == null) {
//             System.out.println("Aucun compte connecte.");
//             return null;
//         }

//         if (ficheSelectionneeId == null) {
//             System.out.println("Aucune fiche selectionnee.");
//             return null;
//         }

//         FichePersonnage fiche = gestionFiche.getFiche(utilisateurConnecte, ficheSelectionneeId);
//         if (fiche == null) {
//             System.out.println("Fiche introuvable.");
//         }
//         return fiche;
//     }

//     private static String choisirNomModule() {
//         System.out.println("1. Portrait");
//         System.out.println("2. Biographie");
//         System.out.println("3. Statistiques");
//         System.out.println("4. Competence");
//         System.out.println("5. Equipement");
//         System.out.print("> ");

//         switch (scanner.nextLine().trim()) {
//             case "1": return "portrait";
//             case "2": return "biographie";
//             case "3": return "statistiques";
//             case "4": return "competence";
//             case "5": return "equipement";
//             default:
//                 System.out.println("Choix invalide.");
//                 return null;
//         }
//     }

//     private static void afficherModule(String nom, Module module) {
//         System.out.println(nom + " | x=" + module.getPositionX()
//                 + " y=" + module.getPositionY()
//                 + " | l=" + module.getLargeur()
//                 + " h=" + module.getHauteur());
//     }

//     private static void afficherStatistiques(List<Statistique> statistiques) {
//         if (statistiques.isEmpty()) {
//             System.out.println("   aucune statistique.");
//             return;
//         }

//         for (Statistique statistique : statistiques) {
//             System.out.println("   - " + statistique.getNomStatistique() + " = " + statistique.getValeurStatistique());
//         }
//     }

//     private static int lireEntier(String libelle, int valeurDefaut) {
//         while (true) {
//             System.out.print(libelle + " [" + valeurDefaut + "] : ");
//             String entree = scanner.nextLine().trim();
//             if (entree.isEmpty()) {
//                 return valeurDefaut;
//             }

//             try {
//                 return Integer.parseInt(entree);
//             } catch (NumberFormatException exception) {
//                 System.out.println("Veuillez entrer un nombre valide.");
//             }
//         }
//     }

//     private static int lireStatistique(FichePersonnage fiche, String nomStatistique, int valeurDefaut) {
//         for (Statistique statistique : fiche.getStatistiques().getStatistiques()) {
//             if (statistique.getNomStatistique().equalsIgnoreCase(nomStatistique)) {
//                 return statistique.getValeurStatistique();
//             }
//         }
//         return valeurDefaut;
//     }

//     private static boolean demanderOuiNon(String question) {
//         System.out.print(question + " (o/n) : ");
//         String reponse = scanner.nextLine().trim().toLowerCase();
//         return reponse.startsWith("o");
//     }
// }

