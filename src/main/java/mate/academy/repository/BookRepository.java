package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom,
        JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId")
    Page<Book> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN FETCH b.categories WHERE b.id = :id")
    Optional<Book> findByIdWithCategory(@Param("id") Long id);
}
