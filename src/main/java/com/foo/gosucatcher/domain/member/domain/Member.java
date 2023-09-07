package com.foo.gosucatcher.domain.member.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50, nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(length = 20, nullable = false)
	private String name;

	@Column(length = 20, unique = true)
	private String phoneNumber;

	@Column
	private boolean isDeleted = Boolean.FALSE;

	@Embedded
	@Column(nullable = false)
	private ImageFile profileImageFile;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Roles role;

	@Column
	private String refreshToken;

	@Builder
	public Member(String name, String password, String email, String phoneNumber, Roles role,
		ImageFile profileImageFile) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.profileImageFile = profileImageFile;
	}

	public void encodePassword(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(password);
	}

	public void changeMemberProfile(Member requestMember, PasswordEncoder passwordEncoder) {
		this.name = requestMember.getName();
		this.password = requestMember.getPassword();
		encodePassword(passwordEncoder);
		this.phoneNumber = requestMember.getPhoneNumber();
	}

	public void deleteMember() {
		this.isDeleted = true;
	}

	public void changeProfileImageFile(ImageFile profileImageFile) {
		if (profileImageFile == null) {
			throw new BusinessException(ErrorCode.INVALID_IMAGE);
		}

		this.profileImageFile = profileImageFile;
	}

	public void changeMemberRole(Roles role) {
		this.role = role;
	}

	public void refreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void authenticate(Member requestMember, PasswordEncoder passwordEncoder) {
		String requestPassword = requestMember.getPassword();
		if (!passwordEncoder.matches(requestPassword, this.password)) {
			throw new InvalidValueException(ErrorCode.LOG_IN_FAILURE);
		}
	}

	public void logout() {
		this.refreshToken = "";
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();

		String memberRole = role.getRole();
		authorities.add(() -> memberRole);

		return authorities;
	}
}
