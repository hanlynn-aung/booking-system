package com.app.booking.security;

import com.app.booking.common.properties.JwtProperties;
import com.app.booking.controller.response.AuthResponse;
import com.app.booking.util.AppUtils;
import com.app.booking.util.Builder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Password;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final String TOKEN_PAYLOAD = "payload";

    private final JwtProperties jwtProperties;

    public JwtTokenProviderImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public AuthResponse generateToken(TokenPayload tokenPayload) {
        Password password = Keys.password(this.jwtProperties.getPassword().toCharArray());
        Date expiresAt = new Date(System.currentTimeMillis() + this.jwtProperties.getExpirationTime());

        String token = Jwts.builder()
                .claim(TOKEN_PAYLOAD, AppUtils.convertToJson(tokenPayload))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiresAt)
                .encryptWith(password, Jwts.KEY.PBES2_HS512_A256KW, Jwts.ENC.A256GCM)
                .compact();

        return Builder.of(AuthResponse::new)
                .add(AuthResponse::setAccessToken, token)
                .build();
    }

    @Override
    public TokenPayload getPayloadFromToken(String token) {
        Password password = Keys.password(this.jwtProperties.getPassword().toCharArray());
        Claims claims = Jwts.parser().decryptWith(password)
                .build().parseEncryptedClaims(token).getPayload();
        return getPayload(claims);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Password password = Keys.password(this.jwtProperties.getPassword().toCharArray());
            Jwts.parser()
                    .decryptWith(password)
                    .build().parseEncryptedClaims(token);
            return true;
        } catch (ExpiredJwtException var1) {
            log.error("validateToken() Token Expired");
            log.error("", var1);
        } catch (Exception e) {
            log.error("validateToken() Invalid Json Token");
            log.error("", e);
        }
        return false;
    }

    private TokenPayload getPayload(Claims claims) {
        if (claims.get(TOKEN_PAYLOAD) != null) {
            return AppUtils.convertJsonToObject(claims.get(TOKEN_PAYLOAD).toString(), TokenPayload.class);
        }
        return null;
    }
}
