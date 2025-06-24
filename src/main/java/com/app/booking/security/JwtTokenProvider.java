package com.app.booking.security;

import com.app.booking.controller.response.AuthResponse;

public interface JwtTokenProvider {
    AuthResponse generateToken(TokenPayload tokenPayload);

    TokenPayload getPayloadFromToken(String token);

    boolean validateToken(String token);
}
