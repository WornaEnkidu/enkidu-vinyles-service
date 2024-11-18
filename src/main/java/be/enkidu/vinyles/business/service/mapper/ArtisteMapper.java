package be.enkidu.vinyles.business.service.mapper;

import be.enkidu.vinyles.business.domain.Artiste;
import be.enkidu.vinyles.business.service.dto.ArtisteDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ArtisteMapper extends EntityMapper<ArtisteDTO, Artiste> {}
