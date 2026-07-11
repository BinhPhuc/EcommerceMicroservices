package com.binhphuc.product_service.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {
  private Boolean isDeleted;
  private Instant createdDate;
  private String createdBy;
  private Instant lastModifiedDate;
  private String lastModifiedBy;
}
