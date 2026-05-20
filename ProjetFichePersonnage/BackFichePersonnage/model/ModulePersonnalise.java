package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModulePersonnalise extends Module implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nom;
    private String type;

    private String contenuTexte;
    private List<String> contenuListe;
    private List<Statistique> contenuStats;

    // CREATION MODULE
    public ModulePersonnalise(String id, String nom, String type) {
        super(0, 0, 300, 200);
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.contenuTexte = "";
        this.contenuListe = new ArrayList<>();
        this.contenuStats = new ArrayList<>();
    }

    // GET ID
    public String getId() { return id; }
    // SET ID
    public void setId(String id) { this.id = id; }

    // GET NOM
    public String getNom() { return nom; }
    // SET NOM
    public void setNom(String nom) { this.nom = nom; }

    // GET TYPE
    public String getType() { return type; }
    // SET TYPE
    public void setType(String type) { this.type = type; }

    // GET TEXTE
    public String getContenuTexte() { return contenuTexte; }
    // SET TEXTE
    public void setContenuTexte(String contenuTexte) { this.contenuTexte = contenuTexte; }

    // GET LISTE
    public List<String> getContenuListe() { return contenuListe; }
    // SET LISTE
    public void setContenuListe(List<String> contenuListe) { this.contenuListe = contenuListe; }

    // GET STATS
    public List<Statistique> getContenuStats() { return contenuStats; }
    // SET STATS
    public void setContenuStats(List<Statistique> contenuStats) { this.contenuStats = contenuStats; }
}
