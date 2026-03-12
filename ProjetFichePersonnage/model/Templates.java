package model;

public class Templates extends FichePersonnage {

    private int idTemplates;
    private String nomTemplates;

    public Templates(int idTemplates, String nomTemplates) {
        super(idTemplates, nomTemplates);
        this.idTemplates = idTemplates;
        this.nomTemplates = nomTemplates;
    }

    public static Templates importerTemplates() {
        // Logique d'importation de Templates depuis un fichier
        return null;
    }

    public void appliquerTemplates(FichePersonnage fiche) {
        // Logique d'application du Templates sur une fiche
    }

    public int getIdTemplates() {
        return idTemplates;
    }

    public String getNomTemplates() {
        return nomTemplates;
    }
}
