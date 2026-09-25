package com.hengthay.store.carts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartMapper {
    // Copy items from Cart to the items property of CartDto
    @Mapping(target = "items", source = "items")
    // For the totalPrice field in CartDto, execute this Java expression: cart.getTotalPrice().
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    // Use to mapping from Entity to Object
    CartDto toDto(Cart cart);

    Cart toEntity(CartDto cartDto);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);

    void update(CartDto cartDto,@MappingTarget Cart cart);
}
