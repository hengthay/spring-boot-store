package com.hengthay.store.auth;

import com.hengthay.store.users.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;


public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey secretKey) {
        this.claims = claims;
        this.secretKey = secretKey;
    }

    // Check if token is expired
    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    // get user id from claims object
    public Long getUserId() {
        return Long.valueOf(claims.getSubject());
    }

    // get user role from claims object
    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    @Override
    public String toString() {
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact(); // use compact to convert it to string
    }
}
