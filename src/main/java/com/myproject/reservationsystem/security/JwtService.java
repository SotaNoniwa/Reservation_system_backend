package com.myproject.reservationsystem.security;

import com.myproject.reservationsystem.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    // Replace this with a secure key in a real application, ideally fetched from environment variables
    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
private static final long EXPIRATION_TIME=1000*60;

    // Generate token with given username
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        System.out.println("generateToken: " + claims);
        return createToken(claims, user.getEmail());
    }

    // Create a JWT token with specified claims and subject (username)
    private String createToken(Map<String, Object> claims, String email) {
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +EXPIRATION_TIME)) // Token valid for 1 min
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
        System.out.println("createToken: " + jwt);
        return jwt;
    }

    // Get the signing key for JWT token
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the username from the token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        System.out.println("extractClaim: " + claims);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        Claims jwt = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("extractAllClaims: " + jwt);
        return jwt;
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the token against user details and expiration
    public Boolean validateToken(String token, CustomUserDetails user) {
        final String email = extractEmail(token);
        System.out.println("email in token: " + email);
        System.out.println("email in user : " + user.getEmail());
        return (email.equals(user.getEmail()) && !isTokenExpired(token));
    }
}
