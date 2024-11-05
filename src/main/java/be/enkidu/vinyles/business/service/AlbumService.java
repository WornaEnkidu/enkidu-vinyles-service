package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.ALBUM_COLUMNS;

import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private TemporaryDataStoreService temporaryDataStoreService;

    public AlbumService(TemporaryDataStoreService temporaryDataStoreService) {
        this.temporaryDataStoreService = temporaryDataStoreService;
    }

    public List<Map<String, String>> exportAlbums() throws IOException {
        List<AlbumDTO> albumsDTO = this.temporaryDataStoreService.getAlbums();
        List<Map<String, String>> albumsMap = new ArrayList<>();

        for (AlbumDTO album : albumsDTO) {
            Map<String, String> albumMap = new LinkedHashMap<>();
            ALBUM_COLUMNS.forEach((columnName, index) -> {
                switch (columnName) {
                    case "ID" -> albumMap.put("ID", album.getId() != null ? album.getId().toString() : "");
                    case "NOM" -> albumMap.put("NOM", album.getNom());
                    case "TAILLE" -> albumMap.put("TAILLE", album.getTaille());
                    case "STATUS" -> albumMap.put("STATUS", album.getStatus() != null ? album.getStatus() : "");
                    case "ARTISTE_IDS" -> {
                        String artistesIds = String.join(",", album.getArtistesIds().stream().map(String::valueOf).toList());
                        albumMap.put("ARTISTE_IDS", artistesIds);
                    }
                    case "TITRE_IDS" -> {
                        String titresIds = String.join(",", album.getTitresIds().stream().map(String::valueOf).toList());
                        albumMap.put("TITRE_IDS", titresIds);
                    }
                    case "IMAGE" -> albumMap.put("IMAGE", album.getImage() != null ? album.getImage() : "");
                }
            });

            albumsMap.add(albumMap);
        }

        return albumsMap;
    }

    public List<AlbumDTO> getAlbums() throws IOException {
        return this.temporaryDataStoreService.getAlbums();
    }

    public AlbumDTO saveAlbum(AlbumDTO albumDTO) throws IOException {
        return this.temporaryDataStoreService.saveAlbum(albumDTO);
    }

    public AlbumDTO getAlbum(Long id) throws IOException, RessourceNotFoundException {
        List<AlbumDTO> albums = this.temporaryDataStoreService.getAlbums();

        return albums
            .stream()
            .filter(a -> a.getId() != null && a.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RessourceNotFoundException("Album not found"));
    }
}
