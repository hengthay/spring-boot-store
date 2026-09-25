package com.hengthay.store.users;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

// Annotate as Mapper interface, and we have to pass componentModel as spring
// So spring can create bean at runtime.
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
    void update(UpdateUserRequest request, @MappingTarget User user);
}
