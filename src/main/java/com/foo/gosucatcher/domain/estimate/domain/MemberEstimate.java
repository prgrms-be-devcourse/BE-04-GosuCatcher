package com.foo.gosucatcher.domain.estimate.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Where(clause = "is_closed = false")
@SQLDelete(sql = "UPDATE member_estimates SET is_closed = true, status = 'FINISH' WHERE id = ?")
@Table(name = "member_estimates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEstimate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	@OneToMany(mappedBy = "memberEstimate", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ExpertEstimate> expertEstimateList = new ArrayList<>();

	@OneToMany(mappedBy = "memberEstimate", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChattingRoom> chattingRoomList = new ArrayList<>();

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private LocalDateTime preferredStartDate;

	@Column(length = 500)
	private String detailedDescription;

	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expert_id")
	private Expert expert;

	private boolean isClosed = Boolean.FALSE;

	@Builder
	public MemberEstimate(Member member, SubItem subItem, String location, LocalDateTime preferredStartDate,
						  String detailedDescription) {
		this.member = member;
		this.subItem = subItem;
		this.location = location;
		this.preferredStartDate = validatePreferredStartDate(preferredStartDate);
		this.detailedDescription = detailedDescription;
	}

	public void addExpertEstimate(ExpertEstimate expertEstimate) {
		expertEstimateList.add(expertEstimate);
		expertEstimate.addMemberEstimate(this);
	}

	public void addChattingRoom(ChattingRoom chattingRoom) {
		chattingRoomList.add(chattingRoom);
	}

	public void removeChattingRoom(ChattingRoom chattingRoom) {
		chattingRoomList.remove(chattingRoom);
	}

	public void updateExpert(Expert expert) {
		this.expert = expert;
	}

	public void updateStatus(Status status) {
		this.status = status;
	}

	private LocalDateTime validatePreferredStartDate(LocalDateTime preferredStartDate) {
		if (LocalDateTime.now().isAfter(preferredStartDate)) {
			throw new BusinessException(ErrorCode.INVALID_MEMBER_ESTIMATE_START_DATE);
		}

		return preferredStartDate;
	}
}
