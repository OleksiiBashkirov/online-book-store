package mate.academy.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository
                .findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by id: " + id));
        bookMapper.updateModel(book, requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found by id: " + id));
        book.setDeleted(true);
        bookRepository.save(book);
    }

    @Override
    public List<BookDto> searchBooks(Map<String, List<String>> searchParameters) {
        Specification<Book> specification = Specification.where(null);
        for (Map.Entry<String, List<String>> entry : searchParameters.entrySet()) {
            Specification<Book> sp = bookSpecificationProvider
                    .getSpecification(entry.getValue(), entry.getKey());
            specification = specification.and(sp);
        }
        return bookRepository.findAll(specification).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> findAllByCategoryId(
            Long categoryId,
            Pageable pageable
    ) {
        Page<Book> bookPage = bookRepository.findAllByCategoriesId(categoryId, pageable);
        List<BookDtoWithoutCategoryIds> bookDtoList = bookPage.stream()
                .map(bookMapper::toDtoWithoutCategories)
                .collect(Collectors.toList());
        return new PageImpl<>(bookDtoList, pageable, bookPage.getTotalElements());
    }
}
