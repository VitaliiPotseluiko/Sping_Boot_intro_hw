package com.springboot.intro.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.intro.config.ContextInitializer;
import com.springboot.intro.dto.ErrorValidationDto;
import com.springboot.intro.dto.StatusErrorDto;
import com.springboot.intro.dto.request.CategoryRequestDto;
import com.springboot.intro.dto.response.CategoryResponseDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = ContextInitializer.class)
class CategoryControllerTest {
    private static final String CATEGORIES_PATH = "/api/categories";
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
                    new ClassPathResource("database/categories/create-three-categories.sql"));
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
                    new ClassPathResource("database/categories/delete-all-categories.sql"));
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/categories/delete-fiction-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            create a new category
            """)
    public void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Fiction");
        categoryRequestDto.setDescription("Fiction books");
        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setName(categoryRequestDto.getName());
        expected.setDescription(categoryRequestDto.getDescription());
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(post(CATEGORIES_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual,"id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            create a new category with invalid request body
            """)
    public void createCategory_InvalidRequestDto_BadRequest() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("");
        categoryRequestDto.setDescription("Fiction books");
        String expectedError = "name can't be blank";
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(post(CATEGORIES_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorValidationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ErrorValidationDto.class);

        assertEquals(expectedError, actual.getErrors()[0]);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatus());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("""
            get three categories
            """)
    public void getAll_GetThreeCategories_Success() throws Exception {
        List<CategoryResponseDto> expected = new ArrayList<>();
        expected.add(new CategoryResponseDto(1L, "Fiction", "d1"));
        expected.add(new CategoryResponseDto(2L, "Action", "d2"));
        expected.add(new CategoryResponseDto(3L, "Thriller", "d3"));

        MvcResult result = mockMvc.perform(get(CATEGORIES_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponseDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryResponseDto[].class);

        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            update category by id = 2
            """)
    public void updateCategory_ValidRequestDto_Success() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Poems");
        categoryRequestDto.setDescription("about poems");
        CategoryResponseDto expected = new CategoryResponseDto(
                null,
                categoryRequestDto.getName(),
                categoryRequestDto.getDescription()
        );
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(put(CATEGORIES_PATH.concat("/2"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryResponseDto.class);

        assertEquals(2, actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            update category by id = 1 with invalid request body
            """)
    public void updateCategory_InvalidRequestDto_BadRequest() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setDescription("about poems");
        String expectedError = "name can't be blank";
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(put(CATEGORIES_PATH.concat("/2"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorValidationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ErrorValidationDto.class);

        assertEquals(expectedError, actual.getErrors()[0]);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatus());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            update category by non existent id
            """)
    public void updateCategory_NonExistentId_NotFound() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Poems");
        categoryRequestDto.setDescription("about poems");
        String expectedError = "There is no such category by id 4";
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(put(CATEGORIES_PATH.concat("/4"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expectedError, actual.getError());
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatus());
    }
}