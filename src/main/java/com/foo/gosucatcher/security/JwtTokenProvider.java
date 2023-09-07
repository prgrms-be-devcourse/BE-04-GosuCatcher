package com.foo.gosucatcher.security;

import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final long ACCESS_TOKEN_EXPIRED_TIME = Duration.ofMinutes(5).toMillis();
	private static final long REFRESH_TOKEN_EXPIRED_TIME = Duration.ofMinutes(60).toMillis();

	private final String accessTokenSecretKey;
	private final String refreshTokenSecretKey;

	private final CustomUserDetailsService customUserDetailsService;

	public JwtTokenProvider(
		@Value("${spring.jwt.accessTokenSecretKey}")
		String accessTokenSecretKey,
		@Value("${spring.jwt.refreshTokenSecretKey}")
		String refreshTokenSecretKey,
		CustomUserDetailsService customUserDetailsService
	) {
		this.accessTokenSecretKey = getTokenSecretKey(accessTokenSecretKey);
		this.refreshTokenSecretKey = getTokenSecretKey(refreshTokenSecretKey);
		this.customUserDetailsService = customUserDetailsService;
	}

	public String createAccessToken(String memberEmail) {
		return getToken(memberEmail, ACCESS_TOKEN_EXPIRED_TIME, accessTokenSecretKey);
	}

	public String createRefreshToken(String memberEmail) {
		return getToken(memberEmail, REFRESH_TOKEN_EXPIRED_TIME, refreshTokenSecretKey);
	}

	public Authentication getAccessTokenAuthentication(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberEmail(token, accessTokenSecretKey));
		String email = userDetails.getUsername();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(email, password, authorities);
	}

	public Authentication getRefreshTokenAuthentication(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberEmail(token, refreshTokenSecretKey));
		String email = userDetails.getUsername();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(email, password, authorities);
	}

	public String resolveAccessToken(HttpServletRequest request) {
		return request.getHeader("AccessToken");
	}

	public boolean validateAccessToken(String token) {
		return extracted(token, accessTokenSecretKey);
	}

	public boolean validateRefreshToken(String token) {
		return extracted(token, accessTokenSecretKey);
	}

	public String bearerRemove(String token) {
		return token.substring("Bearer ".length());
	}

	private String getToken(String memberEmail, long tokenExpiredTime, String secretKey) {
		Date date = new Date();

		Claims claims = Jwts.claims()
			.setSubject("GosuCatcher");
		claims.put("memberEmail", memberEmail);

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setClaims(claims)
			.setIssuedAt(date)
			.setIssuer("GosuCatcher-server")
			.setExpiration(new Date(date.getTime() + tokenExpiredTime))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	private String getMemberEmail(String token, String secretKey) {
		log.warn("token : {}", token);
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("memberEmail")
			.toString();
	}

	private boolean extracted(String token, String secretKey) {
		token = bearerRemove(token);
		try {
			Jws<Claims> claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token);

			return !claims.getBody()
				.getExpiration()
				.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	private static String getTokenSecretKey(String accessTokenSecretKey) {
		return Base64
			.getEncoder()
			.encodeToString(accessTokenSecretKey.getBytes());
	}
}
