package be.enkidu.vinyles.business.service;

import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    public List<Map<String, String>> exportAlbums() {
        List<AlbumDTO> albumsDTO = getAlbumsDTO(); // Récupère la liste des DTO
        List<Map<String, String>> albumsMap = new ArrayList<>();

        for (AlbumDTO album : albumsDTO) {
            Map<String, String> albumMap = new HashMap<>();
            albumMap.put("ID", album.getId() != null ? album.getId().toString() : "");
            albumMap.put("Nom", album.getNom());
            albumMap.put("Taille", album.getTaille());
            albumMap.put("Status", album.getStatus() != null ? album.getStatus() : "");

            // Concatène les IDs des artistes
            String artistesIds = String.join(",", album.getArtistesIds().stream().map(String::valueOf).toList());
            albumMap.put("Artiste IDs", artistesIds);

            // Concatène les IDs des titres
            String titresIds = String.join(",", album.getTitresIds().stream().map(String::valueOf).toList());
            albumMap.put("Titre IDs", titresIds);

            albumsMap.add(albumMap);
        }

        return albumsMap;
    }

    private List<AlbumDTO> getAlbumsDTO() {
        // Création d'exemples de AlbumDTO pour tester
        List<AlbumDTO> albums = new ArrayList<>();

        AlbumDTO album1 = new AlbumDTO();
        album1.setId(1L);
        album1.setNom("Album 1");
        album1.setTaille("33t");
        album1.setStatus("Disponible");
        album1.setArtistesIds(List.of(1L, 2L)); // IDs des artistes
        album1.setTitresIds(List.of(1L, 2L)); // IDs des titres

        AlbumDTO album2 = new AlbumDTO();
        album2.setId(2L);
        album2.setNom("Album 2");
        album2.setTaille("45t");
        album2.setStatus("Indisponible");
        album2.setArtistesIds(List.of(2L)); // IDs des artistes
        album2.setTitresIds(List.of(3L)); // IDs des titres

        albums.add(album1);
        albums.add(album2);

        return albums;
    }
}
