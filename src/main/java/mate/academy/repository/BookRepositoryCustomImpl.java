package mate.academy.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mate.academy.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Book> findAllBySearchParams(Map<String, List<String>> params) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Book.class);
        var rootBook = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        params.forEach(
                (key, value) -> addPredicatesIfNotEmpty(key, value, cb, rootBook, predicates)
        );
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(cq).getResultList();
    }

    private static void addPredicatesIfNotEmpty(String key, List<String> value,
            CriteriaBuilder cb,Root<Book> rootBook,List<Predicate> predicates) {
        if (!value.isEmpty()) {
            CriteriaBuilder.In<String> inClause = cb.in(rootBook.get(key));
            value.forEach(inClause::value);
            predicates.add(inClause);
        }
    }
}
