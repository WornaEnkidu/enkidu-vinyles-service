package be.enkidu.vinyles.business.repository;

import be.enkidu.vinyles.business.domain.Album;
import be.enkidu.vinyles.business.domain.Artiste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, JpaSpecificationExecutor<Album> {}
