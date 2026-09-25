package com.hengthay.store.orders;

import com.hengthay.store.carts.CartItemDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String status;
    private LocalDateTime createdAt;
    private List<CartItemDto> items;
    private BigDecimal totalPrice;
}
