package com.binhphuc.user_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import com.binhphuc.user_service.enums.SellerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "sellers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Seller extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "business_address")
    private String businessAddress;

    @Enumerated(EnumType.STRING)
    private SellerStatus status;
}
