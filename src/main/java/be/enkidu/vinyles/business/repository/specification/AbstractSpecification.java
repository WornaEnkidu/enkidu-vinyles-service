package be.enkidu.vinyles.business.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecification<T> implements Specification<T> {

    protected Predicate likeIgnoreCase(CriteriaBuilder cb, Expression<String> expression, String searchString) {
        return cb.like(cb.lower(expression), "%" + searchString.toLowerCase() + "%");
    }

    protected static Predicate and(CriteriaBuilder cb, List<Predicate> predicates) {
        return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    protected static Predicate or(CriteriaBuilder cb, List<Predicate> predicates) {
        return predicates.isEmpty() ? null : cb.or(predicates.toArray(new Predicate[predicates.size()]));
    }
}
