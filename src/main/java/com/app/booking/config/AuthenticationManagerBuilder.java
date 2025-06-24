package com.app.booking.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationManagerBuilder {

    private final List<UserDetailsService> userDetailsServices;

    private final PasswordEncoder passwordEncoder;

    private AuthenticationManagerBuilder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsServices = new ArrayList<>();
    }

    public static AuthenticationManagerBuilder init(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "init Password Encoder not allow null.");
        return new AuthenticationManagerBuilder(passwordEncoder);
    }

    public AuthenticationManagerBuilder setUserDetailService(UserDetailsService userDetailsService) {
        Assert.notNull(userDetailsService, "UserDetailsService not allow null.");
        this.userDetailsServices.add(userDetailsService);
        return this;
    }

    public AuthenticationManager build() {
        final List<AuthenticationProvider> providerManagers = new ArrayList<>();
        for (UserDetailsService userDetailsService :
                userDetailsServices) {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(userDetailsService);
            provider.setPasswordEncoder(passwordEncoder);
            providerManagers.add(provider);
        }
        return new ProviderManager(providerManagers);
    }
}
