package service;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service qui gere les fiches de personnages avec controle des droits.
 *
 * Regles de droits :
 * - Un utilisateur doit etre connecte pour toute operation
 * - Un utilisateur ne peut creer des fiches que pour lui-meme
 * - Un utilisateur ne peut modifier/supprimer que ses propres fiches
 * - Un utilisateur ne peut voir que ses propres fiches
 *
 * Persistance : serialisation binaire Java dans "data/fiches_{idUtilisateur}.dat"
 * Utilise ObjectOutputStream/ObjectInputStream pour sauvegarder les objets.
 */
public class GestionFiche {

    private static final String DOSSIER_DATA = "data/";
    private GestionUtilisateur gestionUtilisateur;

    public GestionFiche(GestionUtilisateur gestionUtilisateur) {
        this.gestionUtilisateur = gestionUtilisateur;
    }

    /**
     * Cree une nouvelle fiche pour l'utilisateur connecte.
     * Verifie que l'utilisateur est connecte avant de creer.
     */
    public FichePersonnage creerFiche(String nomFiche) {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : vous devez etre connecte pour creer une fiche.");
            return null;
        }

        FichePersonnage fiche = connecte.creerFiche(nomFiche);
        sauvegarderFiches(connecte);
        System.out.println("Fiche '" + nomFiche + "' creee avec succes (id=" + fiche.getIdFichePersonnage() + ").");
        return fiche;
    }

    /**
     * Retourne la liste des fiches de l'utilisateur connecte.
     */
    public List<FichePersonnage> listerFiches() {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : vous devez etre connecte pour voir vos fiches.");
            return new ArrayList<>();
        }

        return connecte.getFiches();
    }

    /**
     * Recupere une fiche par son ID.
     * Verifie que l'utilisateur connecte est bien le proprietaire.
     */
    public FichePersonnage getFiche(int idFiche) {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : vous devez etre connecte.");
            return null;
        }

        for (FichePersonnage fiche : connecte.getFiches()) {
            if (fiche.getIdFichePersonnage() == idFiche) {
                return fiche;
            }
        }

        System.out.println("Erreur : fiche introuvable ou vous n'en etes pas le proprietaire.");
        return null;
    }

    /**
     * Modifie le portrait d'une fiche.
     * Verifie les droits avant modification.
     */
    public boolean modifierPortrait(int idFiche, String imagePortrait) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.modifierPortrait(imagePortrait);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Portrait de la fiche " + idFiche + " modifie.");
        return true;
    }

    /**
     * Modifie la biographie d'une fiche.
     * Verifie les droits avant modification.
     */
    public boolean modifierBiographie(int idFiche, String texteBiographie) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.modifierBiographie(texteBiographie);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Biographie de la fiche " + idFiche + " modifiee.");
        return true;
    }

    /**
     * Ajoute une statistique a une fiche.
     */
    public boolean ajouterStatistique(int idFiche, String nomStat, int valeur) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().ajouterStatistique(nomStat, valeur);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Statistique '" + nomStat + "' ajoutee a la fiche " + idFiche + ".");
        return true;
    }

    /**
     * Modifie une statistique d'une fiche.
     */
    public boolean modifierStatistique(int idFiche, int idStat, String nomStat, int valeur) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().modifierStatistique(idStat, nomStat, valeur);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Statistique " + idStat + " modifiee sur la fiche " + idFiche + ".");
        return true;
    }

    /**
     * Supprime une statistique d'une fiche.
     */
    public boolean supprimerStatistique(int idFiche, int idStat) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().supprimerStatistique(idStat);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Statistique " + idStat + " supprimee de la fiche " + idFiche + ".");
        return true;
    }

    /**
     * Ajoute une competence a une fiche.
     */
    public boolean ajouterCompetence(int idFiche, String nomCompetence) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().ajouterCompetence(nomCompetence);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Competence '" + nomCompetence + "' ajoutee a la fiche " + idFiche + ".");
        return true;
    }

    /**
     * Modifie une competence d'une fiche.
     */
    public boolean modifierCompetence(int idFiche, String ancienNom, String nouveauNom) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().modifierCompetence(ancienNom, nouveauNom);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Competence '" + ancienNom + "' renommee en '" + nouveauNom + "'.");
        return true;
    }

    /**
     * Supprime une competence d'une fiche.
     */
    public boolean supprimerCompetence(int idFiche, String nomCompetence) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().supprimerCompetence(nomCompetence);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Competence '" + nomCompetence + "' supprimee.");
        return true;
    }

    /**
     * Ajoute un equipement a une fiche.
     */
    public boolean ajouterEquipement(int idFiche, String nomEquipement) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().ajouterEquipement(nomEquipement);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Equipement '" + nomEquipement + "' ajoute a la fiche " + idFiche + ".");
        return true;
    }

    /**
     * Modifie un equipement d'une fiche.
     */
    public boolean modifierEquipement(int idFiche, String ancienNom, String nouveauNom) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().modifier(ancienNom, nouveauNom);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Equipement '" + ancienNom + "' renomme en '" + nouveauNom + "'.");
        return true;
    }

    /**
     * Supprime un equipement d'une fiche.
     */
    public boolean supprimerEquipement(int idFiche, String nomEquipement) {
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().supprimerEquipement(nomEquipement);
        sauvegarderFiches(gestionUtilisateur.getUtilisateurConnecte());
        System.out.println("Equipement '" + nomEquipement + "' supprime.");
        return true;
    }

    /**
     * Modifie la position d'un module sur la fiche.
     * Les modules sont : portrait, biographie, statistiques, competence, equipement.
     */
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

    /**
     * Modifie la taille d'un module sur la fiche.
     */
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

    /**
     * Supprime une fiche de l'utilisateur connecte.
     */
    public boolean supprimerFiche(int idFiche) {
        Utilisateur connecte = gestionUtilisateur.getUtilisateurConnecte();
        if (connecte == null) {
            System.out.println("Erreur : vous devez etre connecte.");
            return false;
        }

        // Verification que la fiche existe et appartient a l'utilisateur
        FichePersonnage fiche = getFiche(idFiche);
        if (fiche == null) return false;

        connecte.supprimerFiche(idFiche);
        sauvegarderFiches(connecte);
        System.out.println("Fiche " + idFiche + " supprimee.");
        return true;
    }

    /**
     * Retourne le module correspondant au nom donne.
     */
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

    /**
     * Sauvegarde les fiches d'un utilisateur avec la serialisation Java.
     * Chaque utilisateur a son propre fichier : data/fiches_{id}.dat
     *
     * La serialisation permet de sauvegarder tout l'objet (et ses objets imbriques)
     * en une seule ligne de code, sans avoir a parser manuellement.
     */
    private void sauvegarderFiches(Utilisateur utilisateur) {
        String cheminFichier = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(cheminFichier);
        fichier.getParentFile().mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
            // Sauvegarde toute la liste de fiches en une seule operation
            // Les objets imbriques (Portrait, Biographie, etc.) sont automatiquement inclus
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
            // Charge toute la liste de fiches en une seule operation
            // Le cast (List<FichePersonnage>) est necessaire car readObject() retourne Object
            List<FichePersonnage> fichesChargees = (List<FichePersonnage>) ois.readObject();
            
            // On vide les fiches actuelles et on ajoute celles chargees
            utilisateur.getFiches().clear();
            utilisateur.getFiches().addAll(fichesChargees);
            
            System.out.println("Fiches chargees (deserialisation) : " + fichesChargees.size() + " fiche(s)");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur lors du chargement des fiches : " + e.getMessage());
        }
    }
}
