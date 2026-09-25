package com.hengthay.store.payments;

import com.hengthay.store.orders.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
    Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);
}
