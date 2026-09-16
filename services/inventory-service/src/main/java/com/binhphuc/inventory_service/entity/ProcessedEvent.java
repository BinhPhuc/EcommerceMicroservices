package com.binhphuc.inventory_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "processed_events", indexes = {
        @Index(name = "idx_processed_events_idempotency_key", columnList = "idempotency_key",
                unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProcessedEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "idempotency_key", length = 36, nullable = false)
    private String idempotencyKey;
}
