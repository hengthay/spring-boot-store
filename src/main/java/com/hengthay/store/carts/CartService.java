package com.hengthay.store.carts;

import com.hengthay.store.products.ProductNotFoundException;
import com.hengthay.store.products.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    public List<CartDto> getAllCarts() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::toDto)
                .toList();
    }
    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(
            UUID cartId,
            Long productId
    ) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);

        if(cart == null)
            throw new CartNotFoundException();

        var product = productRepository.findById(productId).orElse(null);

        if(product == null)
            throw new ProductNotFoundException();

        // Add item to cart
        CartItem cartItem = cart.addItem(product);

        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public CartDto getCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);

        if(cart == null)
            throw new CartNotFoundException();

        return cartMapper.toDto(cart);
    }

    public CartItemDto updateCart(
            UUID cartId,
            Long productId,
            Integer quantity
    ) {
        // find cart
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null)
            throw new CartNotFoundException();

        // find productId in cart_items tables
        var cartItem = cart.getItem(productId);

        if(cartItem == null) {
            throw new ProductNotFoundException();
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public void removeCart(UUID cartId, Long productId) {
        // find cart
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);

        if(cart == null) {
            throw new CartNotFoundException();
        }

        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    public void clearCart(UUID cartId) {
        // find cart
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null)
            throw new CartNotFoundException();

        cart.clear();
        cartRepository.save(cart);
    }
}
