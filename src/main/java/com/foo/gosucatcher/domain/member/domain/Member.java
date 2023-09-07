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

import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

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

	@Embedded
	@Column(nullable = false)
	private ImageFile profileImageFile;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Roles role;

	@Column
	private String refreshToken;

	@Column
	private boolean isDeleted = Boolean.FALSE;

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

	public void logout() {
		this.refreshToken = "";
	}

	public void refresh(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void changeMemberInfo(Member member) {
		this.name = member.getName();
		this.password = member.getPassword();
		this.phoneNumber = member.getPhoneNumber();
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
