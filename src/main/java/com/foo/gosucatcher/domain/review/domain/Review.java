package com.foo.gosucatcher.domain.review.domain;

import static com.foo.gosucatcher.global.error.ErrorCode.INVALID_UPDATER;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.review.exception.UnsupportedReplierException;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reviews")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE reviews SET is_deleted = true WHERE id = ?")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id")
	private Member writer;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reply_id")
	private Reply reply;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private List<ReviewImage> reviewImages;

	private String content;

	private double rating;

	private boolean isDeleted = false;

	@Builder
	public Review(Expert expert, Member writer, SubItem subItem, String content, int rating) {
		this.expert = expert;
		this.writer = writer;
		this.subItem = subItem;
		this.content = content;
		this.rating = rating;
	}

	public void update(Review updated, long updaterId) {
		content = updated.getContent();
		rating = updated.getRating();

		validateWriter(updaterId);

		expert.updateRating(rating);
	}

	public void delete(long updaterId) {
		validateWriter(updaterId);

		expert.deleteReview(rating);
	}

	public boolean replyExists() {
		return ((reply != null) && (!reply.isDeleted()));
	}

	private void validateWriter(long updaterId) {
		long writerId = writer.getId();

		if (writerId != updaterId) {
			throw new UnsupportedReplierException(INVALID_UPDATER);
		}
	}

	public void addReply(Reply reply) {
		this.reply = reply;
	}

	public void addReviewImages(List<ReviewImage> reviewImages) {
		this.reviewImages = reviewImages;

		for (ReviewImage reviewImage : reviewImages) {
			reviewImage.addReview(this);
		}
	}
}
