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
