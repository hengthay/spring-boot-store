package com.hengthay.store.orders;

import com.hengthay.store.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var userId = authService.getCurrentUser();
        var order = orderRepository.getOrdersByCustomer(userId.getId());

        return order.stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrders(Long orderId) throws AccessDeniedException {
        var order = orderRepository.getOrderWithItems(orderId).orElseThrow(OrderNotFoundException::new);

        var user = authService.getCurrentUser();

        if(!order.isPlaced(user)) {
            throw new AccessDeniedException("Access Denied!");
        }

        return orderMapper.toDto(order);
    }
}
