package com.foo.gosucatcher.domain.review.domain;

import static com.foo.gosucatcher.global.error.ErrorCode.UNSUPPORTED_REPLIER;
import static java.lang.Boolean.FALSE;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.review.exception.UnsupportedReplierException;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "replies")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE replies SET is_deleted = true WHERE id = ?")
public class Reply extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(mappedBy = "reply", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private Review parent;

	private String content;

	private boolean isDeleted = FALSE;

	@Builder
	public Reply(Review parent, String content) {
		this.content = content;

		parent.addReply(this);
	}

	public void validateWriter(long writerId) {
		long expertId = parent.getExpert().getId();

		if (writerId != expertId) {
			throw new UnsupportedReplierException(UNSUPPORTED_REPLIER);
		}
	}

	public void validateWriter(long writerId, Review review) {
		Expert expert = review.getExpert();

		if (expert.getId() != writerId) {
			throw new UnsupportedReplierException(UNSUPPORTED_REPLIER);
		}

	}

	public void update(Reply updated, long updaterId) {
		validateWriter(updaterId);

		content = updated.getContent();
	}

}
