package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUtilisateur;
    private String nomUtilisateur;
    private String motdepasse;
    private List<FichePersonnage> fiches;

    public Utilisateur(int idUtilisateur, String nomUtilisateur, String motdepasse) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.motdepasse = motdepasse;
        this.fiches = new ArrayList<>();
    }

    public boolean verifierMotDePasse(String motdepasse) {
        return this.motdepasse.equals(motdepasse);
    }

    public FichePersonnage creerFiche(String nomFichePersonnage) {
        int id = fiches.size() + 1;
        FichePersonnage fiche = new FichePersonnage(id, nomFichePersonnage);
        fiches.add(fiche);
        return fiche;
    }

    public void supprimerFiche(int idFichePersonnage) {
        fiches.removeIf(fiche -> fiche.getIdFichePersonnage() == idFichePersonnage);
    }

    public int getIdUtilisateur() { return idUtilisateur; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getMotdepasse() { return motdepasse; }
    public List<FichePersonnage> getFiches() { return fiches; }
}
