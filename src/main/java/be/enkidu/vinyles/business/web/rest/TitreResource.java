package be.enkidu.vinyles.business.web.rest;

import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.service.TitreService;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/titres")
public class TitreResource {

    private final TitreService titreService;

    public TitreResource(TitreService titreService) {
        this.titreService = titreService;
    }

    /**
     * Endpoint pour récupérer la liste de tous les titres.
     *
     * @return La liste des titres en tant que DTO.
     */
    @GetMapping
    public ResponseEntity<List<TitreDTO>> getTitres() {
        try {
            List<TitreDTO> titres = titreService.getTitres();
            return ResponseEntity.ok(titres);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour créer ou mettre à jour un titre.
     *
     * @param titreDTO Le DTO du titre à sauvegarder.
     * @return Le DTO du titre avec son ID mis à jour, si applicable.
     */
    @PostMapping
    public ResponseEntity<TitreDTO> saveTitre(@RequestBody TitreDTO titreDTO) {
        try {
            TitreDTO savedTitre = titreService.saveTitre(titreDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTitre);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour récupérer un titre par ID.
     *
     * @param id L'ID de l'titre à récupérer.
     * @return L'TitreDTO correspondant ou une réponse 404 si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TitreDTO> getTitre(@PathVariable Long id) {
        try {
            TitreDTO titre = titreService.getTitre(id);
            return ResponseEntity.ok(titre);
        } catch (RessourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
