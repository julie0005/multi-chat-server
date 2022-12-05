package org.ajou.multichatserver.user.service;

import static org.ajou.multichatserver.config.ErrorCode.*;
import static org.springframework.util.StringUtils.hasText;


import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.common.exception.InvalidRequestException;
import org.ajou.multichatserver.common.exception.NoSuchElementException;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User signIn(String principal, String credentials) {
        if (!hasText(principal) || !hasText(credentials)) {
            throw new InvalidRequestException(LOGIN_PARAM_REQUIRED);
        }
        User user = this.getUserByEmail(principal);
        user.checkPassword(passwordEncoder, credentials);
        return user;
    }

}
