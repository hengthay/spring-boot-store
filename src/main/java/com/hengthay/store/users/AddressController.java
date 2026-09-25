package com.hengthay.store.users;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/addresses")
@Tag(name = "Address")
public class AddressController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getAllAddresses(
            @RequestParam(required = false, defaultValue = "", name = "userId")
            Long userId
    ) {
        List<Address> addresses;

        if(userId != null) {
            addresses = addressRepository.findByUserId(userId);
        } else {
            addresses = addressRepository.findAllWithUser();
        }

        if(addresses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<AddressDto> addressDtos = addresses.stream()
                .map(addressMapper::toDto)
                .toList();

        return ResponseEntity.ok(addressDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long id) {
        var address = addressRepository.findById(id).orElse(null);

        if(address == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(addressMapper.toDto(address));
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(
        @RequestBody AddressDto addressDto,
        UriComponentsBuilder uriBuilder
    ) {
        var user = userRepository.findById(Long.valueOf(addressDto.getUserId())).orElse(null);

        if(user == null)
            return ResponseEntity.notFound().build();

        var address = addressMapper.toEntity(addressDto);
        address.setUser(user);
        addressRepository.save(address);
        addressDto.setId(address.getId());

        var uri = uriBuilder.path("/addresses/{id}").buildAndExpand(addressDto.getId()).toUri();

        return ResponseEntity.created(uri).body(addressDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressDto addressDto
    ) {
        var user = userRepository.findById(addressDto.getUserId()).orElse(null);

        if(user == null)
            return ResponseEntity.notFound().build();

        var address = addressRepository.findById(id).orElse(null);

        if(address == null)
            return ResponseEntity.notFound().build();

        addressMapper.update(addressDto, address);
        address.setUser(user);
        addressRepository.save(address);
        addressDto.setId(address.getId());

        return ResponseEntity.ok(addressDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AddressDto> deleteAddress(
        @PathVariable Long id
    ) {
        var address = addressRepository.findById(id).orElse(null);

        if(address == null)
            return ResponseEntity.notFound().build();

        addressRepository.delete(address);

        return ResponseEntity.noContent().build();
    }
}
