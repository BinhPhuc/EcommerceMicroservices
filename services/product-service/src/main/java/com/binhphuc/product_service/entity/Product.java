package com.binhphuc.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "products")
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
  private String name;
  private int price;
  private int stock;
  private String categoryId;
}
