package mate.academy.repository;

import java.util.List;
import java.util.Map;
import mate.academy.model.Book;

public interface BookRepositoryCustom {
    List<Book> findAllBySearchParams(Map<String, List<String>> params);
}
