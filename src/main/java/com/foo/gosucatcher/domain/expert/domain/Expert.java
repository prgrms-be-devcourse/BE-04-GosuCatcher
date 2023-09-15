package com.foo.gosucatcher.domain.expert.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE experts SET is_deleted = true WHERE id = ?")
@Getter
@Entity
@Table(name = "experts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expert extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ExpertItem> expertItemList = new ArrayList<>();

	@Column(length = 20)
	private String storeName;

	private String location;

	private int maxTravelDistance;

	@Lob
	private String description;

	@Column(name = "is_auto", nullable = false)
	private boolean isAuto;

	@Column(name = "rating", nullable = false, columnDefinition = "double default 0.0")
	private double rating;

	@Column(name = "review_count", nullable = false, columnDefinition = "int default 0")
	private int reviewCount;

	@Column(name = "is_deleted")
	private boolean isDeleted = Boolean.FALSE;

	@Builder
	public Expert(Member member, String storeName, String location, int maxTravelDistance, String description,
		double rating, int reviewCount) {
		this.member = member;
		this.storeName = storeName;
		this.location = location;
		this.maxTravelDistance = checkMaxTravelDistance(maxTravelDistance);
		this.description = description;
		this.isAuto = false;
		this.rating = rating;
		this.reviewCount = reviewCount;
	}

	public void updateIsAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public void update(Expert updatedExpert) {
		this.storeName = updatedExpert.getStoreName();
		this.location = updatedExpert.getLocation();
		this.maxTravelDistance = checkMaxTravelDistance(updatedExpert.getMaxTravelDistance());
		this.description = updatedExpert.getDescription();
		this.rating = updatedExpert.getRating();
		this.reviewCount = updatedExpert.getReviewCount();
	}

	public void addRating(double rating) {
		double updated = (this.rating * reviewCount + rating) / (reviewCount + 1);
		reviewCount++;
		this.rating = adjustRating(updated);
	}

	public void updateRating(double rating) {
		double updated = (this.rating * reviewCount - this.rating + rating) / reviewCount;

		this.rating = adjustRating(updated);
	}

	public void deleteReview(double rating) {
		double updated = Math.max(this.rating * reviewCount - this.rating - rating, 0) / (reviewCount - 1);
		reviewCount--;
		this.rating = adjustRating(updated);
	}

	private double adjustRating(double rating) {
		return Math.round(rating * 10) / 10.0;
	}

	public void addExpertItem(ExpertItem expertItem) {
		expertItem.addExpert(this);
		this.getExpertItemList().add(expertItem);
	}

	public void removeExpertItem(ExpertItem expertItem) {
		this.getExpertItemList().remove(expertItem);
	}

	private int checkMaxTravelDistance(int maxTravelDistance) {
		if (maxTravelDistance < 0) {
			throw new InvalidValueException(ErrorCode.INVALID_MAX_TRAVEL_DISTANCE);
		}

		return maxTravelDistance;
	}
}
