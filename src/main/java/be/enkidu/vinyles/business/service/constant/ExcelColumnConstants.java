package be.enkidu.vinyles.business.service.constant;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelColumnConstants {

    // Constante pour les colonnes de la feuille Artistes
    public static final Map<String, String> ARTISTE_COLUMNS;

    static {
        ARTISTE_COLUMNS = new LinkedHashMap<>();
        ARTISTE_COLUMNS.put("ID", "ID");
        ARTISTE_COLUMNS.put("NOM", "Nom");
        ARTISTE_COLUMNS.put("PRENOM", "Prenom");
        ARTISTE_COLUMNS.put("DATE_NAISSANCE", "Date Naissance");
        ARTISTE_COLUMNS.put("DATE_DECES", "Date Décès");
        ARTISTE_COLUMNS.put("IMAGE", "Image");
    }

    // Constante pour les colonnes de la feuille Titres
    public static final Map<String, String> TITRE_COLUMNS;

    static {
        TITRE_COLUMNS = new LinkedHashMap<>();
        TITRE_COLUMNS.put("ID", "ID");
        TITRE_COLUMNS.put("NOM", "Nom");
        TITRE_COLUMNS.put("DUREE", "Durée");
        TITRE_COLUMNS.put("ARTISTE_IDS", "Artiste IDs");
    }

    // Constante pour les colonnes de la feuille Albums
    public static final Map<String, String> ALBUM_COLUMNS;

    static {
        ALBUM_COLUMNS = new LinkedHashMap<>();
        ALBUM_COLUMNS.put("ID", "ID");
        ALBUM_COLUMNS.put("NOM", "Nom");
        ALBUM_COLUMNS.put("ARTISTE_IDS", "Artiste IDs");
        ALBUM_COLUMNS.put("TITRE_IDS", "Titre IDs");
        ALBUM_COLUMNS.put("TAILLE", "Taille");
        ALBUM_COLUMNS.put("STATUS", "Status");
        ALBUM_COLUMNS.put("IMAGE", "Image");
    }

    public static int getPositionOfKey(Map<String, String> map, String key) {
        int position = 0;
        for (String currentKey : map.keySet()) {
            if (currentKey.equals(key)) {
                return position;
            }
            position++;
        }
        return -1; // Retourne -1 si la clé n'est pas trouvée
    }
}
