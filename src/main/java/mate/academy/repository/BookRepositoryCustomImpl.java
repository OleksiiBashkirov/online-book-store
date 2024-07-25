package mate.academy.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mate.academy.model.Book;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Book> findAllBySearchParams(Map<String, List<String>> params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        var rootBook = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        params.forEach((key, value) -> {
            if (!value.isEmpty()) {
                CriteriaBuilder.In<String> inClause = cb.in(rootBook.get(key));
                value.forEach(inClause::value);
                predicates.add(inClause);
            }
        });
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(cq).getResultList();
    }
}
