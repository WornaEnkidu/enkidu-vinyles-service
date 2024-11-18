package be.enkidu.vinyles.business.service.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

public interface EntityMapper<D, E> {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    E toEntity(D dtoSource, @MappingTarget E entityTarget);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    E toEntity(D dtoSource);

    D toDto(E entity);

    List<D> toDto(List<E> entity);

    Set<D> toDto(Set<E> entity);
}
