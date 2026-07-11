package com.binhphuc.product_service.entity;

import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public class BaseEntity {
  private Boolean isDeleted;
  private Instant createdDate;
  private String createdBy;
  private Instant lastModifiedDate;
  private String lastModifiedBy;
}
