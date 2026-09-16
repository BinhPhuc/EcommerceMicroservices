package com.binhphuc.order_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "order_item_snapshots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItemSnapshot extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "order_item_id", length = 36, unique = true)
    private String orderItemId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "variant_attributes", columnDefinition = "JSON")
    private String variantAttributes;

    @Column(precision = 19, scale = 2)
    private BigDecimal price;
}
