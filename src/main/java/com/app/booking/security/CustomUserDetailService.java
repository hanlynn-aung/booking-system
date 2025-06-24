package com.app.booking.security;

import com.app.booking.common.constant.UserStatus;
import com.app.booking.entity.User;
import com.app.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("%s is not found.", username)));

        String phoneNumber = null;
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        return CustomUserDetail.create(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == UserStatus.ACTIVE,
                authorities
        );

    }
}
