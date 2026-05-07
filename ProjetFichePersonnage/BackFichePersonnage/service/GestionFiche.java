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

/**
 * Service qui gere les fiches de personnages avec controle des droits.
 *
 * L'utilisateur agissant et sa cle AES sont passes explicitement en parametre
 * a chaque methode qui modifie ou lit les fichiers chiffres.
 * La verification de session se fait en amont dans la couche Route.
 *
 * Persistance : serialisation binaire Java + chiffrement AES dans "data/fiches_{idUtilisateur}.dat"
 */
public class GestionFiche {

    private static final String DOSSIER_DATA = "data/";

    public GestionFiche() {
    }

    public FichePersonnage creerFiche(Utilisateur u, SecretKeySpec cle, String nomFiche) {
        FichePersonnage fiche = u.creerFiche(nomFiche);
        sauvegarderFiches(u, cle);
        System.out.println("Fiche '" + nomFiche + "' creee avec succes (id=" + fiche.getIdFichePersonnage() + ").");
        return fiche;
    }

    public List<FichePersonnage> listerFiches(Utilisateur u) {
        return u.getFiches();
    }

    public FichePersonnage getFiche(Utilisateur u, int idFiche) {
        for (FichePersonnage fiche : u.getFiches()) {
            if (fiche.getIdFichePersonnage() == idFiche) {
                return fiche;
            }
        }
        return null;
    }

    public boolean modifierPortrait(Utilisateur u, SecretKeySpec cle, int idFiche, String imagePortrait) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.modifierPortrait(imagePortrait);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierBiographie(Utilisateur u, SecretKeySpec cle, int idFiche, String texteBiographie) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.modifierBiographie(texteBiographie);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean ajouterStatistique(Utilisateur u, SecretKeySpec cle, int idFiche, String nomStat, int valeur) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().ajouterStatistique(nomStat, valeur);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierStatistique(Utilisateur u, SecretKeySpec cle, int idFiche, int idStat, String nomStat, int valeur) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().modifierStatistique(idStat, nomStat, valeur);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean supprimerStatistique(Utilisateur u, SecretKeySpec cle, int idFiche, int idStat) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getStatistiques().supprimerStatistique(idStat);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean ajouterCompetence(Utilisateur u, SecretKeySpec cle, int idFiche, String nomCompetence) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().ajouterCompetence(nomCompetence);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierCompetence(Utilisateur u, SecretKeySpec cle, int idFiche, String ancienNom, String nouveauNom) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().modifierCompetence(ancienNom, nouveauNom);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean supprimerCompetence(Utilisateur u, SecretKeySpec cle, int idFiche, String nomCompetence) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getCompetence().supprimerCompetence(nomCompetence);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean ajouterEquipement(Utilisateur u, SecretKeySpec cle, int idFiche, String nomEquipement) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().ajouterEquipement(nomEquipement);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierEquipement(Utilisateur u, SecretKeySpec cle, int idFiche, String ancienNom, String nouveauNom) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().modifier(ancienNom, nouveauNom);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean supprimerEquipement(Utilisateur u, SecretKeySpec cle, int idFiche, String nomEquipement) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.getEquipement().supprimerEquipement(nomEquipement);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierPositionModule(Utilisateur u, SecretKeySpec cle, int idFiche, String nomModule, int posX, int posY) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        model.Module module = getModuleParNom(fiche, nomModule);
        if (module == null) return false;

        int x = Math.max(0, Math.min(posX, FichePersonnage.CANVAS_LARGEUR - module.getLargeur()));
        int y = Math.max(0, Math.min(posY, FichePersonnage.CANVAS_HAUTEUR - module.getHauteur()));
        if (chevauche(fiche, nomModule, x, y, module.getLargeur(), module.getHauteur())) {
            return false;
        }
        module.modifierPosition(x, y);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierTailleModule(Utilisateur u, SecretKeySpec cle, int idFiche, String nomModule, int largeur, int hauteur) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        model.Module module2 = getModuleParNom(fiche, nomModule);
        if (module2 == null) return false;

