package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModulePersonnalise extends Module implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nom;
    private String type; // texte | liste | stats

    private String contenuTexte;
    private List<String> contenuListe;
    private List<Statistique> contenuStats;

    public ModulePersonnalise(String id, String nom, String type) {
        super(0,0,300,200);
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.contenuTexte = "";
        this.contenuListe = new ArrayList<>();
        this.contenuStats = new ArrayList<>();
    }

    public ModulePersonnalise(String id, String nom) {
        this(id, nom, "texte");
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContenuTexte() { return contenuTexte; }
    public void setContenuTexte(String contenuTexte) { this.contenuTexte = contenuTexte; }

    public List<String> getContenuListe() { return contenuListe; }
    public void setContenuListe(List<String> contenuListe) { this.contenuListe = contenuListe; }

    public List<Statistique> getContenuStats() { return contenuStats; }
    public void setContenuStats(List<Statistique> contenuStats) { this.contenuStats = contenuStats; }
}
