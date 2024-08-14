package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import mate.academy.config.CustomMySqlContainer;
import mate.academy.model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = "classpath:database/books/setup_books_with_categories.sql",
        executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/books/cleanup_books_with_categories.sql",
        executionPhase = AFTER_TEST_METHOD)
class BookRepositoryTest {
    @Getter
    @Setter
    @Container
    private static CustomMySqlContainer mysqlContainer = CustomMySqlContainer.getInstance();

    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void setUp() {
        System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
        System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
    }

    @Test
    @DisplayName("Find all books by category ID")
    void findAllByCategoryId_ValidCategoryId_ReturnTwoBooks() {
        long categoryId = 1L;

        Page<Book> actual = bookRepository.findAllByCategoryId(categoryId,
                PageRequest.of(0, 10));
        int expectedSize = 2;

        assertEquals(expectedSize, actual.getContent().size());
        assertEquals("Book1", actual.getContent().get(0).getTitle());
        assertEquals("Book2", actual.getContent().get(1).getTitle());
    }

    @Test
    @DisplayName("Not find any book by invalid category ID")
    void findAllByCategoryId_InvalidCategoryId_ReturnEmptyList() {
        long categoryId = -1L;

        Page<Book> actual = bookRepository.findAllByCategoryId(
                categoryId, PageRequest.of(0, 10)
        );
        int expectedSize = 0;

        assertEquals(expectedSize, actual.getContent().size());
    }

    @Test
    @DisplayName("Find all books by empty category")
    void findAllByCategoryId_EmptyCategory_NoBookFound() {
        long categoryId = 3L;

        Page<Book> actual = bookRepository.findAllByCategoryId(
                categoryId, PageRequest.of(0, 10)
        );
        int expectedSize = 0;

        assertEquals(expectedSize, actual.getContent().size());
    }

    @Test
    @DisplayName("Find book by ID along with its categories")
    void findByIdWithCategory_ValidBookId_BookWithCategoryFound() {
        long bookId = 1L;

        Optional<Book> actual = bookRepository.findByIdWithCategory(bookId);
        String expectedTitle = "Book1";

        assertTrue(actual.isPresent());
        assertEquals(expectedTitle, actual.get().getTitle());
        assertFalse(actual.get().getCategories().isEmpty());
    }

    @Test
    @DisplayName("Fail to find book by a non-existent ID")
    void findByIdWithCategory_InvalidBookId_NoBookFound() {
        long bookId = -1L;

        Optional<Book> actual = bookRepository.findByIdWithCategory(bookId);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find all books by title and author")
    void findAllBySearchParams_TitleAndAuthor_ReturnMatchingBooks() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("title", List.of("Book1", "Book3"));
        params.put("author", List.of("Author1"));

        List<Book> actual = bookRepository.findAllBySearchParams(params);
        int expectedSize = 1;

        assertEquals(expectedSize, actual.size());
        assertEquals("Book1", actual.get(0).getTitle());
    }

    @Test
    @DisplayName("Find all books by ISBN")
    void findAllBySearchParams_Isbn_ReturnMatchingBooks() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("isbn", List.of("111222333444555"));

        List<Book> actual = bookRepository.findAllBySearchParams(params);
        int expectedSize = 1;

        assertEquals(expectedSize, actual.size());
        assertEquals("Book1", actual.get(0).getTitle());
    }

    @Test
    @DisplayName("No books found with unmatched parameters")
    void findAllBySearchParams_UnmatchedParams_ReturnEmptyList() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("title", List.of("NonExistentBook"));
        params.put("author", List.of("NonExistentAuthor"));

        List<Book> actual = bookRepository.findAllBySearchParams(params);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find all books by empty search parameters")
    void findAllBySearchParams_EmptyParams_ReturnAllBooks() {
        Map<String, List<String>> params = new HashMap<>();

        List<Book> actual = bookRepository.findAllBySearchParams(params);
        int expectedSize = 4;

        assertEquals(expectedSize, actual.size());
    }
}
