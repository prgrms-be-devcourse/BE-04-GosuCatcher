package com.foo.gosucatcher.config;

import static com.foo.gosucatcher.global.error.ErrorCode.INVALID_TOKEN;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.foo.gosucatcher.domain.member.exception.MemberCertifiedFailException;
import com.foo.gosucatcher.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
		if (stompHeaderAccessor.getCommand() == StompCommand.CONNECT) {
			String accessToken = stompHeaderAccessor.getFirstNativeHeader("Authorization");
			if (!jwtTokenProvider.isValidAccessToken(accessToken)) {
				throw new MemberCertifiedFailException(INVALID_TOKEN);
			}
		}

		return message;
	}
}
