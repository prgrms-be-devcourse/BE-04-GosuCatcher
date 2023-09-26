package com.foo.gosucatcher.global.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.ErrorResponse;
import com.foo.gosucatcher.global.security.exception.JwtValueException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String AuthorizationHeaderValue = request.getHeader("Authorization");
			if (AuthorizationHeaderValue != null) {
				String accessToken = jwtTokenProvider.removeBearer(AuthorizationHeaderValue);

				jwtTokenProvider.checkValidAccessToken(accessToken);

				Authentication authentication = jwtTokenProvider.getAccessTokenAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);
		} catch (JwtValueException e) {
			response.setStatus(401);
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");

			ErrorCode errorCode = e.getErrorCode();
			ErrorResponse errorResponse = ErrorResponse.of(errorCode);

			new ObjectMapper().writeValue(response.getWriter(), errorResponse);
		} catch (RuntimeException e) {
			new ObjectMapper().writeValue(response.getWriter(), e.getMessage());
		}
	}
}
