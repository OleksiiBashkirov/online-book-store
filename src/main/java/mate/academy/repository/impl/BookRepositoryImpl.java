package mate.academy.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepository {
    private final EntityManagerFactory emf;

    @Override
    public Book save(Book book) {
        EntityTransaction tx = null;
        try (EntityManager em = emf.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(book);
            tx.commit();
            return book;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Unable to save a book: " + book, e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            var book = em.find(Book.class, id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            throw new RuntimeException("Unable to find book with id: " + id, e);
        }
    }

    @Override
    public List<Book> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Unable to find all books from DB.", e);
        }
    }
}
