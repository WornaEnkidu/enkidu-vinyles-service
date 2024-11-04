package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.TITRE_COLUMNS;

import be.enkidu.vinyles.business.service.dto.TitreDTO;
import java.io.IOException;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class TitreService {

    private TemporaryDataStoreService temporaryDataStoreService;

    public TitreService(TemporaryDataStoreService temporaryDataStoreService) {
        this.temporaryDataStoreService = temporaryDataStoreService;
    }

    public List<Map<String, String>> exportTitres() throws IOException {
        List<TitreDTO> titresDTO = this.temporaryDataStoreService.getTitres(); // Récupère la liste des DTO
        List<Map<String, String>> titresMap = new ArrayList<>();

        for (TitreDTO titre : titresDTO) {
            // Utilise LinkedHashMap pour préserver l'ordre
            Map<String, String> titreMap = new LinkedHashMap<>();

            // Remplit le map en suivant l'ordre des colonnes défini dans TITRE_COLUMNS
            TITRE_COLUMNS.forEach((columnName, index) -> {
                switch (columnName) {
                    case "ID" -> titreMap.put("ID", titre.getId() != null ? titre.getId().toString() : "");
                    case "NOM" -> titreMap.put("NOM", titre.getNom());
                    case "DUREE" -> {
                        String duree = titre.getDuree() != null
                            ? String.format("%02d:%02d", titre.getDuree() / 60, titre.getDuree() % 60)
                            : "";
                        titreMap.put("DUREE", duree);
                    }
                    case "ARTISTE_IDS" -> {
                        String artistesIds = String.join(",", titre.getArtistesIds().stream().map(String::valueOf).toList());
                        titreMap.put("ARTISTE_IDS", artistesIds);
                    }
                }
            });

            titresMap.add(titreMap);
        }

        return titresMap;
    }

    public List<TitreDTO> getTitres() throws IOException {
        return this.temporaryDataStoreService.getTitres();
    }

    public TitreDTO saveTitre(TitreDTO titreDTO) throws IOException {
        return this.temporaryDataStoreService.saveTitre(titreDTO);
    }
}
