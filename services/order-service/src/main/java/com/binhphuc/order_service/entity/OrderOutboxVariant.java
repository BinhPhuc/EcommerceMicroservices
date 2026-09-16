package com.binhphuc.order_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "order_outbox_variants", indexes = {
        @Index(name = "idx_order_outbox_variants_outbox_id", columnList = "outbox_id")
})

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderOutboxVariant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "outbox_id", length = 36)
    private String outboxId;

    @Column(name = "variant_id", length = 36)
    private String variantId;

    @Column(name = "quantity")
    private Integer quantity;
}
