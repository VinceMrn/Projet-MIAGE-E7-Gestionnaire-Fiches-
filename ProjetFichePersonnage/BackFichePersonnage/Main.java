

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

//     private static final int HP_JOUEUR_DEFAUT = 100;

//     private static Utilisateur utilisateurConnecte;
//     private static String sessionId;
//     private static SecretKeySpec cleSession;
//     private static Integer ficheSelectionneeId;

//     public static void main(String[] args) {
//         System.out.println("===============================================");
//         System.out.println("   DEMO GESTIONNAIRE DE FICHES PERSONNAGES");
//         System.out.println("===============================================");

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
//                         case "6": supprimerFicheSelectionnee(); break;
//                         case "7": listerDonneesGlobales(); break;
//                         case "8": lancerCombatDemo(); break;
//                         case "9": menuCompte(); break;
//                         case "10": seDeconnecter(); break;
//                         case "0": running = false; break;
//                         default: System.out.println("Choix invalide.");
//                     }
//                 }
//             } catch (Exception exception) {
//                 System.out.println("Erreur : " + exception.getMessage());
//             }
//         }

//         if (utilisateurConnecte != null) {
//             seDeconnecter();
//         }
//         System.out.println("A bientot !");
//     }

//     private static void afficherMenu() {
//         System.out.println();
//         if (utilisateurConnecte == null) {
//             System.out.println("--- MENU PRINCIPAL (deconnecte) ---");
//             System.out.println("1. Creer un compte");
//             System.out.println("2. Se connecter");
//             System.out.println("3. Reinitialiser un mot de passe");
//             System.out.println("0. Quitter");
//         } else {
//             System.out.println("--- MENU PRINCIPAL ---");
//             System.out.println("Connecte : " + utilisateurConnecte.getNomUtilisateur()
//                     + " (id " + utilisateurConnecte.getIdUtilisateur() + ")");
//             System.out.println("Session  : " + sessionId);
//             System.out.println("Fiche    : " + (ficheSelectionneeId == null ? "aucune" : ficheSelectionneeId));
//             System.out.println("Canvas   : " + FichePersonnage.CANVAS_LARGEUR + " x " + FichePersonnage.CANVAS_HAUTEUR);
//             System.out.println("---------------------------");
//             System.out.println("1.  Lister mes fiches");
//             System.out.println("2.  Selectionner une fiche");
//             System.out.println("3.  Creer une fiche");
//             System.out.println("4.  Voir la fiche selectionnee");
//             System.out.println("5.  Modifier la fiche selectionnee");
//             System.out.println("6.  Supprimer la fiche selectionnee");
//             System.out.println("7.  Lister mes donnees (toutes fiches)");
//             System.out.println("8.  Lancer une demo de combat");
//             System.out.println("9.  Gerer le compte");
//             System.out.println("10. Se deconnecter");
//             System.out.println("0.  Quitter");
//         }
//         System.out.print("> ");
//     }

//     // ==================== AUTHENTIFICATION ====================

//     private static void creerCompte() throws Exception {
//         System.out.print("Nom utilisateur : ");
//         String nomUtilisateur = scanner.nextLine().trim();
//         System.out.print("Mot de passe : ");
//         String motDePasse = scanner.nextLine().trim();

//         Utilisateur nouvelUtilisateur = gestionUtilisateur.creerCompte(nomUtilisateur, motDePasse);
//         System.out.println("Compte cree pour " + nouvelUtilisateur.getNomUtilisateur() + ".");

//         if (demanderOuiNon("Definir une question secrete maintenant ?")) {
//             definirQuestionSecrete(nouvelUtilisateur);
//         }

//         connecterUtilisateur(nouvelUtilisateur);
//     }

//     private static void seConnecter() throws Exception {
//         System.out.print("Nom utilisateur : ");
//         String nomUtilisateur = scanner.nextLine().trim();
//         System.out.print("Mot de passe : ");
//         String motDePasse = scanner.nextLine().trim();

//         Utilisateur utilisateur = gestionUtilisateur.seConnecter(nomUtilisateur, motDePasse);
//         connecterUtilisateur(utilisateur);
//     }

