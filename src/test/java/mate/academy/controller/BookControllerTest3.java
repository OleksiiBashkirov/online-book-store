//package mate.academy.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import mate.academy.dto.book.BookDto;
//import mate.academy.dto.book.CreateBookRequestDto;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
//
//import javax.sql.DataSource;
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
//import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Sql(scripts = "classpath:database/books/setup_books_with_categories.sql", executionPhase = BEFORE_TEST_METHOD)
//@Sql(scripts = "classpath:database/books/cleanup_books_with_categories.sql", executionPhase = AFTER_TEST_METHOD)
//class BookControllerTest {
//    protected static MockMvc mockMvc;
//    private static final String SCRIPT_TO_INSERT_DATA_TO_DB =
//            "classpath:database/books/setup_books_with_categories.sql";
//    private static final String SCRIPT_TO_REMOVE_DATA_FROM_DB =
//            "classpath:database/books/cleanup_books_with_categories.sql";
//    private static final String URL = "/api/books";
//    private static final Long ID = 1L;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeAll
//    static void beforeAll(
//            @Autowired WebApplicationContext applicationContext
//    ) {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(applicationContext)
//                .apply(springSecurity())
//                .build();
//    }
//
//    @AfterAll
//    static void afterAll(@Autowired DataSource dataSource) {
//        teardown(dataSource);
//    }
//
//    @SneakyThrows
//    static void teardown(DataSource dataSource) {
//        try (Connection connection = dataSource.getConnection()) {
//            connection.setAutoCommit(true);
//            executeSqlScript(
//                    connection,
//                    new ClassPathResource("database/books/cleanup_books_with_categories.sql")
//            );
//        }
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Create a new book")
//    @Sql(scripts = SCRIPT_TO_REMOVE_DATA_FROM_DB, executionPhase = AFTER_TEST_METHOD)
//    void createBook_ValidRequestDto_Success() throws Exception {
//        CreateBookRequestDto requestDto = createBookRequestDto();
//        BookDto expected = createBookDto();
//
//        MvcResult result = mockMvc.perform(post(URL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        BookDto actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), BookDto.class
//        );
//
//        assertNotNull(actual);
//        EqualsBuilder.reflectionEquals(expected, actual, "id");
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Update a book by id")
//    @Sql(scripts = SCRIPT_TO_INSERT_DATA_TO_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_TO_REMOVE_DATA_FROM_DB, executionPhase = AFTER_TEST_METHOD)
//    void updateBook_ValidId_Success() throws Exception {
//        Long bookId = ID;
//
//        CreateBookRequestDto requestDto = createBookRequestDto();
//        requestDto.setTitle("Updated TitleBook1");
//        BookDto expected = createBookDto();
//
//        MvcResult result = mockMvc.perform(put(URL + "/{id}", bookId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        BookDto actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), BookDto.class
//        );
//
//        assertNotNull(actual);
//        EqualsBuilder.reflectionEquals(expected, actual, "id");
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Delete a book by id")
//    void deleteBook_ValidId_Success() throws Exception {
//        mockMvc.perform(delete(URL + "/1").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent())
//                .andReturn();
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Find a book by ID")
//    @Sql(scripts = SCRIPT_TO_INSERT_DATA_TO_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_TO_REMOVE_DATA_FROM_DB, executionPhase = AFTER_TEST_METHOD)
//    void getBookById_ValidId_Success() throws Exception {
//        MvcResult result = mockMvc.perform(get(URL + "/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        BookDto expected = createBookDto();
//        BookDto actual = objectMapper.readValue(result.getResponse()
//                .getContentAsString(), BookDto.class
//        );
//
//        assertNotNull(actual);
//        EqualsBuilder.reflectionEquals(expected, actual, "id");
//    }
//
//    @WithMockUser(username = "user", roles = {"USER"})
//    @Test
//    @DisplayName("Find all books")
//    @Sql(scripts = SCRIPT_TO_INSERT_DATA_TO_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_TO_REMOVE_DATA_FROM_DB, executionPhase = AFTER_TEST_METHOD)
//    void getAll_NotEmpty_ReturnFourBooks() throws Exception {
//        MvcResult result = mockMvc.perform(get(URL)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        List<BookDto> actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), List.class);
//
//        assertNotNull(actual);
//        assertEquals(4, actual.size());
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Search books by params")
//    @Sql(scripts = SCRIPT_TO_INSERT_DATA_TO_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_TO_REMOVE_DATA_FROM_DB, executionPhase = AFTER_TEST_METHOD)
//    void searchBooks_ValidSearchParams_ReturnMatchingBooks() throws Exception {
//        MvcResult result = mockMvc.perform(get(URL + "/search")
//                        .param("title", "Book1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        List<BookDto> actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), List.class);
//
//        assertNotNull(actual);
//        assertEquals(1, actual.size());
//        assertEquals("Book1", actual.get(0).getTitle());
//    }
//
//    private CreateBookRequestDto createBookRequestDto() {
//        var requestDto = new CreateBookRequestDto();
//        requestDto.setTitle("Book1");
//        requestDto.setAuthor("Author1");
//        requestDto.setIsbn("111222333444555");
//        requestDto.setPrice(BigDecimal.valueOf(20));
//        requestDto.setDescription("description Book1");
//        requestDto.setCoverImage("cover_image1.jpg");
//        requestDto.setCategories(Set.of(1L, 2L)); // Ensure categories are set
//        return requestDto;
//    }
//
//    private BookDto createBookDto() {
//        var bookDto = new BookDto();
//        bookDto.setId(ID);
//        bookDto.setTitle("Book1");
//        bookDto.setAuthor("Author1");
//        bookDto.setIsbn("111222333444555");
//        bookDto.setPrice(BigDecimal.valueOf(20));
//        bookDto.setDescription("description Book1");
//        bookDto.setCoverImage("cover_image1.jpg");
//        return bookDto;
//    }
//}
