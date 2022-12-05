package org.ajou.multichatserver.user.service;

import static org.ajou.multichatserver.config.ErrorCode.*;


import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.config.exception.NoSuchElementException;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
    }

}
