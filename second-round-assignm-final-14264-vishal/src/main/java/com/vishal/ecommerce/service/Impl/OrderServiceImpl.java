package com.vishal.ecommerce.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

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

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @Override
    public OrderResDto createOrder(String username, OrderReqDto request) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + username);
        }

        Cart cart = cartRepository.findByUser(user);

        if (cart == null || cart.getItems()== null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        List<Product> products = new ArrayList<>();
        double total = 0;

        List<com.vishal.ecommerce.entity.CartItem> cartItemsCopy =
        new ArrayList<>(cart.getItems());

for (var item : cartItemsCopy) {

            Product product = item.getProduct();

            if (product == null) {
                throw new ResourceNotFoundException("Product not found in cart item");
            }

            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            products.add(product);
            total += product.getPrice() * item.getQuantity();
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

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return mapToOrderResDto(order);
    }

    @Override
    public List<OrderResDto> getAllOrders(String username) {

        User user = userRepository.findByUsername(username);
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream().map(this::mapToOrderResDto).collect(Collectors.toList());
    }

    private OrderResDto mapToOrderResDto(Order order) {

        List<String> productNames = order.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toList());

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
