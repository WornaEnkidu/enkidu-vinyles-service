package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.ARTISTE_COLUMNS;

import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class ArtisteService {

    private TemporaryDataStoreService temporaryDataStoreService;

    public ArtisteService(TemporaryDataStoreService temporaryDataStoreService) {
        this.temporaryDataStoreService = temporaryDataStoreService;
    }

    public ArtisteDTO saveArtiste(ArtisteDTO artisteDTO) throws IOException {
        return this.temporaryDataStoreService.saveArtiste(artisteDTO);
    }

    public List<Map<String, String>> exportArtistes() throws IOException {
        List<ArtisteDTO> artistesDTO = this.temporaryDataStoreService.getArtistes(); // Récupère la liste des DTO
        List<Map<String, String>> artistesMap = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (ArtisteDTO artiste : artistesDTO) {
            // Utilise LinkedHashMap pour préserver l'ordre
            Map<String, String> artisteMap = new LinkedHashMap<>();

            // Remplit le map en suivant l'ordre des colonnes défini dans ARTISTE_COLUMNS
            ARTISTE_COLUMNS.forEach((code, label) -> {
                switch (code) {
                    case "ID" -> artisteMap.put("ID", artiste.getId() != null ? artiste.getId().toString() : "");
                    case "NOM" -> artisteMap.put("NOM", artiste.getNom() != null ? artiste.getNom() : "");
                    case "PRENOM" -> artisteMap.put("PRENOM", artiste.getPrenom() != null ? artiste.getPrenom() : "");
                    case "IMAGE" -> artisteMap.put("IMAGE", artiste.getImage() != null ? artiste.getImage() : "");
                    case "DATE_NAISSANCE" -> {
                        String dateNaissance = artiste.getDateNaissance() != null ? dateFormat.format(artiste.getDateNaissance()) : "";
                        artisteMap.put("DATE_NAISSANCE", dateNaissance);
                    }
                    case "DATE_DECES" -> {
                        String dateDeces = artiste.getDateDeces() != null ? dateFormat.format(artiste.getDateDeces()) : "";
                        artisteMap.put("DATE_DECES", dateDeces);
                    }
                }
            });

            artistesMap.add(artisteMap);
        }

        return artistesMap;
    }

    public List<ArtisteDTO> getArtistes() throws IOException {
        return this.temporaryDataStoreService.getArtistes();
    }
}
