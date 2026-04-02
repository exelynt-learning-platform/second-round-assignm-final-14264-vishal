package com.vishal.ecommerce.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vishal.ecommerce.dto.req.CartItemReqDto;
import com.vishal.ecommerce.dto.res.CartItemResDto;
import com.vishal.ecommerce.dto.res.CartResDto;
import com.vishal.ecommerce.entity.Cart;
import com.vishal.ecommerce.entity.CartItem;
import com.vishal.ecommerce.entity.Product;
import com.vishal.ecommerce.entity.User;
import com.vishal.ecommerce.exception.BadRequestException;
import com.vishal.ecommerce.exception.ResourceNotFoundException;
import com.vishal.ecommerce.repository.CartItemRepository;
import com.vishal.ecommerce.repository.CartRepository;
import com.vishal.ecommerce.repository.ProductRepository;
import com.vishal.ecommerce.repository.UserRepository;
import com.vishal.ecommerce.service.CartService;
import com.vishal.ecommerce.service.StockValidator;

@Service
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final StockValidator stockValidator;

    public CartServiceImpl(UserRepository userRepository, CartRepository cartRepository,
                           CartItemRepository cartItemRepository, ProductRepository productRepository,
                           StockValidator stockValidator) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.stockValidator = stockValidator;
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    @Override
    public CartResDto getCart(String username) {
        User user = getUserOrThrow(username);
        Cart cart = getOrCreateCart(user);
        return convertToDto(cart);
    }

    @Override
    @Transactional
    public CartResDto addItemToCart(String username, CartItemReqDto request) {
        User user = getUserOrThrow(username);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        stockValidator.validateStock(product, request.getQuantity());

        Cart cart = getOrCreateCart(user);

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            stockValidator.validateStockForAddition(product, existingItem.getQuantity(), request.getQuantity());
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public CartResDto updateCartItem(String username, Long itemId, Integer quantity) {
        if (quantity == null) {
            throw new BadRequestException("Quantity cannot be null");
        }

        User user = getUserOrThrow(username);
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            Product product = item.getProduct();
            stockValidator.validateStockForUpdate(product, quantity);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public CartResDto removeCartItem(String username, Long itemId) {
        User user = getUserOrThrow(username);
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(String username) {
        User user = getUserOrThrow(username);
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
    }

    private double calculateSubtotal(Product product, int quantity) {
        if (product.getPrice() == null) {
            throw new IllegalStateException("Product price cannot be null for product: " + product.getName());
        }
        return product.getPrice() * quantity;
    }

    private CartItemResDto convertItemToDto(CartItem item) {
        Product product = item.getProduct();
        if (product == null) {
            throw new IllegalStateException("Cart item references missing product");
        }
        double subtotal = calculateSubtotal(product, item.getQuantity());
        return new CartItemResDto(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                subtotal
        );
    }

    private CartResDto convertToDto(Cart cart) {
        List<CartItemResDto> itemDtos = cart.getItems().stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());

        double total = itemDtos.stream().mapToDouble(CartItemResDto::getSubtotal).sum();
        return new CartResDto(cart.getId(), itemDtos, total);
    }
}