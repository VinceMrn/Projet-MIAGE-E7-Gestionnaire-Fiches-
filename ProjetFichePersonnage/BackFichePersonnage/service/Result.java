package service;

/**
 * Classe générique pour encapsuler le résultat d'une opération.
 * Remplace l'usage des valeurs null et permet de retourner un message d'erreur explicite.
 */
public class Result<T> {
    
    private final boolean succes;
    private final T donnees;
    private final String message;

    private Result(boolean succes, T donnees, String message) {
        this.succes = succes;
        this.donnees = donnees;
        this.message = message;
    }

    /**
     * Crée un résultat de succès avec les données retournées.
     */
    public static <T> Result<T> succes(T donnees) {
        return new Result<>(true, donnees, null);
    }

    /**
     * Crée un résultat d'erreur avec un message explicite.
     */
    public static <T> Result<T> erreur(String message) {
        return new Result<>(false, null, message);
    }

    public boolean estSucces() {
        return succes;
    }

    public T getDonnees() {
        return donnees;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (succes) {
            return "Succès";
        } else {
            return "Erreur: " + message;
        }
    }
}
