package com.vishal.ecommerce.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
private Cart getOrCreateCart(User user) {
Cart cart = cartRepository.findByUserForUpdate(user);

    if (cart == null) {
        cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }

    if (cart.getItems() == null) {
        cart.setItems(new ArrayList<>());
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }

    return cart;
}

    private CartResDto mapToCartResDto(Cart cart) {

    List<CartItemResDto> itemDtos = new ArrayList<>();
    double total = 0;

List<CartItem> items = cart.getItems();

if (items == null) {
    items = new ArrayList<>();
}

for (CartItem item : items)  {
        if (item.getProduct() == null) continue;

        CartItemResDto dto = new CartItemResDto();
        dto.setId(item.getId());
        dto.setProductName(item.getProduct().getName());
        dto.setPrice(item.getProduct().getPrice());
        dto.setQuantity(item.getQuantity());

        total += item.getProduct().getPrice() * item.getQuantity();
        itemDtos.add(dto);
    }

    
    CartResDto response = new CartResDto();
    response.setId(cart.getId());
    response.setItems(itemDtos);
    response.setTotalPrice(total);

    return response;
}
    @Override
    @Transactional(readOnly = true)
    public CartResDto getCart(String username) {
        User user = userRepository.findByUsername(username);
        Cart cart = getOrCreateCart(user);
        return mapToCartResDto(cart);
    }

    @Override
@Transactional
public CartResDto addItem(String username, CartItemReqDto request) {

    User user = userRepository.findByUsername(username);
    if (user == null) {
        throw new ResourceNotFoundException("User not found");
    }

    Cart cart = getOrCreateCart(user);

    Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    if (request.getQuantity() == null || request.getQuantity() <= 0) {
        throw new BadRequestException("Quantity must be greater than zero");
    }

    if (product.getStock() < request.getQuantity()) {
        throw new BadRequestException("Not enough stock");
    }

    List<CartItem> items = cart.getItems();
    if (items == null) {
        items = new ArrayList<>();
        cart.setItems(items);
    }

    boolean exists = items.stream()
            .anyMatch(i -> i.getProduct() != null &&
                    i.getProduct().getId().equals(product.getId()));

    if (exists) {
        throw new BadRequestException("Product already exists in cart");
    }

    CartItem item = new CartItem();
    item.setCart(cart);
    item.setProduct(product);
    item.setQuantity(request.getQuantity());

    cartItemRepository.save(item);
    items.add(item);
    cartRepository.save(cart);

    return mapToCartResDto(cart);

}
    @Override
    public CartResDto updateItem(String username, Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId).orElse(null);

        if (item == null) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        validateCartItemOwnership(item, username);

        if (item.getProduct() == null) {
            throw new ResourceNotFoundException("Product not found for this cart item");
        }

        if (item.getProduct().getStock() < quantity) {
            throw new BadRequestException("Not enough stock available");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        return mapToCartResDto(cart);
    }

    @Override
    public void removeItem(String username, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId).orElse(null);

        if (item == null) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        validateCartItemOwnership(item, username);

        cartItemRepository.delete(item);
    }

    @Override
public void clearCart(String username) {

     User user = userRepository.findByUsername(username);
    if (user == null) {
        throw new ResourceNotFoundException("User not found");
    }

    Cart cart = getOrCreateCart(user);
    cart.getItems().clear();
    cartRepository.save(cart);
}

   private void validateCartItemOwnership(CartItem item, String username) {

    if (item == null
            || item.getCart() == null
            || item.getCart().getUser() == null
            || item.getCart().getUser().getUsername() == null) {
        throw new BadRequestException("Invalid cart item");
    }

    if (!item.getCart().getUser().getUsername().equals(username)) {
        throw new BadRequestException("Unauthorized access");
    }
}
}
