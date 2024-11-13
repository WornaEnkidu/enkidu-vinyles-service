package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.ALBUM_COLUMNS;

import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.AlbumFormDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private TemporaryDataStoreService temporaryDataStoreService;
    private final ArtisteService artisteService;
    private final TitreService titreService;
    private final EnhancedAlbumPdfGenerator pdfGenerator;

    public AlbumService(
        TemporaryDataStoreService temporaryDataStoreService,
        ArtisteService artisteService,
        TitreService titreService,
        EnhancedAlbumPdfGenerator pdfGenerator
    ) {
        this.temporaryDataStoreService = temporaryDataStoreService;
        this.artisteService = artisteService;
        this.titreService = titreService;
        this.pdfGenerator = pdfGenerator;
    }

    public List<Map<String, String>> exportAlbums() throws IOException {
        List<AlbumFormDTO> albumsDTO = this.temporaryDataStoreService.getAlbums();
        List<Map<String, String>> albumsMap = new ArrayList<>();

        for (AlbumFormDTO album : albumsDTO) {
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
                        String titresIds = String.join(",", album.getTitres().stream().map(String::valueOf).toList());
                        albumMap.put("TITRE_IDS", titresIds);
                    }
                    case "IMAGE" -> albumMap.put("IMAGE", album.getImage() != null ? album.getImage() : "");
                }
            });

            albumsMap.add(albumMap);
        }

        return albumsMap;
    }

    public List<AlbumFormDTO> getAlbumDTOs() throws IOException {
        return this.temporaryDataStoreService.getAlbums();
    }

    public List<AlbumDTO> getAlbums() throws IOException {
        List<AlbumFormDTO> formDTOS = this.temporaryDataStoreService.getAlbums();
        List<AlbumDTO> albums = new ArrayList<>();
        for (AlbumFormDTO albumForm : formDTOS) {
            AlbumDTO albumDTO = new AlbumDTO();
            albumDTO.setId(albumForm.getId());
            albumDTO.setNom(albumForm.getNom());
            albumDTO.setImage(albumForm.getImage());
            albumDTO.setStatus(albumForm.getStatus());
            albumDTO.setTaille(albumForm.getTaille());
            albumDTO.setArtistes(new ArrayList<>());
            albumDTO.setTitres(albumForm.getTitres());

            if (albumForm.getArtistesIds() != null) {
                for (long id : albumForm.getArtistesIds()) {
                    albumDTO.getArtistes().add(this.artisteService.getArtiste(id));
                }
            }
            albums.add(albumDTO);
        }
        return albums;
    }

    public AlbumFormDTO saveAlbum(AlbumFormDTO albumFormDTO) throws IOException {
        AtomicInteger index = new AtomicInteger(1); // Initialise l'index à 1
        albumFormDTO.getTitres().forEach(t -> t.setOrdre(index.getAndIncrement()));
        return this.temporaryDataStoreService.saveAlbum(albumFormDTO);
    }

    public AlbumFormDTO getAlbum(Long id) throws IOException, RessourceNotFoundException {
        List<AlbumFormDTO> albums = this.temporaryDataStoreService.getAlbums();

        return albums
            .stream()
            .filter(a -> a.getId() != null && a.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RessourceNotFoundException("Album not found"));
    }

    public byte[] generateAlbumsPdf() throws IOException {
        List<AlbumDTO> albums = getAlbums();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdfGenerator.generateAlbumPdf(baos, albums);

        return baos.toByteArray();
    }
}
