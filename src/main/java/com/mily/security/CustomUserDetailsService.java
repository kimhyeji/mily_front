package com.mily.security;

import com.mily.user.MilyUser;
import com.mily.user.MilyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
    private final MilyUserRepository milyUserRepository;

    @Override
    public UserDetails loadUserByUsername(String userLoginId) throws UsernameNotFoundException {
        MilyUser mu = milyUserRepository.findByUserLoginId(userLoginId).orElseThrow(() -> new UsernameNotFoundException("userLoginId(%s) not found".formatted(userLoginId)));

        if(mu.getRole().equals("waiting")) {
            throw new BadCredentialsException("현재 가입 승인 대기중입니다.");
        }

        return new User(mu.getUserLoginId(), mu.getUserPassword(), mu.getGrantedAuthorities());
    }
}