package com.foo.gosucatcher.domain.chat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.global.BaseEntity;

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

	@OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Message> messageList = new ArrayList<>();

	private boolean isDeleted = Boolean.FALSE;

	@Builder
	public ChattingRoom(MemberEstimate memberEstimate) {
		this.memberEstimate = memberEstimate;
	}

	public void addMessage(Message message) {
		messageList.add(message);
	}

	public void removeMessage(Message message) {
		messageList.remove(message);
	}
}
