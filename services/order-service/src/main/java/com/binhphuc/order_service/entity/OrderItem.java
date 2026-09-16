package com.binhphuc.order_service.entity;

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
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Deprecated(forRemoval = true)
    @Column(name = "seller_order_id", length = 36)
    private String sellerOrderId;

    @Column(name = "order_id", length = 36)
    private String orderId;

    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "variant_id", length = 36)
    private String variantId;

    @Column(nullable = false)
    private Integer quantity;
}
