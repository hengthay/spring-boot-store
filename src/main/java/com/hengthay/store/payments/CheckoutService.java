package com.hengthay.store.payments;

import com.hengthay.store.orders.Order;
import com.hengthay.store.carts.CartEmptyException;
import com.hengthay.store.carts.CartNotFoundException;
import com.hengthay.store.carts.CartRepository;
import com.hengthay.store.orders.OrderRepository;
import com.hengthay.store.auth.AuthService;
import com.hengthay.store.carts.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);

        if (cart == null)
            throw new CartNotFoundException();

        if (cart.isEmpty())
            throw new CartEmptyException();

        var order = Order.fromCartItem(cart, authService.getCurrentUser());

        orderRepository.save(order);

        try {
            // Create checkout session
            var session = paymentGateway.createCheckoutSession(order);

            cartService.clearCart(cart.getId());

            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException ex) {
            orderRepository.delete(order);
            throw ex;
        }
    }

    @Transactional
    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway
            .parseWebhookRequest(request)
            .ifPresent(paymentResult -> {
                var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                order.setStatus(paymentResult.getPaymentStatus());
                orderRepository.save(order);
            });
    }
}
