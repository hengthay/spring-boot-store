package com.hengthay.store.users;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT a from Address a")
    List<Address> findAllWithUser();
}