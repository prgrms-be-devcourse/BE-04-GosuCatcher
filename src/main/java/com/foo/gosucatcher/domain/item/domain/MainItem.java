package com.foo.gosucatcher.domain.item.domain;

import com.foo.gosucatcher.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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

    @Column(nullable = false, unique = true, length = 100)
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
}
