package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.ARTISTE_COLUMNS;

import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class ArtisteService {

    public List<Map<String, String>> exportArtistes() {
        List<ArtisteDTO> artistesDTO = getArtistesDTO(); // Récupère la liste des DTO
        List<Map<String, String>> artistesMap = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (ArtisteDTO artiste : artistesDTO) {
            // Utilise LinkedHashMap pour préserver l'ordre
            Map<String, String> artisteMap = new LinkedHashMap<>();

            // Remplit le map en suivant l'ordre des colonnes défini dans ARTISTE_COLUMNS
            ARTISTE_COLUMNS.forEach((columnName, index) -> {
                switch (columnName) {
                    case "ID" -> artisteMap.put("ID", artiste.getId() != null ? artiste.getId().toString() : "");
                    case "Nom" -> artisteMap.put("Nom", artiste.getNom() != null ? artiste.getNom() : "");
                    case "Prenom" -> artisteMap.put("Prenom", artiste.getPrenom() != null ? artiste.getPrenom() : "");
                    case "Date Naissance" -> {
                        String dateNaissance = artiste.getDateNaissance() != null ? dateFormat.format(artiste.getDateNaissance()) : "";
                        artisteMap.put("Date Naissance", dateNaissance);
                    }
                    case "Date Décès" -> {
                        String dateDeces = artiste.getDateDeces() != null ? dateFormat.format(artiste.getDateDeces()) : "";
                        artisteMap.put("Date Décès", dateDeces);
                    }
                }
            });

            artistesMap.add(artisteMap);
        }

        return artistesMap;
    }

    private List<ArtisteDTO> getArtistesDTO() {
        // Création de la liste d'ArtisteDTO (similaire à l'exemple précédent)
        List<ArtisteDTO> artistes = new ArrayList<>();

        ArtisteDTO artiste1 = new ArtisteDTO();
        artiste1.setId(1L);
        artiste1.setNom("Doe");
        artiste1.setPrenom("John");
        artiste1.setDateNaissance(new Date(1980 - 1900, 0, 1));

        ArtisteDTO artiste2 = new ArtisteDTO();
        artiste2.setId(2L);
        artiste2.setNom("Smith");
        artiste2.setPrenom("Jane");
        artiste2.setDateNaissance(new Date(1985 - 1900, 4, 15));

        artistes.add(artiste1);
        artistes.add(artiste2);

        return artistes;
    }
}
