package org.ajou.multichatserver.user.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.common.ApiResponse;
import org.ajou.multichatserver.jwt.JwtAuthenticationToken;
import org.ajou.multichatserver.jwt.JwtPrincipal;
import org.ajou.multichatserver.user.domain.User;
import org.ajou.multichatserver.user.dto.request.UserSignInRequest;
import org.ajou.multichatserver.user.dto.request.UserSignUpRequest;
import org.ajou.multichatserver.user.dto.response.SignInResponse;
import org.ajou.multichatserver.user.dto.response.SignUpResponse;
import org.ajou.multichatserver.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@RestController
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/local/signin")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(
            @RequestBody @Valid UserSignInRequest request) {
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(),
                request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        String refreshToken = (String) authentication.getDetails();
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        ApiResponse response = ApiResponse.builder()
                .message("로그인 성공하였습니다.")
                .status(OK.value())
                .data(SignInResponse.builder()
                        .userId(principal.getUser().getId())
                        .accessToken(principal.getAccessToken())
                        .refreshToken(refreshToken)
                        .build())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", principal.getAccessToken());
        headers.add("refreshToken", refreshToken);
        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody @Valid
                                                              UserSignUpRequest request) {
        User newUser = userService.signUp(request);
        ApiResponse response = ApiResponse.builder()
                .message("회원가입 성공하였습니다.")
                .status(CREATED.value())
                .data(SignUpResponse.from(newUser))
                .build();
        return ResponseEntity.created(URI.create("/signup")).body(response);
    }
}
