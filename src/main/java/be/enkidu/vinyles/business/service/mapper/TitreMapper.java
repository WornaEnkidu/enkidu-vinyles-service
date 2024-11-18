package be.enkidu.vinyles.business.service.mapper;

import be.enkidu.vinyles.business.domain.Titre;
import be.enkidu.vinyles.business.service.dto.TitreDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TitreMapper extends EntityMapper<TitreDTO, Titre> {}
