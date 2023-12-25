package com.bee.chat.security;

import com.bee.chat.exception.TokenRefreshException;
import com.bee.chat.model.JwtToken;
import com.bee.chat.model.User;
import com.bee.chat.repository.JwtTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInSeconds}")
    private long jwtExpirationInSeconds;

    @Value("${app.jwtRefreshTokenExpirationInSeconds}")
    private long jwtRefreshTokenExpirationInSeconds;

    @Value("${app.jwtIssuer}")
    private String jwtIssuer;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isAccessTokenValid(String token) {
        return !isAccessTokenExpired(token) && isAccessTokenExist(token);
    }

    public JwtToken generateToken(User user, UserDetails userDetails) {
        JwtToken jwtToken;
        Optional<JwtToken> jwtTokenOptional = jwtTokenRepository.findByUser(user);

        if(jwtTokenOptional.isPresent()){
            jwtToken = jwtTokenOptional.get();
        } else {
            jwtToken = new JwtToken();
            jwtToken.setUser(user);
        }

        jwtToken.setRefreshTokenExpiryDate(Instant.now().plusSeconds(jwtRefreshTokenExpirationInSeconds));
        jwtToken.setRefreshToken(generateRefreshToken());
        jwtToken.setAccessToken(generateAccessToken(new HashMap<>(), userDetails));

        jwtToken = jwtTokenRepository.save(jwtToken);

        return jwtToken;
    }

    public Optional<JwtToken> findRefreshTokenByToken(String token) {
        return jwtTokenRepository.findByRefreshToken(token);
    }

    public JwtToken verifyRefreshTokenExpiration(JwtToken token) {
        if (token.getRefreshTokenExpiryDate().compareTo(Instant.now()) < 0) {
            jwtTokenRepository.delete(token);
            throw new TokenRefreshException(token.getRefreshToken(), "Invalid refresh token. Please make a new signin request");
        }

        return token;
    }

    public int deleteRefreshTokenByUser(User user) {
        return jwtTokenRepository.deleteByUser(user);
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (jwtExpirationInSeconds * 1000)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isAccessTokenExist(String token) {
        return jwtTokenRepository.findByAccessToken(token).isPresent();
    }

    private boolean isAccessTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
