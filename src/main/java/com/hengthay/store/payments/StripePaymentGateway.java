package com.hengthay.store.payments;


import com.hengthay.store.orders.Order;
import com.hengthay.store.orders.OrderItem;
import com.hengthay.store.orders.PaymentStatus;
import com.hengthay.store.orders.OrderRepository;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

    private final OrderRepository orderRepository;

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            // Create a checkout session
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("order_id", order.getId().toString()) // for checkout.session.completed
                    .setPaymentIntentData(
                            SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("order_id", order.getId().toString()) // for payment_intent.succeeded
                                    .build()
                    );

            order.getItems().forEach((item) -> {
                var lineItem = createLineItem(item);
                // add each item to builder payment we created above
                builder.addLineItem(lineItem);
            });
            // this will return the session create parameter for checkout session
            var session = Session.create(builder.build());

            return new CheckoutSession(session.getUrl());
        } catch (StripeException ex) {
            System.out.println(ex.getMessage());
            throw new PaymentException();
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            // WebhookRequest -> { orderId, paymentStatus }
            var payload = request.getPayload(); // get payload from HTTP request

            var signature = request.getHeaders();// extract stripe headers
//            String signature = headers.get("stripe-signature");
//            if(headers == null) {
//                throw new RuntimeException("Empty header");
//            }

            if (signature == null || signature.isBlank()) {
                throw new PaymentException("Missing Stripe-Signature header");
            }
            // return event which is indicator that the payment is succeeded or failed or pending.
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);

            System.out.println(event.getType());
            return switch (event.getType()) {
                case "payment_intent.succeeded" ->
                    // Update order status (PAID)
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));

                case "payment_intent.payment_failed" ->
                    // Update order status (FAILED)
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));

                default -> Optional.empty();
            };

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Signature");
        }
    }

    private Long extractOrderId(Event event) {
//        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
//                () -> new PaymentException("Could not deserialize Stripe event. Check the SDK and API versions.")
//        ); // return stripe object
//        // charge -> (Charge) stripeObject
//        // payment_intent.succeeded -> (PaymentIntent) stripeObject
//        var paymentIntent = (PaymentIntent) stripeObject;
//        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));

        // Attempt safe deserialization first, fallback to unsafe if API versions mismatch
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseGet(() ->
                {
                    try {
                        return event.getDataObjectDeserializer().deserializeUnsafe();
                    } catch (EventDataObjectDeserializationException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        var paymentIntent = (PaymentIntent) stripeObject;
        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {
        // Create line item for stripe and predefine currency and unit price
        // for each item
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        // Set the price for each item
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmountDecimal(
                        item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        // Get the specific name of purchased item
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }


}
