package be.enkidu.vinyles.business.web.rest;

import be.enkidu.vinyles.business.service.ArtisteService;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artistes")
public class ArtisteResource {

    private final ArtisteService artisteService;

    @Autowired
    public ArtisteResource(ArtisteService artisteService) {
        this.artisteService = artisteService;
    }

    /**
     * Endpoint pour créer ou mettre à jour un artiste.
     *
     * @param artisteDTO Le DTO de l'artiste à sauvegarder.
     * @return Le DTO de l'artiste avec son ID mis à jour, si applicable.
     */
    @PostMapping
    public ResponseEntity<ArtisteDTO> saveArtiste(@RequestBody ArtisteDTO artisteDTO) {
        try {
            ArtisteDTO savedArtiste = artisteService.saveArtiste(artisteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedArtiste);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
