package com.hengthay.store.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// A DTO(Data Transfers Object) is a simple Java object used
// to transfer data between different layers of an application
// We can expose what we want client to see
@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
