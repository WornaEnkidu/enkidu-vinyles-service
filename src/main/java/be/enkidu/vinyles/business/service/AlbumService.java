package be.enkidu.vinyles.business.service;

import static be.enkidu.vinyles.business.service.constant.ExcelColumnConstants.ALBUM_COLUMNS;

import be.enkidu.vinyles.business.domain.Album;
import be.enkidu.vinyles.business.domain.Artiste;
import be.enkidu.vinyles.business.excpetion.RessourceNotFoundException;
import be.enkidu.vinyles.business.repository.AlbumRepository;
import be.enkidu.vinyles.business.repository.TitreRepository;
import be.enkidu.vinyles.business.repository.critere.AlbumCritere;
import be.enkidu.vinyles.business.repository.specification.AlbumSpecification;
import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.AlbumFormDTO;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import be.enkidu.vinyles.business.service.mapper.AlbumMapper;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlbumService {

    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final TitreRepository titreRepository;
    private final EnhancedAlbumPdfGenerator pdfGenerator;

    public AlbumService(
        AlbumMapper albumMapper,
        AlbumRepository albumRepository,
        TitreRepository titreRepository,
        EnhancedAlbumPdfGenerator pdfGenerator
    ) {
        this.albumMapper = albumMapper;
        this.albumRepository = albumRepository;
        this.titreRepository = titreRepository;
        this.pdfGenerator = pdfGenerator;
    }

    public List<Map<String, String>> exportAlbums() {
        List<AlbumFormDTO> albumsDTO = this.getAlbumFormDTOs(null);
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
                        String artistesIds = "";
                        if (album.getArtistesIds() != null) {
                            artistesIds = String.join(",", album.getArtistesIds().stream().map(String::valueOf).toList());
                        }
                        albumMap.put("ARTISTE_IDS", artistesIds);
                    }
                    case "TITRE_IDS" -> {
                        String titresIds = "";
                        if (album.getTitres() != null) {
                            titresIds = String.join(",", album.getTitres().stream().map(TitreDTO::getId).map(String::valueOf).toList());
                        }
                        albumMap.put("TITRE_IDS", titresIds);
                    }
                    case "IMAGE" -> albumMap.put("IMAGE", album.getImage() != null ? album.getImage() : "");
                    case "PRIX" -> albumMap.put("PRIX", album.getPrix() != null ? String.valueOf(album.getPrix()) : "");
                }
            });

            albumsMap.add(albumMap);
        }

        return albumsMap;
    }

    public List<AlbumFormDTO> getAlbumFormDTOs(AlbumCritere critere) {
        return this.albumRepository.findAll(new AlbumSpecification(critere)).stream().map(albumMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteAllAndsave(List<AlbumFormDTO> albums) {
        this.albumRepository.deleteAll();
        this.titreRepository.deleteAll();

        List<Album> albumList = albums.stream().map(albumMapper::toEntity).collect(Collectors.toList());

        this.albumRepository.saveAll(albumList);
    }

    public AlbumFormDTO saveAlbum(AlbumFormDTO albumFormDTO) {
        Album album = this.albumMapper.toEntity(albumFormDTO);

        album.setArtistes(
            albumFormDTO
                .getArtistesIds()
                .stream()
                .map(a -> {
                    Artiste artiste = new Artiste();
                    artiste.setId(a);
                    return artiste;
                })
                .collect(Collectors.toList())
        );

        album = this.albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    public AlbumFormDTO getAlbum(Long id) throws RessourceNotFoundException {
        return this.albumRepository.findById(id)
            .map(albumMapper::toDto)
            .orElseThrow(() -> new RessourceNotFoundException("Album not found"));
    }

    public byte[] generateAlbumsPdf(AlbumCritere critere) {
        List<AlbumDTO> albums =
            this.albumRepository.findAll(new AlbumSpecification(critere))
                .stream()
                .map(albumMapper::toAlbumDto)
                .collect(Collectors.toList());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdfGenerator.generateAlbumPdf(baos, albums);

        return baos.toByteArray();
    }
}
