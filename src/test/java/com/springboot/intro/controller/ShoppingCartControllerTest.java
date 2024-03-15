package com.springboot.intro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.intro.dto.ErrorValidationDto;
import com.springboot.intro.dto.StatusErrorDto;
import com.springboot.intro.dto.request.AddCartItemRequestDto;
import com.springboot.intro.dto.request.UpdateCartItemRequestDto;
import com.springboot.intro.dto.response.CartItemResponseDto;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    private static final String SHOPPING_CART_PATH = "/api/cart";
    private static final String USER_NAME = "useremail";
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shoppingcart/add-data-to-database.sql"));
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
                    new ClassPathResource("database/shoppingcart/delete-all-from-database.sql"));
        }
    }

    @Test
    @WithUserDetails(USER_NAME)
    @DisplayName("""
            get shopping cart by user with id = 1
            """)
    public void getShoppingCart_getShoppingCartByUser_Ok() throws Exception {
        CartItemResponseDto firstCartItem = CartItemResponseDto.builder()
                .id(1L)
                .bookId(1L)
                .bookTitle("Title1")
                .quantity(10)
                .build();
        CartItemResponseDto secondCartItem = CartItemResponseDto.builder()
                .id(2L)
                .bookId(2L)
                .bookTitle("Title2")
                .quantity(20)
                .build();
        CartItemResponseDto thirdCartItem = CartItemResponseDto.builder()
                .id(3L)
                .bookId(3L)
                .bookTitle("Title3")
                .quantity(30)
                .build();
        ShoppingCartResponseDto expected = new ShoppingCartResponseDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItemSet(
                Set.of(firstCartItem, secondCartItem, thirdCartItem));
        MvcResult result = mockMvc.perform(get(SHOPPING_CART_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithUserDetails(USER_NAME)
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/delete-cart-item-from-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            add book with id = 2 to shopping cart
            """)
    public void addBookToShoppingCart_addBookWithExistingId_Success() throws Exception {
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto();
        requestDto.setBookId(2L);
        requestDto.setQuantity(7);
        CartItemResponseDto expected = CartItemResponseDto.builder()
                .id(4L)
                .bookTitle("Title2")
                .bookId(2L)
                .quantity(7)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(SHOPPING_CART_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class);

        assertEquals(4, actual.getCartItemSet().size());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual.getCartItemSet().stream()
                .filter(cI -> cI.getQuantity() == 7)
                .findFirst()
                .get()));
    }
    @WithUserDetails(USER_NAME)
    @Test
    @DisplayName("""
            add invalid request dto
            """)
    public void addBookToShoppingCart_addInvalidRequestDto_BadRequest() throws Exception {
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto();
        requestDto.setQuantity(0);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        List<String> expected = List.of("bookId must not be null",
                "quantity must be greater than or equal to 1");

        MvcResult result = mockMvc.perform(post(SHOPPING_CART_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorValidationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ErrorValidationDto.class);
        assertEquals(2, actual.getErrors().length);
        assertEquals(expected, Arrays.stream(actual.getErrors()).sorted().toList());
    }

    @WithUserDetails(USER_NAME)
    @Test
    @DisplayName("""
            add not existing book to shopping cart
            """)
    public void addBookToShoppingCart_addNotExistingBook_NotFound() throws Exception {
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto();
        requestDto.setBookId(10L);
        requestDto.setQuantity(7);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String expected = "Can't find book by id 10";

        MvcResult result = mockMvc.perform(post(SHOPPING_CART_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expected, actual.getError());
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatus());
    }

    @WithUserDetails(USER_NAME)
    @Test
    @DisplayName("""
            update quantity of book by id = 1
            """)
    public void updateBookQuantity_ValidRequestDto_Success() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(1);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(SHOPPING_CART_PATH.concat("/cart-items/1"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class);

        assertEquals(1, actual.getCartItemSet().stream()
                .filter(cI -> cI.getId() == 1)
                .findFirst()
                .get().getQuantity());
    }

    @WithUserDetails(USER_NAME)
    @Test
    @DisplayName("""
            invalid request dto
            """)
    public void updateBookQuantity_InvalidRequestDto_BadRequest() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(0);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String expected = "quantity must be greater than or equal to 1";

        MvcResult result = mockMvc.perform(put(SHOPPING_CART_PATH.concat("/cart-items/1"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorValidationDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ErrorValidationDto.class);

        assertEquals(expected, actual.getErrors()[0]);
    }

    @WithUserDetails(USER_NAME)
    @Test
    @DisplayName("""
            not existing cart item 
            """)
    public void updateBookQuantity_NotExistingCartItem_NotFound() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(1);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String expected = "Can't find cart item by id 10";

        MvcResult result = mockMvc.perform(put(SHOPPING_CART_PATH.concat("/cart-items/10"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expected, actual.getError());
    }

    @WithUserDetails(USER_NAME)
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-cart-item-to-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            delete cart item by id = 1
            """)
    public void deleteBookFromShoppingCart_ExistingCartItem_Success() throws Exception {
        MvcResult result = mockMvc.perform(delete(SHOPPING_CART_PATH.concat("/cart-items/1"))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartResponseDto.class);

        assertEquals(2, actual.getCartItemSet().size());
    }

    @WithUserDetails(USER_NAME)
    @Test
    @DisplayName("""
            delete cart item by id = 5 
            """)
    public void deleteBookFromShoppingCart_NotExistingCartItem_NotFound() throws Exception {
        String expected = "Can't find cart item by id 5";

        MvcResult result = mockMvc.perform(delete(SHOPPING_CART_PATH.concat("/cart-items/5"))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        StatusErrorDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                StatusErrorDto.class);

        assertEquals(expected, actual.getError());
    }
}