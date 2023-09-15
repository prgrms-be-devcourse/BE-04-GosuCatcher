package com.foo.gosucatcher.domain.member.application.dto.response;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.foo.gosucatcher.domain.member.exception.EmailMessagingException;
import com.foo.gosucatcher.global.error.ErrorCode;

public record MemberEmailSendResponse(
	String receiver,
	Long expirationTime,
	Boolean isSuccess
) {

	public static MemberEmailSendResponse from(MimeMessage message, Long expirationTime) {
		try {
			String to = message.getRecipients(Message.RecipientType.TO)[0].toString();

			return new MemberEmailSendResponse(to, expirationTime, true);
		} catch (MessagingException e) {
			throw new EmailMessagingException(ErrorCode.INVALID_EMAIL_FORMAT);
		}
	}
}
