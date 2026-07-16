package com.binhphuc.order_service.client.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
  private String id;
  private String name;
  private Integer price;
  private Integer stock;
  @JsonProperty("category_id")
  private String categoryID;

}
