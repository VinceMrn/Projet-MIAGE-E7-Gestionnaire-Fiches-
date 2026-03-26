package service;

import model.*;

import java.io.*;
import java.util.List;

public class GestionFiche {

    private static final String DOSSIER_DATA = "data/";
    private GestionUtilisateur gestionUtilisateur;

    public GestionFiche(GestionUtilisateur gestionUtilisateur) {
        this.gestionUtilisateur = gestionUtilisateur;
    }

    private Utilisateur connecte() {
        return gestionUtilisateur.getUtilisateurConnecte();
    }

    public FichePersonnage creerFiche(String nomFiche) {
        FichePersonnage fiche = connecte().creerFiche(nomFiche);
        sauvegarderFiches(connecte());
        return fiche;
    }

    public List<FichePersonnage> listerFiches() {
        return connecte().getFiches();
    }

    public FichePersonnage getFiche(int idFiche) {
        for (FichePersonnage f : connecte().getFiches()) {
            if (f.getIdFichePersonnage() == idFiche) return f;
        }
        return null;
    }

    public boolean supprimerFiche(int idFiche) {
        if (getFiche(idFiche) == null) return false;
        connecte().supprimerFiche(idFiche);
        sauvegarderFiches(connecte());
        return true;
    }

    public void modifierPortrait(int idFiche, String image) {
        getFiche(idFiche).modifierPortrait(image);
        sauvegarderFiches(connecte());
    }

    public void modifierBiographie(int idFiche, String texte) {
        getFiche(idFiche).modifierBiographie(texte);
        sauvegarderFiches(connecte());
    }

    public void ajouterStatistique(int idFiche, String nom, int valeur) {
        getFiche(idFiche).getStatistiques().ajouterStatistique(nom, valeur);
        sauvegarderFiches(connecte());
    }

    public void modifierStatistique(int idFiche, int idStat, String nom, int valeur) {
        getFiche(idFiche).getStatistiques().modifierStatistique(idStat, nom, valeur);
        sauvegarderFiches(connecte());
    }

    public void supprimerStatistique(int idFiche, int idStat) {
        getFiche(idFiche).getStatistiques().supprimerStatistique(idStat);
        sauvegarderFiches(connecte());
    }

    public void ajouterCompetence(int idFiche, String nom) {
        getFiche(idFiche).getCompetence().ajouterCompetence(nom);
        sauvegarderFiches(connecte());
    }

    public void modifierCompetence(int idFiche, String ancien, String nouveau) {
        getFiche(idFiche).getCompetence().modifierCompetence(ancien, nouveau);
        sauvegarderFiches(connecte());
    }

    public void supprimerCompetence(int idFiche, String nom) {
        getFiche(idFiche).getCompetence().supprimerCompetence(nom);
        sauvegarderFiches(connecte());
    }

    public void ajouterEquipement(int idFiche, String nom) {
        getFiche(idFiche).getEquipement().ajouterEquipement(nom);
        sauvegarderFiches(connecte());
    }

    public void modifierEquipement(int idFiche, String ancien, String nouveau) {
        getFiche(idFiche).getEquipement().modifier(ancien, nouveau);
        sauvegarderFiches(connecte());
    }

    public void supprimerEquipement(int idFiche, String nom) {
        getFiche(idFiche).getEquipement().supprimerEquipement(nom);
        sauvegarderFiches(connecte());
    }

    public boolean modifierPositionModule(int idFiche, String nomModule, int posX, int posY) {
        model.Module m = getModuleParNom(getFiche(idFiche), nomModule);
        if (m == null) return false;
        m.modifierPosition(posX, posY);
        sauvegarderFiches(connecte());
        return true;
    }

    public boolean modifierTailleModule(int idFiche, String nomModule, int largeur, int hauteur) {
        model.Module m = getModuleParNom(getFiche(idFiche), nomModule);
        if (m == null) return false;
        m.modifierTaille(largeur, hauteur);
        sauvegarderFiches(connecte());
        return true;
    }

    private model.Module getModuleParNom(FichePersonnage fiche, String nom) {
        switch (nom.toLowerCase()) {
            case "portrait": return fiche.getPortrait();
            case "biographie": return fiche.getBiographie();
            case "statistiques": return fiche.getStatistiques();
            case "competence": return fiche.getCompetence();
            case "equipement": return fiche.getEquipement();
            default: return null;
        }
    }

    // ========== PERSISTANCE ==========

    private void sauvegarderFiches(Utilisateur utilisateur) {
        String chemin = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(chemin);
        fichier.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
            oos.writeObject(utilisateur.getFiches());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void chargerFiches(Utilisateur utilisateur) {
        String chemin = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(chemin);
        if (!fichier.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            List<FichePersonnage> fiches = (List<FichePersonnage>) ois.readObject();
            utilisateur.getFiches().clear();
            utilisateur.getFiches().addAll(fiches);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
