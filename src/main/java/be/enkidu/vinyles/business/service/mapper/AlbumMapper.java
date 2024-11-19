package be.enkidu.vinyles.business.service.mapper;

import be.enkidu.vinyles.business.domain.Album;
import be.enkidu.vinyles.business.domain.Artiste;
import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.AlbumFormDTO;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = { TitreMapper.class, ArtisteMapper.class })
public interface AlbumMapper extends EntityMapper<AlbumFormDTO, Album> {
    @AfterMapping
    default void linkAlbumToTitres(@MappingTarget Album album) {
        if (album.getTitres() != null) {
            album.getTitres().forEach(titre -> titre.setAlbum(album));
        }
    }

    AlbumDTO toAlbumDto(Album entity);

    @AfterMapping
    default void mapArtistesToIds(Album entity, @MappingTarget AlbumFormDTO dto) {
        if (entity.getArtistes() != null) {
            dto.setArtistesIds(
                entity
                    .getArtistes()
                    .stream()
                    .map(Artiste::getId) // Extraire les IDs
                    .collect(Collectors.toList())
            );
        }
    }
}
