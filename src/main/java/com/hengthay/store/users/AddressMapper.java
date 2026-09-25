package com.hengthay.store.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);
    Address toEntity(AddressDto addressDto);
    @Mapping(target = "id", ignore = true)
    void update(AddressDto addressDto,@MappingTarget Address address);
}
