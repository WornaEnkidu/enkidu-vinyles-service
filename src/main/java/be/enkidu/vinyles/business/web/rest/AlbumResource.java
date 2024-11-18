package be.enkidu.vinyles.business.web.rest;

import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.service.AlbumService;
import be.enkidu.vinyles.business.service.dto.AlbumFormDTO;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/albums")
public class AlbumResource {

    private final AlbumService albumService;

    public AlbumResource(AlbumService albumService) {
        this.albumService = albumService;
    }

    /**
     * Endpoint pour récupérer la liste de tous les albums.
     *
     * @return La liste des albums en tant que DTO.
     */
    @GetMapping
    public ResponseEntity<List<AlbumFormDTO>> getAlbums() {
        return ResponseEntity.ok(albumService.getAlbumFormDTOs());
    }

    /**
     * Endpoint pour créer ou mettre à jour un album.
     *
     * @param albumFormDTO Le DTO de l'album à sauvegarder.
     * @return Le DTO de l'album avec son ID mis à jour, si applicable.
     */
    @PostMapping
    public ResponseEntity<AlbumFormDTO> saveAlbum(@RequestBody AlbumFormDTO albumFormDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.saveAlbum(albumFormDTO));
    }

    /**
     * Endpoint pour récupérer un album par ID.
     *
     * @param id L'ID de l'album à récupérer.
     * @return L'AlbumDTO correspondant ou une réponse 404 si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumFormDTO> getAlbum(@PathVariable Long id) {
        try {
            AlbumFormDTO album = albumService.getAlbum(id);
            return ResponseEntity.ok(album);
        } catch (RessourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> generateAlbumsPdf() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "albums_catalogue.pdf");

            return new ResponseEntity<>(albumService.generateAlbumsPdf(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
