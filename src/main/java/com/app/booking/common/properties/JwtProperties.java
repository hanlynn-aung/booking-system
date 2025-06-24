package com.app.booking.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.app.jwt")
public class JwtProperties {

    private String password;

    private Integer expirationTime;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
    }
}