//     private static void connecterUtilisateur(Utilisateur utilisateur) throws Exception {
//         utilisateurConnecte = utilisateur;
//         cleSession = GestionChiffrement.genererCleDepuisHash(utilisateur);
//         sessionId = gestionSession.creerSession(utilisateurConnecte, cleSession);
//         gestionFiche.chargerFiches(utilisateurConnecte, cleSession);

//         if (!utilisateurConnecte.getFiches().isEmpty()) {
//             ficheSelectionneeId = utilisateurConnecte.getFiches().get(0).getIdFichePersonnage();
//         } else {
//             ficheSelectionneeId = null;
//         }

//         System.out.println("Connecte (session " + sessionId + ").");
//         System.out.println("Fiches chargees : " + utilisateurConnecte.getFiches().size());
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
//             System.out.println("Aucune question secrete trouvee pour cet utilisateur.");
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
//             System.out.println("Impossible de reinitialiser le mot de passe (reponse incorrecte ?).");
//         }
//     }

//     // ==================== FICHES ====================

//     private static void afficherFiches() {
//         List<FichePersonnage> fiches = gestionFiche.listerFiches(utilisateurConnecte);
//         if (fiches.isEmpty()) {
//             System.out.println("Aucune fiche.");
//             return;
//         }

//         System.out.println("--- MES FICHES (" + fiches.size() + ") ---");
//         for (FichePersonnage fiche : fiches) {
//             String marqueur = (ficheSelectionneeId != null && fiche.getIdFichePersonnage() == ficheSelectionneeId) ? " *" : "";
//             System.out.println("- [" + fiche.getIdFichePersonnage() + "] " + fiche.getNomFichePersonnage() + marqueur);
//         }
//     }

//     private static void selectionnerFiche() {
//         afficherFiches();
//         if (utilisateurConnecte.getFiches().isEmpty()) {
//             return;
//         }

//         int defaut = utilisateurConnecte.getFiches().get(0).getIdFichePersonnage();
//         int idFiche = lireEntier("Id de la fiche", defaut);
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
//         if (nomFiche.isEmpty()) {
//             System.out.println("Nom vide, creation annulee.");
//             return;
//         }
//         FichePersonnage fiche = gestionFiche.creerFiche(utilisateurConnecte, cleSession, nomFiche);
//         ficheSelectionneeId = fiche.getIdFichePersonnage();
//         System.out.println("Fiche creee avec l'id " + ficheSelectionneeId + ".");
//     }

//     private static void supprimerFicheSelectionnee() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }
//         if (!demanderOuiNon("Supprimer definitivement la fiche " + fiche.getNomFichePersonnage() + " ?")) {
//             System.out.println("Suppression annulee.");
//             return;
//         }
//         if (gestionFiche.supprimerFiche(utilisateurConnecte, cleSession, fiche.getIdFichePersonnage())) {
//             System.out.println("Fiche supprimee.");
//             List<FichePersonnage> restantes = utilisateurConnecte.getFiches();
//             ficheSelectionneeId = restantes.isEmpty() ? null : restantes.get(0).getIdFichePersonnage();
//         } else {
//             System.out.println("Suppression impossible.");
//         }
//     }

//     private static void afficherFicheSelectionnee() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         System.out.println("--- FICHE [" + fiche.getIdFichePersonnage() + "] " + fiche.getNomFichePersonnage() + " ---");
//         afficherModule("Portrait    ", fiche.getPortrait());
//         System.out.println("   image = " + valeurOuVide(fiche.getPortrait().getImagePortrait()));
//         afficherModule("Biographie  ", fiche.getBiographie());
//         System.out.println("   texte = " + valeurOuVide(fiche.getBiographie().getTexteBiographie()));
//         afficherModule("Statistiques", fiche.getStatistiques());
//         afficherStatistiques(fiche.getStatistiques().getStatistiques());
//         afficherModule("Competence  ", fiche.getCompetence());
//         afficherListe(fiche.getCompetence().getCompetences());
//         afficherModule("Equipement  ", fiche.getEquipement());
//         afficherListe(fiche.getEquipement().getEquipements());
//     }

//     // ==================== MODIFICATION FICHE ====================

