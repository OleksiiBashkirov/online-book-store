package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.model.Category;
import mate.academy.repository.BookRepository;
import mate.academy.service.impl.BookServiceImpl;
import mate.academy.specification.BookSpecificationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final Long ID = 1L;
    private Book book;
    private BookDto bookDto;
    private CreateBookRequestDto requestDto;
    private Category category;

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationProvider bookSpecificationProvider;

    @BeforeEach
    void setup() {
        category = createCategory(ID, "Category1", "Description1");
        book = createBook(ID, "Book1", "Author1", "111222333444",
                BigDecimal.TEN, Set.of(category));
        bookDto = createBookDto(book);
        requestDto = createBookRequestDto(bookDto);
    }

    @Test
    void save_ValidBook_ReturnBookDto() {
        when(bookMapper.toModel(any(CreateBookRequestDto.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        BookDto actual = bookService.save(requestDto);
        String expected = bookDto.getTitle();

        assertNotNull(actual);
        assertEquals(expected, actual.getTitle());

        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    void save_BookWithEmptyCategory_ReturnBookDto() {
        book.setCategories(Collections.emptySet());

        when(bookMapper.toModel(any(CreateBookRequestDto.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        BookDto actual = bookService.save(requestDto);
        String expected = bookDto.getTitle();

        assertNotNull(actual);
        assertEquals(expected, actual.getTitle());

        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    void findAll_NotEmptyPage_ReturnPageOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        Page<BookDto> actual = bookService.findAll(pageable);

        assertNotNull(actual);
        assertEquals(1L, actual.getContent().size());

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findById_ValidId_ReturnBookDto() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        BookDto actual = bookService.findById(1L);
        String expected = bookDto.getTitle();

        assertNotNull(actual);
        assertEquals(expected, actual.getTitle());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        Long bookId = -1L;

        when(bookRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Book not found by id:" + bookId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(bookId)
        );

        assertEquals("Book not found by id:" + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void update_ValidId_ReturnsUpdatedBookDto() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto actual = bookService.update(1L, requestDto);
        String expected = bookDto.getTitle();

        assertNotNull(actual);
        assertEquals(expected, actual.getTitle());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).updateModel(book, requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void update_InvalidId_ThrowsEntityNotFoundException() {
        Long bookId = -1L;

        when(bookRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Book not found by id:" + bookId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(bookId, requestDto));

        assertEquals("Book not found by id:" + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void deleteById_ValidId_Success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertDoesNotThrow(() -> bookService.deleteById(1L));

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void deleteById_InvalidId_ThrowsEntityNotFoundException() {
        Long bookId = -1L;
        when(bookRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Book not found by id:" + bookId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteById(bookId));

        assertEquals("Book not found by id:" + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void searchBooks_WithValidParameters_ReturnListOfBookDto() {
        List<Book> books = Collections.singletonList(book);
        when(bookRepository.findAll(any(Specification.class))).thenReturn(books);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        List<BookDto> actual = bookService.searchBooks(Collections.emptyMap());

        assertNotNull(actual);
        assertEquals(1L, actual.size());

        verify(bookRepository, times(1)).findAll(any(Specification.class));
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void searchBooks_WithInvalidParameters_ReturnEmptyListOfBookDto() {
        when(bookRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<BookDto> actual = bookService.searchBooks(Collections.singletonMap("invalid",
                List.of("value")));

        assertNotNull(actual);
        assertEquals(0, actual.size());

        verify(bookRepository, times(1)).findAll(any(Specification.class));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void searchBooks_LargeDataset_ReturnExpectedResult() {
        List<Book> largeBookList = createLargeBookList(1000);
        when(bookRepository.findAll(any(Specification.class))).thenReturn(largeBookList);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        List<BookDto> actual = bookService.searchBooks(
                Collections.singletonMap("title", List.of("Book1"))
        );

        assertNotNull(actual);
        assertEquals(1000, actual.size());

        verify(bookRepository, times(1)).findAll(any(Specification.class));
        verify(bookMapper, times(1000)).toDto(any(Book.class));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findAllByCategoryId_ValidCategoryId_ReturnPageOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds =
                createBookDtoWithoutCategoryIds(book);

        when(bookRepository.findAllByCategoryId(anyLong(), any(Pageable.class)))
                .thenReturn(bookPage);
        when(bookMapper.toDtoWithoutCategories(any(Book.class)))
                .thenReturn(bookDtoWithoutCategoryIds);

        Page<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(
                1L, pageable);

        assertNotNull(actual);
        assertEquals(1L, actual.getContent().size());

        verify(bookRepository, times(1))
                .findAllByCategoryId(1L, pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategories(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findAllByCategoryId_InvalidCategoryId_ReturnEmptyPage() {
        Long categoryId = -1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = Page.empty();

        when(bookRepository.findAllByCategoryId(anyLong(), any(Pageable.class)))
                .thenReturn(bookPage);

        Page<BookDtoWithoutCategoryIds> actual =
                bookService.findAllByCategoryId(categoryId, pageable);

        assertNotNull(actual);
        assertEquals(0, actual.getContent().size());

        verify(bookRepository, times(1)).findAllByCategoryId(categoryId, pageable);
        verifyNoMoreInteractions(bookRepository);
    }

    private Category createCategory(Long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private Book createBook(Long id, String title, String author, String isbn,
                            BigDecimal price, Set<Category> category) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setCategories(category);
        return book;
    }

    private BookDto createBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        return bookDto;
    }

    private CreateBookRequestDto createBookRequestDto(BookDto bookDto) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(bookDto.getTitle());
        requestDto.setAuthor(bookDto.getAuthor());
        requestDto.setIsbn(bookDto.getIsbn());
        requestDto.setPrice(bookDto.getPrice());
        return requestDto;
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds(Book book) {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setId(book.getId());
        bookDtoWithoutCategoryIds.setTitle(book.getTitle());
        bookDtoWithoutCategoryIds.setAuthor(book.getAuthor());
        bookDtoWithoutCategoryIds.setIsbn(book.getIsbn());
        bookDtoWithoutCategoryIds.setPrice(book.getPrice());
        return bookDtoWithoutCategoryIds;
    }

    private List<Book> createLargeBookList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createBook(
                        (long) i,
                        "Book" + i,
                        "Author" + i,
                        "ISBN" + String.format("%013d", i),
                        BigDecimal.valueOf(i + 10),
                        Set.of(createCategory(
                                (long) i % 10,
                                "Category" + (i % 10),
                                "Description" + (i % 10)))
                ))
                .toList();
    }
}
