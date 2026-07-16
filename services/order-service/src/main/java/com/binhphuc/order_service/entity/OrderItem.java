package com.binhphuc.order_service.entity;

import com.binhphuc.common_jpa_starter.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(length = 36)
  private String id;

  @Column(name = "order_id", length = 36, nullable = false)
  private String orderID;

  @Column(name = "product_id", length = 36, nullable = false)
  private String productID;

  private Integer price;

  private Integer quantity;
}
