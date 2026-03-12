package service;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class GestionFiche {

    private static final String DOSSIER_DATA = "data/";
    private GestionUtilisateur gestionUtilisateur;

    public GestionFiche(GestionUtilisateur gestionUtilisateur) {
        this.gestionUtilisateur = gestionUtilisateur;
    }

    // Cree une nouvelle fiche.
    public FichePersonnage creerFiche(String nomFiche) {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : aucun utilisateur connecte.");
            return null;
        }
        FichePersonnage fiche = connecte.creerFiche(nomFiche);
        sauvegarderFiches(connecte);
        System.out.println("Fiche '" + nomFiche + "' creee avec succes (id=" + fiche.getIdFichePersonnage() + ").");
        return fiche;
    }

    // Retourne la liste des fiches.
    public List<FichePersonnage> listerFiches() {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : aucun utilisateur connecte.");
            return new ArrayList<>();
        }
        return connecte.getFiches();
    }

    // Recupere une fiche par son ID.
    public FichePersonnage getFiche(int idFiche) {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : aucun utilisateur connecte.");
            return null;
        }
        for (FichePersonnage fiche : connecte.getFiches()) {
            if (fiche.getIdFichePersonnage() == idFiche) {
                return fiche;
            }
        }
        return null;
    }

    // Modifie le portrait d'une fiche.
    public boolean modifierPortrait(int idFiche, String imagePortrait) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.modifierPortrait(imagePortrait);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Portrait de la fiche " + idFiche + " modifie.");
        return true;
    }

    // Modifie la biographie d'une fiche.
    public boolean modifierBiographie(int idFiche, String texteBiographie) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.modifierBiographie(texteBiographie);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Biographie de la fiche " + idFiche + " modifiee.");
        return true;
    }

    // Ajoute une statistique a une fiche.
    public boolean ajouterStatistique(int idFiche, String nomStat, int valeur) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().ajouterStatistique(nomStat, valeur);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Statistique '" + nomStat + "' ajoutee a la fiche " + idFiche + ".");
        return true;
    }

    //  Modifie une statistique d'une fiche.
    public boolean modifierStatistique(int idFiche, int idStat, String nomStat, int valeur) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().modifierStatistique(idStat, nomStat, valeur);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Statistique " + idStat + " modifiee sur la fiche " + idFiche + ".");
        return true;
    }

    // Supprime une statistique d'une fiche.
    public boolean supprimerStatistique(int idFiche, int idStat) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().supprimerStatistique(idStat);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Statistique " + idStat + " supprimee de la fiche " + idFiche + ".");
        return true;
    }

    // Ajoute une competence a une fiche.
    public boolean ajouterCompetence(int idFiche, String nomCompetence) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().ajouterCompetence(nomCompetence);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Competence '" + nomCompetence + "' ajoutee a la fiche " + idFiche + ".");
        return true;
    }

    // Modifie une competence d'une fiche.
    public boolean modifierCompetence(int idFiche, String ancienNom, String nouveauNom) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().modifierCompetence(ancienNom, nouveauNom);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Competence '" + ancienNom + "' renommee en '" + nouveauNom + "'.");
        return true;
    }

    // Supprime une competence d'une fiche.
    public boolean supprimerCompetence(int idFiche, String nomCompetence) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().supprimerCompetence(nomCompetence);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Competence '" + nomCompetence + "' supprimee.");
        return true;
    }

    // Ajoute un equipement a une fiche.
    public boolean ajouterEquipement(int idFiche, String nomEquipement) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().ajouterEquipement(nomEquipement);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Equipement '" + nomEquipement + "' ajoute a la fiche " + idFiche + ".");
        return true;
    }

    // Modifie un equipement d'une fiche.
    public boolean modifierEquipement(int idFiche, String ancienNom, String nouveauNom) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().modifier(ancienNom, nouveauNom);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Equipement '" + ancienNom + "' renomme en '" + nouveauNom + "'.");
        return true;
    }

    // Supprime un equipement d'une fiche.
    public boolean supprimerEquipement(int idFiche, String nomEquipement) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().supprimerEquipement(nomEquipement);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Equipement '" + nomEquipement + "' supprime.");
        return true;
    }

    // Modifie la position d'un module sur la fiche.
    public boolean modifierPositionModule(int idFiche, String nomModule, int posX, int posY) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        model.Module module = getModuleParNom(fiche, nomModule);
        if (module == null) {
            System.out.println("Erreur : module '" + nomModule + "' inconnu.");
            return false;
        }

        module.modifierPosition(posX, posY);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Position du module '" + nomModule + "' modifiee (" + posX + ", " + posY + ").");
        return true;
    }

    //  Modifie la taille d'un module sur la fiche.
    public boolean modifierTailleModule(int idFiche, String nomModule, int largeur, int hauteur) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        model.Module module2 = getModuleParNom(fiche, nomModule);
        if (module2 == null) {
            System.out.println("Erreur : module '" + nomModule + "' inconnu.");
            return false;
        }

        module2.modifierTaille(largeur, hauteur);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Taille du module '" + nomModule + "' modifiee (" + largeur + "x" + hauteur + ").");
        return true;
    }

    // Supprime une fiche de l'utilisateur connecte.
    public boolean supprimerFiche(int idFiche) {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : aucun utilisateur connecte.");
            return false;
        }

        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        connecte.supprimerFiche(idFiche);
        sauvegarderFiches(connecte);
        System.out.println("Fiche " + idFiche + " supprimee.");
        return true;
    }

    // Retourne le module correspondant au nom donne
    private model.Module getModuleParNom(FichePersonnage fiche, String nomModule) {
        switch (nomModule.toLowerCase()) {
            case "portrait":
                return fiche.getPortrait();
            case "biographie":
                return fiche.getBiographie();
            case "statistiques":
                return fiche.getStatistiques();
            case "competence":
                return fiche.getCompetence();
            case "equipement":
                return fiche.getEquipement();
            default:
                return null;
        }
    }







    // ========== PERSISTANCE (SERIALISATION BINAIRE) ==========

    private void sauvegarderFiches(Utilisateur utilisateur) {
        String cheminFichier = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(cheminFichier);
        fichier.getParentFile().mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
            oos.writeObject(utilisateur.getFiches());
            System.out.println("Fiches sauvegardees (serialisation) : " + cheminFichier);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde des fiches : " + e.getMessage());
        }
    }

    /**
     * Charge les fiches d'un utilisateur depuis son fichier serialise.
     * Reconstruit automatiquement tous les objets grace a la deserialisation.
     */

    @SuppressWarnings("unchecked")
    public void chargerFiches(Utilisateur utilisateur) {
        String cheminFichier = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(cheminFichier);

        if (!fichier.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            List<FichePersonnage> fichesChargees = (List<FichePersonnage>) ois.readObject();
            
            utilisateur.getFiches().clear();
            utilisateur.getFiches().addAll(fichesChargees);
            
            System.out.println("Fiches chargees (deserialisation) : " + fichesChargees.size() + " fiche(s)");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur lors du chargement des fiches : " + e.getMessage());
        }
    }
}
