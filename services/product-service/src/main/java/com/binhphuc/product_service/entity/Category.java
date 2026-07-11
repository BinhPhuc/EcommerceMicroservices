package com.binhphuc.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private String id;
  private String name;
  @Column(name = "parent_id") private String parentId;
}
