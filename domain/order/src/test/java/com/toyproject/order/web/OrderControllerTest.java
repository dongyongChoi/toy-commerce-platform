package com.toyproject.order.web;

import com.toyproject.common.web.GlobalExceptionHandler;
import com.toyproject.order.application.OrderService;
import com.toyproject.order.web.dto.CreateOrderRequest;
import com.toyproject.order.web.dto.OrderResponse;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("주문 목록 조회 요청이 유효하면 주문 정보를 반환한다")
    void getOrders_returnsOrderResponses() throws Exception {
        given(orderService.getOrders()).willReturn(List.of(
            new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CREATED")
        ));

        mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].memberId").value(1))
            .andExpect(jsonPath("$.data[0].productId").value(10))
            .andExpect(jsonPath("$.data[0].quantity").value(2))
            .andExpect(jsonPath("$.data[0].totalPrice").value(20000))
            .andExpect(jsonPath("$.data[0].status").value("CREATED"));
    }

    @Test
    @DisplayName("주문 생성 요청이 유효하면 201 응답과 생성된 주문 정보를 반환한다")
    void createOrder_returnsCreatedResponse() throws Exception {
        given(orderService.createOrder(any(CreateOrderRequest.class)))
            .willReturn(new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CREATED"));

        mockMvc.perform(
                post("/api/v1/orders")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "memberId": 1,
                          "productId": 10,
                          "quantity": 2,
                          "totalPrice": 20000
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("order created"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.memberId").value(1))
            .andExpect(jsonPath("$.data.productId").value(10))
            .andExpect(jsonPath("$.data.quantity").value(2))
            .andExpect(jsonPath("$.data.totalPrice").value(20000))
            .andExpect(jsonPath("$.data.status").value("CREATED"));
    }

    @Test
    @DisplayName("주문 생성 요청의 수량이 0 이하이면 400 응답을 반환한다")
    void createOrder_withNonPositiveQuantity_returnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/api/v1/orders")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "memberId": 1,
                          "productId": 10,
                          "quantity": 0,
                          "totalPrice": 20000
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("quantity: quantity must be greater than zero"))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(orderService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("주문 생성 요청의 총 가격이 0 이하이면 400 응답을 반환한다")
    void createOrder_withNonPositiveTotalPrice_returnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/api/v1/orders")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "memberId": 1,
                          "productId": 10,
                          "quantity": 2,
                          "totalPrice": 0
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("totalPrice: totalPrice must be greater than zero"))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(orderService).shouldHaveNoInteractions();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(OrderController.class)
    static class TestApplication {
    }
}
