package com.toyproject.catalog.web;

import com.toyproject.catalog.application.ProductService;
import com.toyproject.catalog.web.dto.CreateProductRequest;
import com.toyproject.catalog.web.dto.ProductResponse;
import com.toyproject.catalog.web.dto.UpdateProductRequest;
import com.toyproject.common.web.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("상품 생성 요청이 유효하면 201 응답과 생성된 상품 정보를 반환한다")
    void createProduct_returnsCreatedResponse() throws Exception {
        given(productService.createProduct(any(CreateProductRequest.class)))
            .willReturn(new ProductResponse(1L, "Keyboard", BigDecimal.valueOf(10000)));

        mockMvc.perform(
                post("/api/v1/products")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Keyboard",
                          "price": 10000
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("product created"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Keyboard"))
            .andExpect(jsonPath("$.data.price").value(10000));
    }

    @Test
    @DisplayName("상품 생성 요청의 가격이 0 이하이면 400 응답을 반환한다")
    void createProduct_withNonPositivePrice_returnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/api/v1/products")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Keyboard",
                          "price": 0
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("price: price must be greater than zero"))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(productService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("상품 생성 요청의 가격이 없으면 400 응답을 반환한다")
    void createProduct_withoutPrice_returnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/api/v1/products")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Keyboard"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("price: price must not be null"))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(productService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("상품 단건 조회 요청이 유효하면 상품 정보를 반환한다")
    void getProduct_returnsProductResponse() throws Exception {
        given(productService.getProduct(1L))
            .willReturn(new ProductResponse(1L, "Keyboard", BigDecimal.valueOf(10000)));

        mockMvc.perform(get("/api/v1/products/{productId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Keyboard"))
            .andExpect(jsonPath("$.data.price").value(10000));
    }

    @Test
    @DisplayName("상품 수정 요청이 유효하면 수정된 상품 정보를 반환한다")
    void updateProduct_returnsUpdatedResponse() throws Exception {
        given(productService.updateProduct(any(Long.class), any(UpdateProductRequest.class)))
            .willReturn(new ProductResponse(1L, "Keyboard Pro", BigDecimal.valueOf(15000)));

        mockMvc.perform(
                put("/api/v1/products/{productId}", 1L)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Keyboard Pro",
                          "price": 15000
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("product updated"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Keyboard Pro"))
            .andExpect(jsonPath("$.data.price").value(15000));
    }

    @Test
    @DisplayName("상품 수정 요청의 가격이 없으면 400 응답을 반환한다")
    void updateProduct_withoutPrice_returnsBadRequest() throws Exception {
        mockMvc.perform(
                put("/api/v1/products/{productId}", 1L)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Keyboard Pro"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("price: price must not be null"))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(productService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("상품 삭제 요청이 유효하면 삭제 완료 응답을 반환한다")
    void deleteProduct_returnsDeletedResponse() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{productId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("product deleted"));

        then(productService).should().deleteProduct(1L);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(ProductController.class)
    static class TestApplication {
    }
}
