package com.example.hotel_management_project.Utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component

public class JWTUtils {
    //Đối tượng ghi thông tin chương trình đang chạy
    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    @Value("${spring.app.jwtDefaultExpirationMs}")
    private Long jwtExpirationMs;
    @Value("${spring.app.jwtRememberExpirationMs}")
    private Long jwtRememberExpirationMs;
//    GET /api/user/profile
//    Authorization: Bearer abcxyz123
    public String getJwtSecretFromHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
    //JWT = Header.Payload.Signature
    //Header:{
    //  "alg": "HS256",
    //  "typ": "JWT"
    //}
    //Payload:{
    //  "sub": "username",
    //  "iat": 1516239022,
    //  "exp": 1516242622
    //Header,Payload mã hóa Base64URL
    //Phần đuôi Signature Header+Payload+Signature rồi dùng thuật toán kí của singature
    public String generateTokenFromUser(String username, boolean rememberMe) {

        Long expirationMs = rememberMe ? jwtRememberExpirationMs : jwtExpirationMs;
        return Jwts.builder()
                .subject(username) //Ghi thông tin username vào payload của token.
                .issuedAt(new Date())//Ghi lại thời điểm token được tạo.
                .expiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(key())
                .compact();

    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()//HEADER . PAYLOAD . SIGNATURE
                .getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().
                    parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}
