package com.toyproject.order.web;

import com.toyproject.common.web.ApiResponse;
import com.toyproject.order.application.OrderService;
import com.toyproject.order.web.dto.CreateOrderRequest;
import com.toyproject.order.web.dto.OrderResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrders() {
        return ApiResponse.success(orderService.getOrders());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request), "order created");
    }
}

