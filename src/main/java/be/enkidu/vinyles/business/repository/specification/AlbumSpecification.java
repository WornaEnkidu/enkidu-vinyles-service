package be.enkidu.vinyles.business.repository.specification;

import be.enkidu.vinyles.business.domain.Album;
import be.enkidu.vinyles.business.domain.Album_;
import be.enkidu.vinyles.business.repository.critere.AlbumCritere;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class AlbumSpecification extends AbstractSpecification<Album> {

    private final transient AlbumCritere critere;

    public AlbumSpecification(AlbumCritere critere) {
        this.critere = critere;
    }

    @Override
    public Predicate toPredicate(Root<Album> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(critere.getStatus())
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .ifPresent(status -> predicates.add(this.getStatusPredicate(root, cb, status)));

        return and(cb, predicates);
    }

    private Predicate getStatusPredicate(Root<Album> root, CriteriaBuilder cb, String status) {
        return cb.equal(cb.lower(root.get(Album_.STATUS)), status.toLowerCase());
    }
}
