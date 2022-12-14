package org.ajou.multichatserver.user.service;

import static org.ajou.multichatserver.common.ErrorCode.LOGIN_PARAM_REQUIRED;
import static org.ajou.multichatserver.common.ErrorCode.USER_ALREADY_EXISTS;
import static org.ajou.multichatserver.common.ErrorCode.USER_NOT_FOUND;
import static org.springframework.util.StringUtils.hasText;

import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.common.exception.AlreadyExistsException;
import org.ajou.multichatserver.common.exception.InvalidRequestException;
import org.ajou.multichatserver.common.exception.NoSuchElementException;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.dto.request.UserSignUpRequest;
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

    @Transactional
    public User signUp(UserSignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException(USER_ALREADY_EXISTS);
        }
        //TODO: ????????? ?????? ??????

        User.validatePassword(request.getPassword());

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .encodedPassword(passwordEncoder.encode(request.getPassword()))
                .build();
        return userRepository.save(newUser);
    }

}
