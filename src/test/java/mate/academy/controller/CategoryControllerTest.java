//package mate.academy.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.security.test.web.servlet
// .setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.sql.Connection;
//import java.util.List;
//import javax.sql.DataSource;
//import lombok.SneakyThrows;
//import mate.academy.dto.category.CategoryDto;
//import mate.academy.dto.category.CreateCategoryRequestDto;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.jdbc.datasource.init.ScriptUtils;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
//
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//class CategoryControllerTest {
//    private static final Long ID = 1L;
//    private static final String URL = "/api/categories";
//    private static final String SCRIPT_FOR_ADD_CATEGORY_IN_DB =
//            "classpath:database/books/setup_books_with_categories.sql";
//    private static final String SCRIPT_FOR_REMOVE_DATA_IN_DB =
//            "classpath:database/books/cleanup_books_with_categories.sql";
//
//    protected static MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeAll
//    static void beforeAll(@Autowired WebApplicationContext webApplicationContext,
//                          @Autowired DataSource dataSource
//    ) {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .apply(springSecurity())
//                .build();
//    }
//
//    @AfterAll
//    static void afterAll(@Autowired DataSource dataSource) {
//        tearDown(dataSource);
//    }
//
//    @SneakyThrows
//    static void tearDown(DataSource dataSource) {
//        try (Connection connection = dataSource.getConnection()) {
//            connection.setAutoCommit(true);
//            ScriptUtils.executeSqlScript(
//                    connection,
//                    new ClassPathResource("database/books/cleanup_books_with_categories.sql")
//            );
//        }
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Create a new category")
//    @Sql(scripts = SCRIPT_FOR_REMOVE_DATA_IN_DB, executionPhase = AFTER_TEST_METHOD)
//    void createCategory_ValidRequestDto_Success() throws Exception {
//        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
//        CategoryDto expected = new CategoryDto()
//                .setName(requestDto.getName())
//                .setDescription(requestDto.getDescription());
//
//        MvcResult result = mockMvc.perform(post(URL)
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        CategoryDto actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), CategoryDto.class
//        );
//
//        assertNotNull(actual);
//        EqualsBuilder.reflectionEquals(expected, actual, "id");
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Update a category by id")
//    @Sql(scripts = SCRIPT_FOR_ADD_CATEGORY_IN_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_FOR_REMOVE_DATA_IN_DB, executionPhase = AFTER_TEST_METHOD)
//    void updateCategory_ValidId_Success() throws Exception {
//        Long categoryId = ID;
//        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
//        requestDto.setName("Updated Category");
//
//        CategoryDto expected = createCategoryDto();
//        expected.setName(requestDto.getName());
//
//        String jsonRequest = objectMapper.writeValueAsString(requestDto);
//        MvcResult result = mockMvc.perform(put(URL + "/{id}", categoryId)
//                        .contentType(APPLICATION_JSON)
//                        .content(jsonRequest))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        CategoryDto actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), CategoryDto.class
//        );
//
//        assertNotNull(actual);
//        EqualsBuilder.reflectionEquals(expected, actual, "id");
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Delete a category by id")
//    @Sql(scripts = SCRIPT_FOR_ADD_CATEGORY_IN_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_FOR_REMOVE_DATA_IN_DB, executionPhase = AFTER_TEST_METHOD)
//    void deleteCategory_ValidId_Success() throws Exception {
//        mockMvc.perform(delete(URL + "/1").contentType(APPLICATION_JSON))
//                .andExpect(status().isNoContent())
//                .andReturn();
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Find a category by ID")
//    @Sql(scripts = SCRIPT_FOR_ADD_CATEGORY_IN_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_FOR_REMOVE_DATA_IN_DB, executionPhase = AFTER_TEST_METHOD)
//    void getCategoryById_ValidId_Success() throws Exception {
//        MvcResult result = mockMvc.perform(get(URL + "/1")
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        CategoryDto expected = createCategoryDto();
//        CategoryDto actual = objectMapper.readValue(result.getResponse()
//                .getContentAsString(), CategoryDto.class
//        );
//
//        assertNotNull(actual);
//        EqualsBuilder.reflectionEquals(expected, actual, "id");
//    }
//
//    @WithMockUser(username = "user", roles = {"USER"})
//    @Test
//    @DisplayName("Find all categories")
//    @Sql(scripts = SCRIPT_FOR_ADD_CATEGORY_IN_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_FOR_REMOVE_DATA_IN_DB, executionPhase = AFTER_TEST_METHOD)
//    void getAll_NotEmpty_ReturnCategories() throws Exception {
//        MvcResult result = mockMvc.perform(get(URL)
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        List<CategoryDto> actual = objectMapper.readValue(
//                result.getResponse().getContentAsString(), List.class);
//
//        assertNotNull(actual);
//        assertEquals(3, actual.size());
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Find books by category ID")
//    @Sql(scripts = SCRIPT_FOR_ADD_CATEGORY_IN_DB, executionPhase = BEFORE_TEST_METHOD)
//    @Sql(scripts = SCRIPT_FOR_REMOVE_DATA_IN_DB, executionPhase = AFTER_TEST_METHOD)
//    void getBooksByCategoryId_ValidId_ReturnBooks() throws Exception {
//        MvcResult result = mockMvc.perform(get(URL + "/1/books")
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        List<?> actual = objectMapper.readValue(result.getResponse()
//                .getContentAsString(), List.class);
//
//        assertNotNull(actual);
//        assertEquals(2, actual.size());
//    }
//
//    private CreateCategoryRequestDto createCategoryRequestDto() {
//        return new CreateCategoryRequestDto()
//                .setName("New Category")
//                .setDescription("Description for the new category");
//    }
//
//    private CategoryDto createCategoryDto() {
//        return new CategoryDto()
//                .setId(ID)
//                .setName("Category1")
//                .setDescription("Description of Category1");
//    }
//}
