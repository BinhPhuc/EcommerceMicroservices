package com.binhphuc.product_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "collections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Collection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String name;

    @Column(name = "seller_id", length = 36)
    private String sellerId;
}
