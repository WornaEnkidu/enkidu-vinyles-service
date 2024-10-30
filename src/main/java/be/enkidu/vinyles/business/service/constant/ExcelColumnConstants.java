package be.enkidu.vinyles.business.service.constant;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelColumnConstants {

    // Constante pour les colonnes de la feuille Artistes
    public static final Map<String, Integer> ARTISTE_COLUMNS;

    static {
        ARTISTE_COLUMNS = new LinkedHashMap<>();
        ARTISTE_COLUMNS.put("ID", 0);
        ARTISTE_COLUMNS.put("Nom", 1);
        ARTISTE_COLUMNS.put("Prenom", 2);
        ARTISTE_COLUMNS.put("Date Naissance", 3);
        ARTISTE_COLUMNS.put("Date Décès", 4);
    }

    // Constante pour les colonnes de la feuille Titres
    public static final Map<String, Integer> TITRE_COLUMNS;

    static {
        TITRE_COLUMNS = new LinkedHashMap<>();
        TITRE_COLUMNS.put("ID", 0);
        TITRE_COLUMNS.put("Nom", 1);
        TITRE_COLUMNS.put("Durée", 2);
        TITRE_COLUMNS.put("Artistes IDs", 3);
    }

    // Constante pour les colonnes de la feuille Albums
    public static final Map<String, Integer> ALBUM_COLUMNS;

    static {
        ALBUM_COLUMNS = new LinkedHashMap<>();
        ALBUM_COLUMNS.put("ID", 0);
        ALBUM_COLUMNS.put("Nom", 1);
        ALBUM_COLUMNS.put("Artiste IDs", 2);
        ALBUM_COLUMNS.put("Titre IDs", 3);
        ALBUM_COLUMNS.put("Taille", 4);
        ALBUM_COLUMNS.put("Status", 5);
    }

    public static int getPositionOfKey(Map<String, Integer> map, String key) {
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
