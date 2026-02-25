import service.GestionUtilisateur;
import model.Utilisateur;

public class Main {

    public static void main(String[] args) {
        GestionUtilisateur gestion = new GestionUtilisateur();

        System.out.println("=== TEST 1 : Creation de comptes ===");
        gestion.creerCompte("Alice", "motdepasse123");
        gestion.creerCompte("Bob", "secret456");
        gestion.creerCompte("Alice", "autre"); // doit echouer (doublon)

        System.out.println("\n=== TEST 2 : Connexion ===");
        gestion.seConnecter("Alice", "mauvais"); // doit echouer
        gestion.seConnecter("Alice", "motdepasse123"); // doit reussir

        System.out.println("\n=== TEST 3 : Utilisateur connecte ===");
        Utilisateur connecte = gestion.getUtilisateurConnecte();
        if (connecte != null) {
            System.out.println("Utilisateur connecte : " + connecte.getNomUtilisateur());
            System.out.println("ID : " + connecte.getIdUtilisateur());
        }

        System.out.println("\n=== TEST 4 : Double connexion ===");
        gestion.seConnecter("Bob", "secret456"); // doit echouer (deja connecte)

        System.out.println("\n=== TEST 5 : Deconnexion ===");
        gestion.seDeconnecter();
        gestion.seDeconnecter(); // doit echouer (deja deconnecte)

        System.out.println("\n=== TEST 6 : Connexion Bob ===");
        gestion.seConnecter("Bob", "secret456"); // doit reussir

        System.out.println("\n=== TEST 7 : Persistance ===");
        System.out.println("Nombre d'utilisateurs sauvegardes : " + gestion.getUtilisateurs().size());
        System.out.println("Relancez le programme pour verifier que les comptes persistent.");
    }
}
