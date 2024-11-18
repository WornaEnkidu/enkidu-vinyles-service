package be.enkidu.vinyles.business.service.mapper;

import be.enkidu.vinyles.business.domain.Album;
import be.enkidu.vinyles.business.service.dto.AlbumDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = { TitreMapper.class })
public interface AlbumMapper extends EntityMapper<AlbumDTO, Album> {}
