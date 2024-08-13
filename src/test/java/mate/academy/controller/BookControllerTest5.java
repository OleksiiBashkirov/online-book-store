//package mate.academy.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import mate.academy.config.CustomMySqlContainer;
//import mate.academy.dto.book.BookDto;
//import mate.academy.dto.book.CreateBookRequestDto;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import javax.sql.DataSource;
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@Sql(scripts = "classpath:database/books/setup_books_with_categories.sql", executionPhase = BEFORE_TEST_METHOD)
//@Sql(scripts = "classpath:database/books/cleanup_books_with_categories.sql", executionPhase = AFTER_TEST_METHOD)
//class BookControllerTest {
//
//    protected static MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeAll
//    static void setUp(
//            @Autowired WebApplicationContext webApplicationContext,
//            @Autowired DataSource dataSource) throws SQLException {
//        CustomMySqlContainer.getInstance().start();
//
//        // Initialize MockMvc
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .apply(SecurityMockMvcConfigurers.springSecurity())
//                .build();
//
//        setupDatabase(dataSource);
//    }
//
//    @AfterAll
//    static void tearDown(@Autowired DataSource dataSource) {
//        CustomMySqlContainer.getInstance().stop();
//        cleanupDatabase(dataSource);
//    }
//
//    @SneakyThrows
//    static void setupDatabase(DataSource dataSource) {
//        try (Connection connection = dataSource.getConnection()) {
//            connection.setAutoCommit(true);
//            executeSqlScript(
//                    connection,
//                    new ClassPathResource("database/books/setup_books_with_categories.sql")
//            );
//        }
//    }
//
//    @SneakyThrows
//    static void cleanupDatabase(DataSource dataSource) {
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
//    @DisplayName("Create a new book successfully")
//    void createBook_ValidRequestDto_Success() throws Exception {
//        // Given
//        CreateBookRequestDto requestDto = createBookRequestDto();
////        requestDto.setIsbn("1234567890123"); // Use a unique ISBN
//        BookDto expected = createBookDto();
////        expected.setIsbn("1234567890123");
//
//        // When
//        MvcResult result = mockMvc.perform(post("/api/books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        // Then
//        BookDto actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), BookDto.class
//        );
//
//        assertNotNull(actual);
//        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Update a book by ID successfully")
//    void updateBook_ValidId_Success() throws Exception {
//        // Given
//        Long bookId = 1L;
//        CreateBookRequestDto requestDto = createBookRequestDto();
//        requestDto.setTitle("Updated TitleBook1");
//        requestDto.setIsbn("0987654321098"); // Ensure a unique ISBN
//        BookDto expected = createBookDto();
//        expected.setTitle("Updated TitleBook1");
//        expected.setIsbn("0987654321098");
//
//        // When
//        MvcResult result = mockMvc.perform(put("/api/books/{id}", bookId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Then
//        BookDto actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), BookDto.class
//        );
//
//        assertNotNull(actual);
//        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Delete a book by ID successfully")
//    void deleteBook_ValidId_Success() throws Exception {
//        // When & Then
//        mockMvc.perform(delete("/api/books/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Find a book by ID successfully")
//    void getBookById_ValidId_Success() throws Exception {
//        // When
//        MvcResult result = mockMvc.perform(get("/api/books/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Then
//        BookDto expected = createBookDto();
//        BookDto actual = objectMapper.readValue(result.getResponse()
//                .getContentAsString(), BookDto.class
//        );
//
//        assertNotNull(actual);
//        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Find all books successfully")
//    void getAll_NotEmpty_ReturnFourBooks() throws Exception {
//        // When
//        MvcResult result = mockMvc.perform(get("/api/books")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Then
//        List<BookDto> actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), List.class);
//
//        assertNotNull(actual);
//        assertThat(actual).hasSize(4);
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Search books by parameters successfully")
//    void searchBooks_ValidSearchParams_ReturnMatchingBooks() throws Exception {
//        // When
//        MvcResult result = mockMvc.perform(get("/api/books/search")
//                        .param("title", "Book1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Then
//        List<BookDto> actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), List.class);
//
//        assertNotNull(actual);
//        assertThat(actual).hasSize(1);
//        assertThat(actual.get(0).getTitle()).isEqualTo("Book1");
//    }
//
//    private CreateBookRequestDto createBookRequestDto() {
//        CreateBookRequestDto requestDto = new CreateBookRequestDto();
//        requestDto.setTitle("Book1");
//        requestDto.setAuthor("Author1");
//        requestDto.setIsbn("1112223334445"); // Ensure unique ISBN for tests
//        requestDto.setPrice(BigDecimal.valueOf(20));
//        requestDto.setDescription("description Book1");
//        requestDto.setCoverImage("cover_image1.jpg");
//        return requestDto;
//    }
//
//    private BookDto createBookDto() {
//        BookDto bookDto = new BookDto();
//        bookDto.setId(1L);
//        bookDto.setTitle("Book1");
//        bookDto.setAuthor("Author1");
//        bookDto.setIsbn("1112223334445"); // Ensure consistent ISBN for comparison
//        bookDto.setPrice(BigDecimal.valueOf(20));
//        bookDto.setDescription("description Book1");
//        bookDto.setCoverImage("cover_image1.jpg");
//        return bookDto;
//    }
//}