//     private static void menuModificationFiche() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         boolean continuer = true;
//         while (continuer) {
//             System.out.println();
//             System.out.println("--- MODIFIER FICHE [" + fiche.getIdFichePersonnage() + "] " + fiche.getNomFichePersonnage() + " ---");
//             System.out.println("1. Modifier le portrait (image)");
//             System.out.println("2. Modifier la biographie (texte)");
//             System.out.println("3. Ajouter une statistique");
//             System.out.println("4. Ajouter une competence");
//             System.out.println("5. Ajouter un equipement");
//             System.out.println("6. Deplacer un module");
//             System.out.println("7. Redimensionner un module");
//             System.out.println("0. Retour");
//             System.out.print("> ");

//             String choix = scanner.nextLine().trim();
//             int idFiche = fiche.getIdFichePersonnage();
//             switch (choix) {
//                 case "1":
//                     System.out.print("Image portrait : ");
//                     afficherResultat(gestionFiche.modifierPortrait(utilisateurConnecte, cleSession, idFiche, scanner.nextLine().trim()),
//                             "Portrait mis a jour.", "Modification impossible.");
//                     break;
//                 case "2":
//                     System.out.print("Texte biographie : ");
//                     afficherResultat(gestionFiche.modifierBiographie(utilisateurConnecte, cleSession, idFiche, scanner.nextLine().trim()),
//                             "Biographie mise a jour.", "Modification impossible.");
//                     break;
//                 case "3":
//                     System.out.print("Nom statistique : ");
//                     String nomStatistique = scanner.nextLine().trim();
//                     int valeurStatistique = lireEntier("Valeur", 10);
//                     afficherResultat(gestionFiche.ajouterStatistique(utilisateurConnecte, cleSession, idFiche, nomStatistique, valeurStatistique),
//                             "Statistique ajoutee.", "Ajout impossible.");
//                     break;
//                 case "4":
//                     System.out.print("Nom competence : ");
//                     afficherResultat(gestionFiche.ajouterCompetence(utilisateurConnecte, cleSession, idFiche, scanner.nextLine().trim()),
//                             "Competence ajoutee.", "Ajout impossible.");
//                     break;
//                 case "5":
//                     System.out.print("Nom equipement : ");
//                     afficherResultat(gestionFiche.ajouterEquipement(utilisateurConnecte, cleSession, idFiche, scanner.nextLine().trim()),
//                             "Equipement ajoute.", "Ajout impossible.");
//                     break;
//                 case "6":
//                     deplacerModule(idFiche);
//                     break;
//                 case "7":
//                     redimensionnerModule(idFiche);
//                     break;
//                 case "0":
//                     continuer = false;
//                     break;
//                 default:
//                     System.out.println("Choix invalide.");
//             }
//         }
//     }

//     private static void deplacerModule(int idFiche) {
//         String nomModule = choisirNomModule();
//         if (nomModule == null) {
//             return;
//         }
//         int x = lireEntier("Nouvelle position X", 0);
//         int y = lireEntier("Nouvelle position Y", 0);
//         boolean ok = gestionFiche.modifierPositionModule(utilisateurConnecte, cleSession, idFiche, nomModule, x, y);
//         System.out.println(ok ? "Position mise a jour." : "Position refusee (hors canvas ou chevauchement).");
//     }

//     private static void redimensionnerModule(int idFiche) {
//         String nomModule = choisirNomModule();
//         if (nomModule == null) {
//             return;
//         }
//         int largeur = lireEntier("Nouvelle largeur", 300);
//         int hauteur = lireEntier("Nouvelle hauteur", 200);
//         boolean ok = gestionFiche.modifierTailleModule(utilisateurConnecte, cleSession, idFiche, nomModule, largeur, hauteur);
//         System.out.println(ok ? "Taille mise a jour." : "Taille refusee (hors canvas ou chevauchement).");
//     }

//     // ==================== DONNEES GLOBALES ====================

//     private static void listerDonneesGlobales() {
//         List<Statistique> statistiques = gestionFiche.listerStatistiquesUtilisateur(utilisateurConnecte);
//         List<String> competences = gestionFiche.listerCompetencesUtilisateur(utilisateurConnecte);
//         List<String> equipements = gestionFiche.listerEquipementsUtilisateur(utilisateurConnecte);

