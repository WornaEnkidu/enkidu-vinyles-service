package be.enkidu.vinyles.business.web.rest;

import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.service.AlbumService;
import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<AlbumDTO>> getAlbums() {
        try {
            List<AlbumDTO> albums = albumService.getAlbums();
            return ResponseEntity.ok(albums);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour créer ou mettre à jour un album.
     *
     * @param albumDTO Le DTO de l'album à sauvegarder.
     * @return Le DTO de l'album avec son ID mis à jour, si applicable.
     */
    @PostMapping
    public ResponseEntity<AlbumDTO> saveAlbum(@RequestBody AlbumDTO albumDTO) {
        try {
            AlbumDTO savedAlbum = albumService.saveAlbum(albumDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAlbum);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour récupérer un album par ID.
     *
     * @param id L'ID de l'album à récupérer.
     * @return L'AlbumDTO correspondant ou une réponse 404 si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getAlbum(@PathVariable Long id) {
        try {
            AlbumDTO album = albumService.getAlbum(id);
            return ResponseEntity.ok(album);
        } catch (RessourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
