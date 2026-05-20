package service;

import model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.SecretKeySpec;

public class GestionFiche {

    private static final String DOSSIER_DATA = "data/";

    // CREATION SERVICE
    public GestionFiche() {
    }

    // CREER FICHE
    public FichePersonnage creerFiche(Utilisateur u, SecretKeySpec cle, String nomFiche) {
        FichePersonnage fiche = u.creerFiche(nomFiche);
        sauvegarderFiches(u, cle);
        return fiche;
    }

    // LISTER FICHES
    public List<FichePersonnage> listerFiches(Utilisateur u) {
        return u.getFiches();
    }

    // GET FICHE
    public FichePersonnage getFiche(Utilisateur u, int idFiche) {
        for (FichePersonnage fiche : u.getFiches()) {
            if (fiche.getIdFichePersonnage() == idFiche) {
                return fiche;
            }
        }
        return null;
    }

    // MODIFIER PORTRAIT
    public boolean modifierPortrait(Utilisateur u, SecretKeySpec cle, int idFiche, String imagePortrait) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        fiche.modifierPortrait(imagePortrait);
        sauvegarderFiches(u, cle);
        return true;
    }

    // MODIFIER BIOGRAPHIE
    public boolean modifierBiographie(Utilisateur u, SecretKeySpec cle, int idFiche, String texteBiographie) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        fiche.modifierBiographie(texteBiographie);
        sauvegarderFiches(u, cle);
        return true;
    }

    // AJOUTER STATISTIQUE
    public boolean ajouterStatistique(Utilisateur u, SecretKeySpec cle, int idFiche, String nomStat, int valeur) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        fiche.getStatistiques().ajouterStatistique(nomStat, valeur);
        sauvegarderFiches(u, cle);
        return true;
    }

    // AJOUTER COMPETENCE
    public boolean ajouterCompetence(Utilisateur u, SecretKeySpec cle, int idFiche, String nomCompetence) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        fiche.getCompetence().ajouterCompetence(nomCompetence);
        sauvegarderFiches(u, cle);
        return true;
    }

    // AJOUTER EQUIPEMENT
    public boolean ajouterEquipement(Utilisateur u, SecretKeySpec cle, int idFiche, String nomEquipement) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        fiche.getEquipement().ajouterEquipement(nomEquipement);
        sauvegarderFiches(u, cle);
        return true;
    }

    // MODIFIER POSITION
    public boolean modifierPositionModule(Utilisateur u, SecretKeySpec cle, int idFiche, String nomModule, int posX, int posY) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        model.Module module = getModuleParNom(fiche, nomModule);
        if (module == null) return false;
        int x = Math.max(0, Math.min(posX, FichePersonnage.CANVAS_LARGEUR - module.getLargeur()));
        int y = Math.max(0, Math.min(posY, FichePersonnage.CANVAS_HAUTEUR - module.getHauteur()));
        if (chevauche(fiche, nomModule, x, y, module.getLargeur(), module.getHauteur())) return false;
        module.modifierPosition(x, y);
        sauvegarderFiches(u, cle);
        return true;
    }

    // MODIFIER TAILLE
    public boolean modifierTailleModule(Utilisateur u, SecretKeySpec cle, int idFiche, String nomModule, int largeur, int hauteur) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        model.Module module = getModuleParNom(fiche, nomModule);
        if (module == null) return false;
        int l = Math.max(1, Math.min(largeur, FichePersonnage.CANVAS_LARGEUR - module.getPositionX()));
        int h = Math.max(1, Math.min(hauteur, FichePersonnage.CANVAS_HAUTEUR - module.getPositionY()));
        if (chevauche(fiche, nomModule, module.getPositionX(), module.getPositionY(), l, h)) return false;
        module.modifierTaille(l, h);
        sauvegarderFiches(u, cle);
        return true;
    }

    // VERIFIER CHEVAUCHEMENT
    private boolean chevauche(FichePersonnage fiche, String nomExclu, int x, int y, int l, int h) {
        String[] noms = {"portrait", "biographie", "statistiques", "competence", "equipement"};
        for (String nom : noms) {
            if (nom.equalsIgnoreCase(nomExclu)) continue;
            model.Module m = getModuleParNom(fiche, nom);
            if (m == null) continue;
            int x2 = m.getPositionX(), y2 = m.getPositionY();
            int l2 = m.getLargeur(), h2 = m.getHauteur();
            if (x < x2 + l2 && x + l > x2 && y < y2 + h2 && y + h > y2) return true;
        }
        return false;
    }

    // LISTER COMPETENCES
    public List<String> listerCompetencesUtilisateur(Utilisateur u) {
        Set<String> set = new LinkedHashSet<>();
        for (FichePersonnage f : u.getFiches()) {
            set.addAll(f.getCompetence().getCompetences());
        }
        return new ArrayList<>(set);
    }

    // LISTER EQUIPEMENTS
    public List<String> listerEquipementsUtilisateur(Utilisateur u) {
        Set<String> set = new LinkedHashSet<>();
        for (FichePersonnage f : u.getFiches()) {
            set.addAll(f.getEquipement().getEquipements());
        }
        return new ArrayList<>(set);
    }

    // LISTER STATISTIQUES
    public List<Statistique> listerStatistiquesUtilisateur(Utilisateur u) {
        Map<String, Statistique> map = new LinkedHashMap<>();
        for (FichePersonnage f : u.getFiches()) {
            for (Statistique s : f.getStatistiques().getStatistiques()) {
                if (!map.containsKey(s.getNomStatistique())) {
                    map.put(s.getNomStatistique(), s);
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    // AJOUTER MODULE
    public boolean ajouterModulePersonnalise(Utilisateur u, SecretKeySpec cle, int idFiche, ModulePersonnalise module) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        fiche.getModulesPersonnalises().add(module);
        sauvegarderFiches(u, cle);
        return true;
    }

    // MODIFIER MODULE
    public boolean modifierModulePersonnalise(Utilisateur u, SecretKeySpec cle, int idFiche, String idModule, ModulePersonnalise module) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        List<ModulePersonnalise> liste = fiche.getModulesPersonnalises();
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getId().equals(idModule)) {
                liste.set(i, module);
                sauvegarderFiches(u, cle);
                return true;
            }
        }
        return false;
    }

    // SUPPRIMER MODULE
    public boolean supprimerModulePersonnalise(Utilisateur u, SecretKeySpec cle, int idFiche, String idModule) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        boolean removed = fiche.getModulesPersonnalises().removeIf(m -> m.getId().equals(idModule));
        if (removed) sauvegarderFiches(u, cle);
        return removed;
    }

    // SUPPRIMER FICHE
    public boolean supprimerFiche(Utilisateur u, SecretKeySpec cle, int idFiche) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        u.supprimerFiche(idFiche);
        sauvegarderFiches(u, cle);
        return true;
    }

    // GET MODULE
    private model.Module getModuleParNom(FichePersonnage fiche, String nomModule) {
        switch (nomModule.toLowerCase()) {
            case "portrait": return fiche.getPortrait();
            case "biographie": return fiche.getBiographie();
            case "statistiques": return fiche.getStatistiques();
            case "competence": return fiche.getCompetence();
            case "equipement": return fiche.getEquipement();
            default: return null;
        }
    }

    // EXPORTER FICHE
    public byte[] exporterFiche(Utilisateur u, int idFiche) throws Exception {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(fiche);
        return baos.toByteArray();
    }

    // IMPORTER FICHE
    public FichePersonnage importerFiche(Utilisateur u, SecretKeySpec cle, byte[] data) throws Exception {
        FichePersonnage fiche = (FichePersonnage) new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
        fiche.setIdFichePersonnage(u.prochainIdFiche());
        u.getFiches().add(fiche);
        sauvegarderFiches(u, cle);
        return fiche;
    }

    // SAUVEGARDER FICHES
    private void sauvegarderFiches(Utilisateur utilisateur, SecretKeySpec cle) {
        String cheminFichier = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(cheminFichier);
        fichier.getParentFile().mkdirs();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(utilisateur.getFiches());
            byte[] dataChiffree = GestionChiffrement.chiffrer(baos.toByteArray(), cle);
            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                fos.write(dataChiffree);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde des fiches : " + e.getMessage());
        }
    }

    // CHARGER FICHES
    @SuppressWarnings("unchecked")
    public void chargerFiches(Utilisateur utilisateur, SecretKeySpec cle) {
        String cheminFichier = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(cheminFichier);
        if (!fichier.exists()) return;
        try {
            byte[] dataChiffree = Files.readAllBytes(fichier.toPath());
            byte[] dataSerialisee = GestionChiffrement.dechiffrer(dataChiffree, cle);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dataSerialisee));
            List<FichePersonnage> fichesChargees = (List<FichePersonnage>) ois.readObject();
            utilisateur.getFiches().clear();
            utilisateur.getFiches().addAll(fichesChargees);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des fiches : " + e.getMessage());
        }
    }
}