//         System.out.println("--- DONNEES AGREGEES (toutes fiches) ---");
//         System.out.println("Statistiques uniques (" + statistiques.size() + ") :");
//         afficherStatistiques(statistiques);
//         System.out.println("Competences uniques (" + competences.size() + ") :");
//         afficherListe(competences);
//         System.out.println("Equipements uniques (" + equipements.size() + ") :");
//         afficherListe(equipements);
//     }

//     // ==================== COMPTE ====================

//     private static void menuCompte() throws Exception {
//         boolean continuer = true;
//         while (continuer) {
//             System.out.println();
//             System.out.println("--- GESTION DU COMPTE ---");
//             System.out.println("1. Modifier le nom d'utilisateur");
//             System.out.println("2. Modifier le mot de passe");
//             System.out.println("3. Definir / modifier la question secrete");
//             System.out.println("0. Retour");
//             System.out.print("> ");

//             switch (scanner.nextLine().trim()) {
//                 case "1":
//                     System.out.print("Nouveau nom : ");
//                     afficherResultat(gestionUtilisateur.modifierIdentifiant(utilisateurConnecte, scanner.nextLine().trim()),
//                             "Nom modifie.", "Modification impossible (nom deja pris ?).");
//                     break;
//                 case "2":
//                     System.out.print("Ancien mot de passe : ");
//                     String ancien = scanner.nextLine().trim();
//                     System.out.print("Nouveau mot de passe : ");
//                     String nouveau = scanner.nextLine().trim();
//                     if (gestionUtilisateur.modifierMotDePasse(utilisateurConnecte, ancien, nouveau)) {
//                         cleSession = GestionChiffrement.genererCleDepuisHash(utilisateurConnecte);
//                         if (sessionId != null) {
//                             gestionSession.supprimerSession(sessionId);
//                         }
//                         sessionId = gestionSession.creerSession(utilisateurConnecte, cleSession);
//                         System.out.println("Mot de passe modifie (nouvelle cle de session generee).");
//                     } else {
//                         System.out.println("Modification impossible (ancien mot de passe incorrect ?).");
//                     }
//                     break;
//                 case "3":
//                     definirQuestionSecrete(utilisateurConnecte);
//                     break;
//                 case "0":
//                     continuer = false;
//                     break;
//                 default:
//                     System.out.println("Choix invalide.");
//             }
//         }
//     }

//     private static void definirQuestionSecrete(Utilisateur utilisateur) {
//         System.out.print("Question secrete (min 10 caracteres) : ");
//         String question = scanner.nextLine().trim();
//         System.out.print("Reponse secrete : ");
//         String reponse = scanner.nextLine().trim();
//         afficherResultat(gestionUtilisateur.definirQuestionSecrete(utilisateur, question, reponse),
//                 "Question secrete enregistree.", "Enregistrement impossible (question trop courte ou contient ';' ?).");
//     }

//     // ==================== COMBAT ====================

//     private static void lancerCombatDemo() {
//         FichePersonnage fiche = ficheCourante();
//         if (fiche == null) {
//             return;
//         }

//         System.out.println();
//         System.out.println("==============================");
//         System.out.println("        DEMO COMBAT");
//         System.out.println("==============================");

//         int niveau = lireEntier("Niveau ennemi (1-5)", 1);
//         Ennemi ennemi = gestionCombat.genererEnnemi(niveau);

//         int attaque = lireStatistique(fiche, "Attaque", 12);
//         int defense = lireStatistique(fiche, "Defense", 8);
//         int bonusDefense = fiche.getEquipement().getEquipements().size();

//         int hpJoueur = HP_JOUEUR_DEFAUT;
//         int hpEnnemi = ennemi.getHp();

//         System.out.println("Joueur : " + fiche.getNomFichePersonnage()
//                 + " | HP=" + hpJoueur
//                 + " | ATK=" + attaque
//                 + " | DEF=" + defense
//                 + " | bonus equipements=" + bonusDefense);
//         System.out.println("Ennemi : " + ennemi.getNom()
//                 + " | HP=" + ennemi.getHp()
//                 + " | ATK=" + ennemi.getAttaque()
//                 + " | DEF=" + ennemi.getDefense());
//         System.out.println();

