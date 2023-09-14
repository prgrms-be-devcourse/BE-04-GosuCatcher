package com.foo.gosucatcher.domain.item.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "main_items")
@SQLDelete(sql = "UPDATE main_items SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MainItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "mainItem", cascade = CascadeType.ALL)
	private List<SubItem> subItems = new ArrayList<>();

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	@Lob
	private String description;

	private boolean isDeleted = Boolean.FALSE;

	@Builder
	public MainItem(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public void update(MainItem mainItem) {
		this.name = mainItem.getName();
		this.description = mainItem.getDescription();
	}

	public void addSubItem(SubItem subItem) {
		this.getSubItems().add(subItem);
	}

	public void removeSubItem(SubItem subItem) {
		this.getSubItems().remove(subItem);
	}
}
