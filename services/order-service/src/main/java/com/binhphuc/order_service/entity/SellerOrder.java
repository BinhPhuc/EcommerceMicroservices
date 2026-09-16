package com.binhphuc.order_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import com.binhphuc.order_service.enums.SellerOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

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
@Table(name = "seller_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Deprecated(forRemoval = true, since = "This entity is deprecated and will be removed in future " +
        "versions. Please use the new order management system.")
public class SellerOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "order_id", length = 36)
    private String orderId;

    @Column(name = "seller_id", length = 36)
    private String sellerId;

    @Enumerated(EnumType.STRING)
    private SellerOrderStatus status;

    @Column(name = "shipping_fee", precision = 19, scale = 2)
    private BigDecimal shippingFee;
}
