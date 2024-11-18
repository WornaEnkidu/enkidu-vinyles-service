package be.enkidu.vinyles.business.repository;

import be.enkidu.vinyles.business.domain.Titre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TitreRepository extends JpaRepository<Titre, Long>, JpaSpecificationExecutor<Titre> {}
