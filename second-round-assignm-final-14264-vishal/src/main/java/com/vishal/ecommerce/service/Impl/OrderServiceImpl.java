package com.vishal.ecommerce.service.Impl;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import com.vishal.ecommerce.dto.req.OrderItemResDto;
import com.vishal.ecommerce.dto.req.OrderReqDto;
import com.vishal.ecommerce.dto.res.OrderResDto;
import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.CartItem;
import com.vishal.ecommerce.entity.Order;
import com.vishal.ecommerce.entity.OrderItem;
import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.CartRepository;
import com.vishal.ecommerce.repository.OrderItemRepository;
import com.vishal.ecommerce.repository.OrderRepository;
import com.vishal.ecommerce.repository.ProductRepository;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.CartService;
import com.vishal.ecommerce.service.OrderService;

import jakarta.validation.ValidationException;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderServiceImpl(UserRepository userRepository, CartRepository cartRepository,
                            OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            ProductRepository productRepository, CartService cartService) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public OrderResDto createOrder(String username, OrderReqDto request) {
        // Validate shipping address
        String address = request.getShippingAddress();
        if (address == null || address.trim().isEmpty() || address.length() > 255) {
            throw new ValidationException("Shipping address must be between 1 and 255 characters");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot create order from empty cart");
        }

        double total = 0.0;
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentStatus("PENDING");

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int qty = cartItem.getQuantity();

            if (product.getStockQuantity() < qty) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            // Deduct stock and save product
            product.setStockQuantity(product.getStockQuantity() - qty);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(qty);
            order.getItems().add(orderItem);
            orderItemRepository.save(orderItem);

            total += product.getPrice() * qty;
        }

        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order creation
        cartService.clearCart(username);

        return convertToDto(savedOrder);
    }
    @Override
    public OrderResDto getOrderById(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not authorized to view this order");
        }

        return convertToDto(order);
    }

    @Override
    public List<OrderResDto> getAllOrdersForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        return orders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private OrderResDto convertToDto(Order order) {
        List<OrderItemResDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemResDto(
                        item.getId(),
                        item.getProductName(),
                        item.getProductPrice(),
                        item.getQuantity(),
                        item.getProductPrice() * item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderResDto(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                order.getOrderDate(),
                order.getShippingAddress(),
                order.getTotalPrice(),
                order.getPaymentStatus(),
                itemDtos
        );
    }

}
