package com.toyproject.inventory.web;

import com.toyproject.common.web.GlobalExceptionHandler;
import com.toyproject.inventory.application.InventoryService;
import com.toyproject.inventory.web.dto.StockItemResponse;
import com.toyproject.inventory.web.dto.UpsertStockRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@Import(GlobalExceptionHandler.class)
class InventoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Test
    @DisplayName("재고 목록 조회 요청이 유효하면 재고 정보를 반환한다")
    void getInventory_returnsInventoryResponses() throws Exception {
        given(inventoryService.getInventory()).willReturn(List.of(
            new StockItemResponse(1L, 10L, 50)
        ));

        mockMvc.perform(get("/api/v1/inventory"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].productId").value(10))
            .andExpect(jsonPath("$.data[0].quantity").value(50));
    }

    @Test
    @DisplayName("재고 갱신 요청이 유효하면 갱신된 재고 정보를 반환한다")
    void upsertStock_returnsUpdatedResponse() throws Exception {
        given(inventoryService.upsertStock(eq(10L), any(UpsertStockRequest.class)))
            .willReturn(new StockItemResponse(1L, 10L, 80));

        mockMvc.perform(
                put("/api/v1/inventory/{productId}", 10L)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "quantity": 80
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("stock updated"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.productId").value(10))
            .andExpect(jsonPath("$.data.quantity").value(80));
    }

    @Test
    @DisplayName("재고 갱신 요청의 수량이 음수이면 400 응답을 반환한다")
    void upsertStock_withNegativeQuantity_returnsBadRequest() throws Exception {
        mockMvc.perform(
                put("/api/v1/inventory/{productId}", 10L)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "quantity": -1
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("quantity: quantity must be zero or greater"))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(inventoryService).shouldHaveNoInteractions();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(InventoryController.class)
    static class TestApplication {
    }
}