        int l = Math.max(1, Math.min(largeur, FichePersonnage.CANVAS_LARGEUR - module2.getPositionX()));
        int h = Math.max(1, Math.min(hauteur, FichePersonnage.CANVAS_HAUTEUR - module2.getPositionY()));
        if (chevauche(fiche, nomModule, module2.getPositionX(), module2.getPositionY(), l, h)) {
            return false;
        }
        module2.modifierTaille(l, h);
        sauvegarderFiches(u, cle);
        return true;
    }

    private boolean chevauche(FichePersonnage fiche, String nomExclu, int x, int y, int l, int h) {
        String[] noms = {"portrait", "biographie", "statistiques", "competence", "equipement"};
        for (String nom : noms) {
            if (nom.equalsIgnoreCase(nomExclu)) continue;
            model.Module m = getModuleParNom(fiche, nom);
            if (m == null) continue;
            int x2 = m.getPositionX(), y2 = m.getPositionY();
            int l2 = m.getLargeur(), h2 = m.getHauteur();
            if (x < x2 + l2 && x + l > x2 && y < y2 + h2 && y + h > y2) {
                return true;
            }
        }
        return false;
    }

    public List<String> listerCompetencesUtilisateur(Utilisateur u) {
        Set<String> set = new LinkedHashSet<>();
        for (FichePersonnage f : u.getFiches()) {
            set.addAll(f.getCompetence().getCompetences());
        }
        return new ArrayList<>(set);
    }

    public List<String> listerEquipementsUtilisateur(Utilisateur u) {
        Set<String> set = new LinkedHashSet<>();
        for (FichePersonnage f : u.getFiches()) {
            set.addAll(f.getEquipement().getEquipements());
        }
        return new ArrayList<>(set);
    }

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

    public boolean ajouterModulePersonnalise(Utilisateur u, SecretKeySpec cle, int idFiche, model.ModulePersonnalise module) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        fiche.getModulesPersonnalises().add(module);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierModulePersonnalise(Utilisateur u, SecretKeySpec cle, int idFiche, String idModule, model.ModulePersonnalise module) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        java.util.List<model.ModulePersonnalise> liste = fiche.getModulesPersonnalises();
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getId().equals(idModule)) {
                liste.set(i, module);
                sauvegarderFiches(u, cle);
                return true;
            }
        }
        return false;
    }

    public boolean supprimerModulePersonnalise(Utilisateur u, SecretKeySpec cle, int idFiche, String idModule) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;
        java.util.List<model.ModulePersonnalise> liste = fiche.getModulesPersonnalises();
        boolean removed = liste.removeIf(m -> m.getId().equals(idModule));
        if (removed) {
            sauvegarderFiches(u, cle);
        }
        return removed;
    }

    public boolean supprimerFiche(Utilisateur u, SecretKeySpec cle, int idFiche) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        u.supprimerFiche(idFiche);
        sauvegarderFiches(u, cle);
        return true;
    }

    public boolean modifierNomFiche(Utilisateur u, SecretKeySpec cle, int idFiche, String nouveauNom) {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return false;

        fiche.modifierNomFiche(nouveauNom);
        sauvegarderFiches(u, cle);
        return true;
    }

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

    // ===== IMPORT / EXPORT =====

    public byte[] exporterFiche(Utilisateur u, int idFiche) throws Exception {
        FichePersonnage fiche = getFiche(u, idFiche);
        if (fiche == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(fiche);
        return baos.toByteArray();
    }

    public FichePersonnage importerFiche(Utilisateur u, SecretKeySpec cle, byte[] data) throws Exception {
        FichePersonnage fiche = (FichePersonnage) new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
        fiche.setIdFichePersonnage(u.prochainIdFiche());
        u.getFiches().add(fiche);
        sauvegarderFiches(u, cle);
        return fiche;
    }

    // ===== PERSISTANCE (serialisation binaire + chiffrement AES) =====

    // 3 etapes : serialiser en byte[] -> chiffrer -> ecrire sur disque
    private void sauvegarderFiches(Utilisateur utilisateur, SecretKeySpec cle) {
        String cheminFichier = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(cheminFichier);
        fichier.getParentFile().mkdirs();

        try {
            // 1. serialiser en byte[] (RAM, pas sur disque)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(utilisateur.getFiches());
            byte[] dataSerialisee = baos.toByteArray();

            // 2. chiffrer ces bytes avec la cle AES
            byte[] dataChiffree = GestionChiffrement.chiffrer(dataSerialisee, cle);

            // 3. ecrire les bytes chiffres sur le disque
            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                fos.write(dataChiffree);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde des fiches : " + e.getMessage());
        }
    }

    // 3 etapes : lire le fichier chiffre -> dechiffrer -> deserialiser
    @SuppressWarnings("unchecked")
    public void chargerFiches(Utilisateur utilisateur, SecretKeySpec cle) {
        String cheminFichier = DOSSIER_DATA + "fiches_" + utilisateur.getIdUtilisateur() + ".dat";
        File fichier = new File(cheminFichier);

        if (!fichier.exists()) {
            return;
        }

        try {
            // 1. lire les bytes chiffres depuis le disque
            byte[] dataChiffree = Files.readAllBytes(fichier.toPath());

            // 2. dechiffrer les bytes avec la cle AES
            byte[] dataSerialisee = GestionChiffrement.dechiffrer(dataChiffree, cle);

            // 3. deserialiser depuis le byte[] (et non depuis le fichier disque)
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dataSerialisee));
            List<FichePersonnage> fichesChargees = (List<FichePersonnage>) ois.readObject();
            utilisateur.getFiches().clear();
            utilisateur.getFiches().addAll(fichesChargees);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des fiches : " + e.getMessage());
        }
    }
}
