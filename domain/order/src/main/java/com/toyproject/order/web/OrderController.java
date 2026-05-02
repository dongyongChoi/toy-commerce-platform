package com.toyproject.order.web;

import com.toyproject.common.web.ApiResponse;
import com.toyproject.order.application.OrderService;
import com.toyproject.order.web.dto.CreateOrderRequest;
import com.toyproject.order.web.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ApiResponse.success(orderService.getOrder(orderId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request), "order created");
    }

    @PatchMapping("/{orderId}/confirm")
    public ApiResponse<OrderResponse> confirmOrder(@PathVariable Long orderId) {
        return ApiResponse.success(orderService.confirmOrder(orderId), "order confirmed");
    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ApiResponse.success(orderService.cancelOrder(orderId), "order cancelled");
    }
}
