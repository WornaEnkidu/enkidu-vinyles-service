package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.ARTISTE_COLUMNS;

import be.enkidu.vinyles.business.domain.Artiste;
import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.repository.ArtisteRepository;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.mapper.ArtisteMapper;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtisteService {

    private final ArtisteMapper artisteMapper;
    private final ArtisteRepository artisteRepository;

    public ArtisteService(ArtisteMapper artisteMapper, ArtisteRepository artisteRepository) {
        this.artisteMapper = artisteMapper;
        this.artisteRepository = artisteRepository;
    }

    @Transactional
    public void deleteAllAndsave(List<ArtisteDTO> artistes) {
        this.artisteRepository.deleteAll();

        List<Artiste> artisteList = artistes.stream().map(artisteMapper::toEntity).collect(Collectors.toList());

        this.artisteRepository.saveAll(artisteList);
    }

    public ArtisteDTO saveArtiste(ArtisteDTO artisteDTO) {
        Artiste savedArtiste = this.artisteRepository.save(artisteMapper.toEntity(artisteDTO));
        return artisteMapper.toDto(savedArtiste);
    }

    public List<Map<String, String>> exportArtistes() {
        List<ArtisteDTO> artistesDTO = this.artisteRepository.findAll().stream().map(artisteMapper::toDto).collect(Collectors.toList());

        List<Map<String, String>> artistesMap = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (ArtisteDTO artiste : artistesDTO) {
            // Utilise LinkedHashMap pour préserver l'ordre
            Map<String, String> artisteMap = new LinkedHashMap<>();

            // Remplit le map en suivant l'ordre des colonnes défini dans ARTISTE_COLUMNS
            ARTISTE_COLUMNS.forEach((code, label) -> {
                switch (code) {
                    case "ID" -> artisteMap.put("ID", artiste.getId() != null ? artiste.getId().toString() : "");
                    case "NOM" -> artisteMap.put("NOM", artiste.getNomArtiste() != null ? artiste.getNomArtiste() : "");
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

    public List<ArtisteDTO> getArtistes() {
        return this.artisteRepository.findAll().stream().map(artisteMapper::toDto).collect(Collectors.toList());
    }

    public ArtisteDTO getArtiste(Long id) throws RessourceNotFoundException {
        return this.artisteRepository.findById(id)
            .map(artisteMapper::toDto)
            .orElseThrow(() -> new RessourceNotFoundException("Artiste not found"));
    }
}
