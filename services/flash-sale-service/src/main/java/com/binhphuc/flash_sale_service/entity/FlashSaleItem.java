package com.binhphuc.flash_sale_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "flash_sale_items", indexes = {
        @Index(name = "idx_flash_sale_item_variant", columnList = "flash_sale_id, variant_id", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FlashSaleItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "flash_sale_id", length = 36)
    private String flashSaleId;

    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "variant_id", length = 36)
    private String variantId;

    @Column(name = "flash_price", precision = 19, scale = 2)
    private BigDecimal flashPrice;

    @Column(name = "stock", nullable = false)
    private Long stock;

    @Column(name = "sold_quantity", nullable = false)
    private Long soldQuantity;

    @Column(name = "purchase_limit", nullable = false)
    private Integer purchaseLimit;
}
