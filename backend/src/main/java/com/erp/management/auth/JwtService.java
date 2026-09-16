package com.erp.management.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${app.security.jwt.secret}") String secret,
                      @Value("${app.security.jwt.expiration-ms:900000}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public long getExpirationMs() { return expirationMs; }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            return user.getUsername().equals(extractUsername(token)) && !extractExpiration(token).before(new Date());
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private Date extractExpiration(String token) { return extractClaim(token, Claims::getExpiration); }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload());
    }
}
