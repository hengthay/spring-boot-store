package com.hengthay.store.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Return single object
    Optional<User> findUserByEmail(String email);
}
