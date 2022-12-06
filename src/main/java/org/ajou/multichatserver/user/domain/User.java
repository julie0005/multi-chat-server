package org.ajou.multichatserver.user.domain;

import static org.ajou.multichatserver.common.ErrorCode.*;
import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ajou.multichatserver.config.BaseEntity;
import org.ajou.multichatserver.common.exception.AuthErrorException;
import org.ajou.multichatserver.common.exception.InvalidRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseEntity {
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private static final String NAME_REGEX = "[a-zA-Z가-힣]+( [a-zA-Z가-힣]+)*";

    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_NAME_LENGTH = 10;
    private static final int MAX_PASSWORD_LENGTH = 500;

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = MAX_EMAIL_LENGTH)
    private String email;
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(length = MAX_PASSWORD_LENGTH)
    private String password;

    public User(String email, String name, String password) {
        if (!hasText(email)) {
            throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
        }
        if (!hasText(name)) {
            throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
        }

        validateEmail(email);
        validateName(name);
        validatePassword(password);

        this.name = name;
        this.email = email;
    }

    private static void validateName(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidRequestException(INVALID_LENGTH);
        }
        if (!Pattern.matches(NAME_REGEX, name)) {
            throw new InvalidRequestException(INVALID_INPUT_VALUE);
        }
    }

    private static void validateEmail(String email) {
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new InvalidRequestException(INVALID_LENGTH);
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new InvalidRequestException(INVALID_INPUT_VALUE);
        }
    }

    public void checkPassword(PasswordEncoder passwordEncoder, String credentials) {
        if (!passwordEncoder.matches(credentials, password)) {
            throw new AuthErrorException(INVALID_ACCOUNT_REQUEST);
        }
    }

    public static void validatePassword(String password) {
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidRequestException(INVALID_LENGTH);
        }
        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new InvalidRequestException(INVALID_INPUT_VALUE);
        }
    }
}
