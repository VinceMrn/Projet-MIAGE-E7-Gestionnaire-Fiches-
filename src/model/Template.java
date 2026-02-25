package model;

public class Template {

    private int idTemplate;
    private String nomTemplate;

    public Template(int idTemplate, String nomTemplate) {
        this.idTemplate = idTemplate;
        this.nomTemplate = nomTemplate;
    }

    public static Template creeTemplate(int idFichePersonnage, String nomTemplate) {
        return new Template(idFichePersonnage, nomTemplate);
    }

    public static Template importerTemplate() {
        // Logique d'importation de template depuis un fichier
        return null;
    }

    public void appliquerTemplate(FichePersonnage fiche) {
        // Logique d'application du template sur une fiche
    }

    public int getIdTemplate() {
        return idTemplate;
    }

    public String getNomTemplate() {
        return nomTemplate;
    }
}
