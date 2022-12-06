package org.ajou.multichatserver.common.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.common.ErrorCode;
import org.ajou.multichatserver.common.ErrorResponse;
import org.ajou.multichatserver.common.exception.AuthErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, ErrorCode.TOKEN_EXPIRED);
        } catch (AuthErrorException e) {
            setErrorResponse(HttpStatus.BAD_REQUEST, response, ErrorCode.BLACKLIST_TOKEN_REQUEST);
        } catch (RuntimeException e) {
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, ErrorCode errorCode) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            String json = objectMapper.writeValueAsString(ErrorResponse.of(errorCode));
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
