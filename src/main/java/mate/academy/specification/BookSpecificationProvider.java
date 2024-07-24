package mate.academy.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.List;
import mate.academy.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationProvider {
    public Specification<Book> getSpecification(List<String> params, String fieldName) {
        return (root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<String> inPredicate = criteriaBuilder.in(root.get(fieldName));
            params.forEach(inPredicate::value);
            return criteriaBuilder.and(inPredicate);
        };
    }
}
