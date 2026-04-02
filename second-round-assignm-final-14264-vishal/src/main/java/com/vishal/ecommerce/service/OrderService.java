package com.vishal.ecommerce.service;

import java.util.List;

import com.vishal.ecommerce.dto.req.OrderReqDto;
import com.vishal.ecommerce.dto.res.OrderResDto;

public interface OrderService {

        OrderResDto createOrder(String username, OrderReqDto request);
    OrderResDto getOrderById(String username, Long orderId);
    List<OrderResDto> getAllOrdersForUser(String username);
}
