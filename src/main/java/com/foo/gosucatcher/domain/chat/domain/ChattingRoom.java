package com.foo.gosucatcher.domain.chat.domain;

import javax.persistence.*;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "chatting_rooms")
@SQLDelete(sql = "UPDATE chatting_rooms SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_estimate_id")
	private MemberEstimate memberEstimate;

	private boolean isDeleted = Boolean.FALSE;

	@Builder
	public ChattingRoom(MemberEstimate memberEstimate) {
		this.memberEstimate = memberEstimate;
	}
}
