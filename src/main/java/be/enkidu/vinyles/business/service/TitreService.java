package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.TITRE_COLUMNS;

import be.enkidu.vinyles.business.repository.TitreRepository;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import be.enkidu.vinyles.business.service.mapper.TitreMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TitreService {

    private final TitreRepository titreRepository;
    private final TitreMapper titreMapper;

    public TitreService(TitreRepository titreRepository, TitreMapper titreMapper) {
        this.titreRepository = titreRepository;
        this.titreMapper = titreMapper;
    }

    public List<Map<String, String>> exportTitres() {
        List<TitreDTO> titresDTO = this.titreRepository.findAll().stream().map(titreMapper::toDto).collect(Collectors.toList());

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
                        String artistesIds = "";

                        if (titre.getArtistesIds() != null) {
                            artistesIds = String.join(",", titre.getArtistesIds().stream().map(String::valueOf).toList());
                        }
                        titreMap.put("ARTISTE_IDS", artistesIds);
                    }
                }
            });

            titresMap.add(titreMap);
        }

        return titresMap;
    }

    public List<TitreDTO> getTitres() {
        return this.titreRepository.findAll().stream().map(titreMapper::toDto).collect(Collectors.toList());
    }
}
