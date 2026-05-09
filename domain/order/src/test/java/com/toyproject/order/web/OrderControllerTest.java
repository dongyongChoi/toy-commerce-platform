package com.toyproject.order.web;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("주문 단건 조회 요청이 유효하면 주문 정보를 반환한다")
    void getOrder_returnsOrderResponse() throws Exception {
        given(orderService.getOrder(1L))
            .willReturn(new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CREATED"));

        mockMvc.perform(get("/api/v1/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.status").value("CREATED"));
    }

    @Test
    @DisplayName("존재하지 않는 주문 단건 조회 시 404 응답을 반환한다")
    void getOrder_whenNotFound_returns404() throws Exception {
        given(orderService.getOrder(99L))
            .willThrow(new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "주문을 찾을 수 없습니다."));

        mockMvc.perform(get("/api/v1/orders/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("COMMON-404"));
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
                          "quantity": 2
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("order created"))
            .andExpect(jsonPath("$.data.id").value(1))
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
                          "quantity": 0
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(orderService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("주문 생성 요청의 회원 ID가 없으면 400 응답을 반환한다")
    void createOrder_withoutMemberId_returnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/api/v1/orders")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "productId": 10,
                          "quantity": 2
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(orderService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("주문 생성 요청의 상품 ID가 없으면 400 응답을 반환한다")
    void createOrder_withoutProductId_returnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/api/v1/orders")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "memberId": 1,
                          "quantity": 2
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(orderService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("주문 확정 요청이 유효하면 CONFIRMED 상태의 주문을 반환한다")
    void confirmOrder_returnsConfirmedResponse() throws Exception {
        given(orderService.confirmOrder(1L))
            .willReturn(new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CONFIRMED"));

        mockMvc.perform(patch("/api/v1/orders/1/confirm"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("order confirmed"))
            .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("주문 취소 요청이 유효하면 CANCELLED 상태의 주문을 반환한다")
    void cancelOrder_returnsCancelledResponse() throws Exception {
        given(orderService.cancelOrder(1L))
            .willReturn(new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CANCELLED"));

        mockMvc.perform(patch("/api/v1/orders/1/cancel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("order cancelled"))
            .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(OrderController.class)
    static class TestApplication {
    }
}
