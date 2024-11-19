package be.enkidu.vinyles.business.repository.specification;

import be.enkidu.vinyles.business.domain.*;
import be.enkidu.vinyles.business.repository.critere.AlbumCritere;
import jakarta.persistence.criteria.*;
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

        if (critere != null) {
            Optional.ofNullable(critere.getStatus())
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .ifPresent(status -> predicates.add(this.getStatusPredicate(root, cb, status)));

            Optional.ofNullable(critere.getSearch())
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .ifPresent(search -> predicates.add(this.getSearchPredicate(root, cb, search)));
        }

        return and(cb, predicates);
    }

    private Predicate getStatusPredicate(Root<Album> root, CriteriaBuilder cb, String status) {
        return cb.equal(cb.lower(root.get(Album_.STATUS)), status.toLowerCase());
    }

    private Predicate getSearchPredicate(Root<Album> root, CriteriaBuilder cb, String search) {
        Join<Album, Titre> titreJoin = root.join(Album_.TITRES, JoinType.LEFT);
        Join<Titre, Artiste> titreArtisteJoin = titreJoin.join(Titre_.ARTISTES, JoinType.LEFT);
        Join<Album, Artiste> albumArtisteJoin = root.join(Album_.ARTISTES, JoinType.LEFT);

        return cb.or(
            likeIgnoreCase(cb, root.get(Album_.NOM), search),
            likeIgnoreCase(cb, titreJoin.get(Titre_.NOM), search),
            likeIgnoreCase(cb, titreArtisteJoin.get(Artiste_.NOM_ARTISTE), search),
            likeIgnoreCase(cb, albumArtisteJoin.get(Artiste_.NOM_ARTISTE), search)
        );
    }
}
