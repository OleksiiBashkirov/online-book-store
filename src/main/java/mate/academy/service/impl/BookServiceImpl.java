package mate.academy.service.impl;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import mate.academy.service.BookService;
import mate.academy.specification.BookSpecificationProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationProvider bookSpecificationProvider;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        var book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto findById(Long id) {
        var book = getBook(id);
        return bookMapper.toDto(book);
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found by id: " + id));
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        var book = getBook(id);
        bookMapper.updateModel(book, requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        var book = getBook(id);
        book.setDeleted(true);
        bookRepository.save(book);
    }

    @Override
    public List<BookDto> searchBooks(Map<String, List<String>> searchParameters) {
        Specification<Book> specification = Specification.where(null);
        for (var entry : searchParameters.entrySet()) {
            var sp = bookSpecificationProvider.getSpecification(entry.getValue(), entry.getKey());
            specification = specification.and(sp);
        }
        return bookRepository.findAll(specification).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId,
            Pageable pageable) {
        var bookPage = bookRepository.findAllByCategoryId(categoryId, pageable);
        var bookDtoList = bookPage.stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();

        return new PageImpl<>(bookDtoList, pageable, bookPage.getTotalElements());
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found by id: " + id));
    }
}
