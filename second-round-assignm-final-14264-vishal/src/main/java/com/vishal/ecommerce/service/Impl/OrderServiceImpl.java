package com.vishal.ecommerce.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vishal.ecommerce.dto.req.OrderReqDto;
import com.vishal.ecommerce.dto.res.OrderResDto;
import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.Order;
import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.CartRepository;
import com.vishal.ecommerce.repository.OrderRepository;
import com.vishal.ecommerce.repository.ProductRepository;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
private ProductRepository productRepository;


    @Autowired
    private CartRepository cartRepository;

    @Override
    public OrderResDto createOrder(String username, OrderReqDto request) {

        User user = userRepository.findByUsername(username);
        Cart cart = cartRepository.findByUser(user);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        List<Product> products = new ArrayList<>();
        double total = 0;

        for (var item : cart.getItems()) {

    if (item.getProduct().getStock() < item.getQuantity()) {
        throw new BadRequestException("Not enough stock for product: " + item.getProduct().getName());
    }

    item.getProduct().setStock(item.getProduct().getStock() - item.getQuantity());
    productRepository.save(item.getProduct());

    products.add(item.getProduct());
    total += item.getProduct().getPrice() * item.getQuantity();
}

        Order order = new Order();
        order.setUser(user);
        order.setProducts(products);
        order.setTotalPrice(total);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResDto(order);
    }

    @Override
    public OrderResDto getOrderById(String username, Long orderId) {

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        return mapToOrderResDto(order);
    }

    @Override
    public List<OrderResDto> getAllOrders(String username) {

        User user = userRepository.findByUsername(username);
        List<Order> orders = orderRepository.findByUser(user);

        List<OrderResDto> response = new ArrayList<>();
        for (Order order : orders) {
            response.add(mapToOrderResDto(order));
        }

        return response;
    }

    private OrderResDto mapToOrderResDto(Order order) {

        List<String> productNames = new ArrayList<>();
        for (Product product : order.getProducts()) {
            productNames.add(product.getName());
        }

        OrderResDto response = new OrderResDto();
        response.setId(order.getId());
        response.setProductNames(productNames);
        response.setTotalPrice(order.getTotalPrice());
        response.setShippingAddress(order.getShippingAddress());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setCreatedAt(order.getCreatedAt());

        return response;
    }
    
}
