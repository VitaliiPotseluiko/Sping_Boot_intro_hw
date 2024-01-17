package com.springboot.intro.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.intro.dto.ErrorValidationDto;
import com.springboot.intro.dto.StatusErrorDto;
import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookResponseDto;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    private static final String BOOKS_PATH = "/api/books";
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-data-to-database.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/delete-all-from-database.sql"));
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/delete-sixth-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            save valid book
            """)
    public void save_ValidRequestDto_Success() throws Exception {
        BookRequestDto bookRequestDto = new BookRequestDto()
                .setTitle("Title6")
                .setAuthor("Author6")
                .setIsbn("2400000032632")
                .setPrice(BigDecimal.valueOf(60))
                .setCoverImage("cow6")
                .setDescription("d6")
                .setCategoryId(2L);
        BookResponseDto expected = new BookResponseDto()
                .setTitle(bookRequestDto.getTitle())
                .setAuthor(bookRequestDto.getAuthor())
                .setIsbn(bookRequestDto.getIsbn())
                .setPrice(bookRequestDto.getPrice())
                .setCoverImage(bookRequestDto.getCoverImage())
                .setDescription(bookRequestDto.getDescription())
                .setCategoryIds(Set.of(2L));
        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(post(BOOKS_PATH)
                        .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookResponseDto.class);

        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual,"id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            save invalid book
            """)
    public void save_InvalidRequestDto_BadRequest() throws Exception {
        BookRequestDto invalidRequest = new BookRequestDto();
        invalidRequest.setTitle("Title6")
                .setAuthor(null)
                .setIsbn("2400000032632")
                .setPrice(BigDecimal.valueOf(-1))
                .setCoverImage("cow6")
                .setDescription("d6")
                .setCategoryId(2L);
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);
        List<String> expectedErrors = List.of("author can't be blank", "price must be bigger than 0");

        MvcResult result = mockMvc.perform(post(BOOKS_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorValidationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ErrorValidationDto.class);

        assertEquals(2, actual.getErrors().length);
        assertEquals(expectedErrors, Arrays.stream(actual.getErrors()).sorted().toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            update book with valid requestDto
            """)
    public void update_ValidRequestDto_Success() throws Exception {
        BookRequestDto bookRequestDto = new BookRequestDto()
                .setTitle("Title6")
                .setAuthor("Author6")
                .setIsbn("978-5-699-54574-2")
                .setPrice(BigDecimal.valueOf(60))
                .setCoverImage("cow6")
                .setDescription("d6")
                .setCategoryId(2L);
        BookResponseDto expected = new BookResponseDto()
                .setId(4L)
                .setTitle(bookRequestDto.getTitle())
                .setAuthor(bookRequestDto.getAuthor())
                .setIsbn(bookRequestDto.getIsbn())
                .setPrice(bookRequestDto.getPrice())
                .setCoverImage(bookRequestDto.getCoverImage())
                .setDescription(bookRequestDto.getDescription())
                .setCategoryIds(Set.of(2L));
        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(put(BOOKS_PATH.concat("/4"))
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookResponseDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            update book with valid requestDto
            """)
    public void update_InvalidRequestDto_BadRequest() throws Exception {
        BookRequestDto expected = new BookRequestDto();
        expected.setTitle("")
                .setAuthor("")
                .setIsbn("2400000032632")
                .setPrice(BigDecimal.valueOf(60))
                .setCoverImage("cow6")
                .setDescription("d6")
                .setCategoryId(2L);
        List<String> expectedErrors = List.of( "author can't be blank", "title can't be blank");
        String jsonRequest = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(put(BOOKS_PATH.concat("/4"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorValidationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ErrorValidationDto.class);

        assertEquals(2, actual.getErrors().length);
        assertEquals(expectedErrors, Arrays.stream(actual.getErrors()).sorted().toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            update book by non existent id
            """)
    public void update_NonExistentId_NotFound() throws Exception {
        BookRequestDto expected = new BookRequestDto();
        expected.setTitle("Title6")
                .setAuthor("Author6")
                .setIsbn("2400000032632")
                .setPrice(BigDecimal.valueOf(60))
                .setCoverImage("cow6")
                .setDescription("d6")
                .setCategoryId(2L);
        String jsonRequest = objectMapper.writeValueAsString(expected);
        String expectedError = "Can't update book by id 7. There is no such entity";

        MvcResult result = mockMvc.perform(put(BOOKS_PATH.concat("/7"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expectedError, actual.getError());
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatus());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
            get three books
            """)
    public void getAll_GetThreeBooks_Success() throws Exception {
        List<BookResponseDto> expected = new ArrayList<>();
        expected.add(new BookResponseDto().setId(1L)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("978-1-2345-6789-7")
                .setPrice(BigDecimal.valueOf(11))
                .setDescription("d1")
                .setCoverImage("cov1")
                .setCategoryIds(Set.of(1L)));
        expected.add(new BookResponseDto().setId(2L)
                .setTitle("Title2")
                .setAuthor("Author2")
                .setIsbn("978-2-266-11156-0")
                .setPrice(BigDecimal.valueOf(21))
                .setDescription("d1")
                .setCoverImage("cov2")
                .setCategoryIds(Set.of(1L)));
        expected.add(new BookResponseDto().setId(3L)
                .setTitle("Title3")
                .setAuthor("Author3")
                .setIsbn("979-0-2600-0043-8")
                .setPrice(BigDecimal.valueOf(31))
                .setDescription("d2")
                .setCoverImage("cov3")
                .setCategoryIds(Set.of(1L)));
        expected.add(new BookResponseDto().setId(4L)
                .setTitle("Title4")
                .setAuthor("Author4")
                .setIsbn("978-5-699-54574-2")
                .setPrice(BigDecimal.valueOf(41))
                .setDescription("d4")
                .setCoverImage("cov4")
                .setCategoryIds(Set.of(2L)));
        expected.add(new BookResponseDto().setId(5L)
                .setTitle("Title5")
                .setAuthor("Author5")
                .setIsbn("978-966-2046-92-2")
                .setPrice(BigDecimal.valueOf(51))
                .setDescription("d5")
                .setCoverImage("cov5")
                .setCategoryIds(Set.of(2L)));

        MvcResult result = mockMvc.perform(get(BOOKS_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookResponseDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookResponseDto[].class);

        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }
}