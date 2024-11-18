package be.enkidu.vinyles.business.service.mapper;

import be.enkidu.vinyles.business.domain.Album;
import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import be.enkidu.vinyles.business.service.dto.AlbumFormDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = { TitreMapper.class })
public interface AlbumMapper extends EntityMapper<AlbumFormDTO, Album> {
    @AfterMapping
    default void linkAlbumToTitres(@MappingTarget Album album) {
        if (album.getTitres() != null) {
            album.getTitres().forEach(titre -> titre.setAlbum(album));
        }
    }

    AlbumDTO toAlbumDto(Album entity);
}
