package com.binhphuc.product_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@Builder
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_product_sku", columnList = "sku", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductVariant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "product_id", length = 36)
    private String productId;

    private String sku;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> attributes;

    @Column(precision = 19, scale = 2)
    private BigDecimal price;
}