//         boolean autoMode = demanderOuiNon("Lancer le combat en automatique (sinon tour par tour) ?");

//         int numeroTour = 1;
//         while (hpJoueur > 0 && hpEnnemi > 0) {
//             System.out.println("--- Tour " + numeroTour + " ---");
//             int[] resultat = gestionCombat.calculerTour(attaque, defense, bonusDefense,
//                     ennemi.getDefense(), ennemi.getAttaque());

//             int degatsJoueur = resultat[0];
//             int degatsEnnemi = resultat[1];
//             int deJoueur = resultat[2];
//             int deEnnemi = resultat[3];
//             int totalJoueur = resultat[4];
//             int totalEnnemi = resultat[5];
//             int typeJoueur = resultat[6];
//             int typeEnnemi = resultat[7];

//             System.out.println("Joueur jette D20=" + deJoueur + " (+" + attaque + " = " + totalJoueur + ") -> "
//                     + typeJet(typeJoueur) + " | degats infliges : " + degatsJoueur);
//             hpEnnemi = Math.max(0, hpEnnemi - degatsJoueur);
//             System.out.println("HP ennemi : " + hpEnnemi + " / " + ennemi.getHp());

//             if (hpEnnemi <= 0) {
//                 System.out.println();
//                 System.out.println(">>> VICTOIRE ! " + ennemi.getNom() + " est vaincu en " + numeroTour + " tour(s).");
//                 return;
//             }

//             System.out.println("Ennemi jette D20=" + deEnnemi + " (+" + ennemi.getAttaque() + " = " + totalEnnemi + ") -> "
//                     + typeJet(typeEnnemi) + " | degats subis  : " + degatsEnnemi);
//             hpJoueur = Math.max(0, hpJoueur - degatsEnnemi);
//             System.out.println("HP joueur : " + hpJoueur + " / " + HP_JOUEUR_DEFAUT);

//             if (hpJoueur <= 0) {
//                 System.out.println();
//                 System.out.println(">>> DEFAITE ! Vous etes terrasse par " + ennemi.getNom() + " apres " + numeroTour + " tour(s).");
//                 return;
//             }

//             numeroTour++;
//             if (!autoMode) {
//                 System.out.print("[Entree] pour le tour suivant, ou 'q' pour arreter : ");
//                 if (scanner.nextLine().trim().equalsIgnoreCase("q")) {
//                     System.out.println("Combat interrompu.");
//                     return;
//                 }
//             }
//         }
//     }

//     private static String typeJet(int type) {
//         switch (type) {
//             case 2: return "critique";
//             case 1: return "normal";
//             case -1: return "echec critique";
//             default: return "inconnu";
//         }
//     }

//     // ==================== HELPERS ====================

//     private static FichePersonnage ficheCourante() {
//         if (utilisateurConnecte == null) {
//             System.out.println("Aucun compte connecte.");
//             return null;
//         }
//         if (ficheSelectionneeId == null) {
//             System.out.println("Aucune fiche selectionnee. Creez ou selectionnez une fiche.");
//             return null;
//         }
//         FichePersonnage fiche = gestionFiche.getFiche(utilisateurConnecte, ficheSelectionneeId);
//         if (fiche == null) {
//             System.out.println("Fiche introuvable.");
//             ficheSelectionneeId = null;
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
//             System.out.println("   (aucune)");
//             return;
//         }
//         for (Statistique statistique : statistiques) {
//             System.out.println("   - " + statistique.getNomStatistique() + " = " + statistique.getValeurStatistique());
//         }
//     }

//     private static void afficherListe(List<String> elements) {
//         if (elements.isEmpty()) {
//             System.out.println("   (aucun)");
//             return;
//         }
//         for (String element : elements) {
//             System.out.println("   - " + element);
//         }
//     }

//     private static void afficherResultat(boolean succes, String messageOk, String messageKo) {
//         System.out.println(succes ? messageOk : messageKo);
//     }

//     private static String valeurOuVide(String valeur) {
//         return (valeur == null || valeur.isEmpty()) ? "(vide)" : valeur;
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
//                 System.out.println("Nombre invalide.");
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
//         return scanner.nextLine().trim().toLowerCase().startsWith("o");
//     }
// }
