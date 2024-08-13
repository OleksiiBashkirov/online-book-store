package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CreateCategoryRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import mate.academy.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final Long ID = 1L;
    private Category category;
    private CategoryDto categoryDto;
    private CreateCategoryRequestDto requestDto;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setup() {
        category = createCategory(ID, "Category1", "Description1");
        categoryDto = createCategoryDto(category);
        requestDto = createCategoryRequestDto(categoryDto);
    }

    @Test
    void save_ValidCategory_ReturnCategoryDto() {
        when(categoryMapper.toModel(any(CategoryDto.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);

        CategoryDto savedCategoryDto = categoryService.save(categoryDto);

        assertNotNull(savedCategoryDto);
        assertEquals(categoryDto.getName(), savedCategoryDto.getName());
        verify(categoryMapper, times(1)).toModel(any(CategoryDto.class));
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toDto(any(Category.class));
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    void findAll_NotEmptyList_ReturnListOfCategoryDto() {
        List<Category> categories = Collections.singletonList(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);

        List<CategoryDto> categoryDtos = categoryService.findAll();

        assertNotNull(categoryDtos);
        assertEquals(1L, categoryDtos.size());
        assertEquals(categoryDto.getName(), categoryDtos.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toDto(any(Category.class));
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void findAll_EmptyList_ReturnEmptyList() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<CategoryDto> categoryDtos = categoryService.findAll();

        assertNotNull(categoryDtos);
        assertEquals(0L, categoryDtos.size());
        verify(categoryRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void getById_ValidId_ReturnCategoryDto() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);

        CategoryDto foundCategoryDto = categoryService.getById(1L);
        Long categoryId = 1L;

        assertNotNull(foundCategoryDto);
        assertEquals(categoryId, foundCategoryDto.getId());
        assertEquals(categoryDto.getName(), foundCategoryDto.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void getById_InvalidId_ThrowsEntityNotFoundException() {
        Long categoryId = -1L;

        when(categoryRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Category not found by id: " + categoryId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));

        assertEquals("Category not found by id: " + categoryId, exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void update_ValidCategoryId_ReturnUpdatedCategoryDto() {
        Long categoryId = 1L;

        CreateCategoryRequestDto updatedRequestDto = new CreateCategoryRequestDto();
        updatedRequestDto.setName("UpdatedCategoryName");
        updatedRequestDto.setDescription("UpdatedCategoryDescription");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateModel(category, updatedRequestDto);

        category.setName(updatedRequestDto.getName());
        category.setDescription(updatedRequestDto.getDescription());

        CategoryDto updatedCategoryDto = new CategoryDto();
        updatedCategoryDto.setId(category.getId());
        updatedCategoryDto.setName(category.getName());
        updatedCategoryDto.setDescription(category.getDescription());

        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(updatedCategoryDto);

        CategoryDto actual = categoryService.update(categoryId, updatedRequestDto);

        assertNotNull(actual);
        assertEquals(categoryId, actual.getId());
        assertEquals("UpdatedCategoryName", actual.getName());
        assertEquals("UpdatedCategoryDescription", actual.getDescription());

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).updateModel(category, updatedRequestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void update_InvalidCategoryId_ThrowsEntityNotFoundException() {
        Long categoryId = -1L;
        when(categoryRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Category not found by id: " + categoryId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryId, requestDto));

        assertEquals("Category not found by id: " + categoryId, exception.getMessage());

        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void deleteById_ValidCategoryId_Success() {
        Long categoryId = 1L;

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        assertDoesNotThrow(() -> categoryService.deleteById(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(category);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void deleteById_InvalidCategoryId_ThrowEntityNotFoundException() {
        Long categoryId = -1L;

        when(categoryRepository.findById(any())).thenThrow(new EntityNotFoundException(
                "Category not found by id: " + categoryId));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(categoryId));

        assertEquals("Category not found by id: " + categoryId, exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    private Category createCategory(Long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private CategoryDto createCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        return categoryDto;
    }

    private CreateCategoryRequestDto createCategoryRequestDto(CategoryDto categoryDto) {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(categoryDto.getName());
        requestDto.setDescription(categoryDto.getDescription());
        return requestDto;
    }
}
