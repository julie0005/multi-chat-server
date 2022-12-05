package org.ajou.multichatserver.config;

import lombok.Getter;
import lombok.Setter;
import org.ajou.multichatserver.jwt.Jwt;
import org.ajou.multichatserver.jwt.JwtAuthenticationFilter;
import org.ajou.multichatserver.jwt.JwtAuthenticationProvider;
import org.ajou.multichatserver.jwt.JwtService;
import org.ajou.multichatserver.user.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Getter
@Setter
public class JwtConfig {
    private String issuer;
    private String clientSecret;
    private Token accessToken;
    private Token refreshToken;
    private String blackListPrefix;
    @Getter
    @Setter
    public static class Token {
        private String header;
        private int expirySeconds;

        @Override
        public String toString() {
            return "header: "+header+" expirySeconds: "+expirySeconds;
        }
    }

    @Bean
    @Qualifier("accessJwt")
    public Jwt accessJwt() {
        return new Jwt(
                this.issuer,
                this.clientSecret,
                this.accessToken.expirySeconds);
    }

    @Bean
    @Qualifier("refreshJwt")
    public Jwt refreshJwt() {
        return new Jwt(
                this.issuer,
                this.clientSecret,
                this.refreshToken.expirySeconds);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtService jwtService,
                                                               UserService userService) {
        return new JwtAuthenticationProvider(jwtService, userService);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                                                           UserService userService) {
        return new JwtAuthenticationFilter(this.accessToken.header, jwtService,
                userService);
    }
}
