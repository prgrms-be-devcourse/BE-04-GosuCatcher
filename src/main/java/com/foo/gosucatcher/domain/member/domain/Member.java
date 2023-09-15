package com.foo.gosucatcher.domain.member.domain;

import static com.foo.gosucatcher.global.error.ErrorCode.EMPTY_IMAGE;

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

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE members SET is_deleted = true WHERE id = ?")
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "is_deleted")
	private boolean isDeleted = Boolean.FALSE;

	@Embedded
	@Column(name = "profile_image_file")
	private ImageFile profileImageFile;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Roles role;

	@Column(name = "refresh_token", length = 500)
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

	public void authenticate(Member requestMember, PasswordEncoder passwordEncoder) {
		String requestPassword = requestMember.getPassword();
		boolean isMatchedPassword = passwordEncoder.matches(requestPassword, this.password);
		boolean isWithdrawnMember = isDeleted;

		if (isWithdrawnMember || !isMatchedPassword) {
			throw new InvalidValueException(ErrorCode.LOG_IN_FAILURE);
		}
	}

	public void changePassword(String password, PasswordEncoder passwordEncoder) {
		this.password = encodePassword(password, passwordEncoder);
	}

	public void updateProfile(Member requestMember, PasswordEncoder passwordEncoder) {
		this.name = requestMember.getName();
		this.password = encodePassword(requestMember.getPassword(), passwordEncoder);
		this.phoneNumber = requestMember.getPhoneNumber();
	}

	private String encodePassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.encode(password);
	}

	public void updateProfileImage(ImageFile profileImageFile) {
		if (profileImageFile == null) {
			throw new BusinessException(EMPTY_IMAGE);
		}

		this.profileImageFile = profileImageFile;
	}

	public void updateMemberRole(Roles role) {
		this.role = role;
	}

	public void refreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void logout() {
		this.refreshToken = "";
	}

	public void isSameMemberName(String requestName) {
		if (!name.equals(requestName)) {
			throw new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER);
		}
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
