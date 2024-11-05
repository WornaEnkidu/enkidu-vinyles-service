package be.enkidu.vinyles.business.web.rest;

import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.service.ArtisteService;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import java.io.IOException;
import java.util.List;
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

    /**
     * Endpoint pour récupérer la liste de tous les artistes.
     *
     * @return La liste des artistes en tant que DTO.
     */
    @GetMapping
    public ResponseEntity<List<ArtisteDTO>> getArtistes() {
        try {
            List<ArtisteDTO> artistes = artisteService.getArtistes();
            return ResponseEntity.ok(artistes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour récupérer un artiste par ID.
     *
     * @param id L'ID de l'artiste à récupérer.
     * @return L'ArtisteDTO correspondant ou une réponse 404 si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtisteDTO> getArtiste(@PathVariable Long id) {
        try {
            ArtisteDTO artiste = artisteService.getArtiste(id);
            return ResponseEntity.ok(artiste);
        } catch (RessourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
