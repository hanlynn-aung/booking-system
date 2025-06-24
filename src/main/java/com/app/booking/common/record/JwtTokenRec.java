package com.app.booking.common.record;

import java.util.Date;

public record JwtTokenRec(String token, Date expiresAt) {
}
