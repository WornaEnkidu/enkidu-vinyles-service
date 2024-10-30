package be.enkidu.vinyles.business.service;

import be.enkidu.vinyles.business.service.dto.TitreDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TitreService {

    public List<Map<String, String>> exportTitres() {
        List<TitreDTO> titresDTO = getTitresDTO(); // Récupère la liste des DTO
        List<Map<String, String>> titresMap = new ArrayList<>();

        for (TitreDTO titre : titresDTO) {
            Map<String, String> titreMap = new HashMap<>();
            titreMap.put("ID", titre.getId() != null ? titre.getId().toString() : "");
            titreMap.put("Nom", titre.getNom());
            titreMap.put("Durée", titre.getDuree() != null ? String.format("%02d:%02d", titre.getDuree() / 60, titre.getDuree() % 60) : "");

            // Concatène les IDs des artistes
            String artistesIds = String.join(",", titre.getArtistesIds().stream().map(String::valueOf).toList());
            titreMap.put("Artiste IDs", artistesIds);

            titresMap.add(titreMap);
        }

        return titresMap;
    }

    private List<TitreDTO> getTitresDTO() {
        // Création d'exemples de TitreDTO pour tester
        List<TitreDTO> titres = new ArrayList<>();

        // Crée des exemples de titres avec IDs d'artistes
        TitreDTO titre1 = new TitreDTO();
        titre1.setId(1L);
        titre1.setNom("Chanson A");
        titre1.setDuree(225); // Durée en secondes (3 min 45 s)
        titre1.setArtistesIds(List.of(1L)); // ID de l'artiste

        TitreDTO titre2 = new TitreDTO();
        titre2.setId(2L);
        titre2.setNom("Chanson B");
        titre2.setDuree(270); // Durée en secondes (4 min 30 s)
        titre2.setArtistesIds(List.of(2L)); // ID de l'artiste

        titres.add(titre1);
        titres.add(titre2);

        return titres;
    }
}
