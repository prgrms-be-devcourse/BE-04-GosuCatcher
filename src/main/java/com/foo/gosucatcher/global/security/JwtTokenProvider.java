package com.foo.gosucatcher.global.security;

import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.security.exception.JwtExpiredException;
import com.foo.gosucatcher.global.security.exception.JwtHeaderException;
import com.foo.gosucatcher.global.security.exception.JwtKeyException;
import com.foo.gosucatcher.global.security.exception.JwtParseException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {

	private final CustomUserDetailsService customUserDetailsService;

	private final long ACCESS_TOKEN_EXPIRED_TIME;
	private final long REFRESH_TOKEN_EXPIRED_TIME;
	private final String ACCESS_TOKEN_SECRET_KEY;
	private final String REFRESH_TOKEN_SECRET_KEY;

	public JwtTokenProvider(CustomUserDetailsService customUserDetailsService, JwtProperties jwtProperties) {
		this.customUserDetailsService = customUserDetailsService;
		this.ACCESS_TOKEN_EXPIRED_TIME = Duration.ofDays(jwtProperties.getAccessTokenExpiredTime()).toMillis();
		this.REFRESH_TOKEN_EXPIRED_TIME = Duration.ofDays(jwtProperties.getRefreshTokenExpiredTime()).toMillis();
		this.ACCESS_TOKEN_SECRET_KEY = getTokenSecretKey(jwtProperties.getAccessTokenSecretKey());
		this.REFRESH_TOKEN_SECRET_KEY = getTokenSecretKey(jwtProperties.getRefreshTokenSecretKey());
	}

	public String removeBearer(String token) {
		try {
			return token.substring("Bearer ".length());
		} catch (IndexOutOfBoundsException e) {
			throw new JwtHeaderException(ErrorCode.SHORT_OF_JWT_LENGTH);
		}
	}

	public void checkValidAccessToken(String token) {
		checkValidToken(token, ACCESS_TOKEN_SECRET_KEY);
	}

	public void checkValidRefreshToken(String token) {
		checkValidToken(token, REFRESH_TOKEN_SECRET_KEY);
	}

	private void checkValidToken(String token, String secretKey) {
		Jws<Claims> claims = parseClaimsJws(token, secretKey);
		try {
			boolean isBefore = claims.getBody()
				.getExpiration()
				.before(new Date());
			if (isBefore) {
				throw new JwtExpiredException(ErrorCode.EXPIRED_JWT);
			}
		} catch (NullPointerException e) {
			throw new JwtParseException(ErrorCode.EMPTY_OR_NULL_JWT);
		}
	}

	private String getTokenSecretKey(String accessTokenSecretKey) {
		try {
			return Base64.getEncoder()
				.encodeToString(accessTokenSecretKey.getBytes());
		} catch (NullPointerException e) {
			throw new JwtParseException(ErrorCode.INVALID_SECRET_KEY);
		}
	}

	public String createAccessToken(Member member, Expert expert) {
		return getToken(member, expert, ACCESS_TOKEN_EXPIRED_TIME, ACCESS_TOKEN_SECRET_KEY);
	}

	public String createRefreshToken(Member member, Expert expert) {
		return getToken(member, expert, REFRESH_TOKEN_EXPIRED_TIME, REFRESH_TOKEN_SECRET_KEY);
	}

	private String getToken(Member member, Expert expert, long tokenExpiredTime, String secretKey) {
		Long memberId = member.getId();
		String memberEmail = member.getEmail();
		Long expertId = expert.getId();

		Claims claims = Jwts.claims()
			.setSubject("GosuCatcher");
		claims.put("memberId", memberId);
		claims.put("memberEmail", memberEmail);
		claims.put("expertId", expertId);

		Date date = new Date();
		try {
			return Jwts.builder()
				.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
				.setClaims(claims)
				.setIssuedAt(date)
				.setIssuer("GosuCatcher-server")
				.setExpiration(new Date(date.getTime() + tokenExpiredTime))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
		} catch (InvalidKeyException e) {
			throw new JwtKeyException(ErrorCode.INVALID_SECRET_KEY);
		}
	}

	public Authentication getAccessTokenAuthentication(String token) {
		String memberEmail = parseMemberEmailByToken(token, ACCESS_TOKEN_SECRET_KEY);
		CustomUserDetails userDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(memberEmail);

		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
	}

	private String parseMemberEmailByToken(String token, String secretKey) {
		Jws<Claims> claimsJws = parseClaimsJws(token, secretKey);
		try {
			return claimsJws.getBody()
				.get("memberEmail")
				.toString();
		} catch (NullPointerException e) {
			throw new JwtParseException(ErrorCode.EMPTY_OR_NULL_JWT);
		}
	}

	private static Jws<Claims> parseClaimsJws(String token, String secretKey) {
		try {
			return Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token);
		} catch (MalformedJwtException e) {
			throw new JwtParseException(ErrorCode.MALFORMED_JWT);
		} catch (ExpiredJwtException e) {
			throw new JwtExpiredException(ErrorCode.EXPIRED_JWT);
		} catch (UnsupportedJwtException e) {
			throw new JwtParseException(ErrorCode.UNSUPPORTED_JWT);
		} catch (SignatureException e) {
			throw new JwtParseException(ErrorCode.INVALID_SIGNATURE);
		} catch (IllegalArgumentException e) {
			throw new JwtParseException(ErrorCode.EMPTY_OR_NULL_JWT);
		}
	}

	public UserDetails getCustomUserDetailsByRefreshToken(String refreshToken) {
		String memberEmail = parseMemberEmailByToken(refreshToken, REFRESH_TOKEN_SECRET_KEY);

		return customUserDetailsService.loadUserByUsername(memberEmail);
	}
}
