package com.binhphuc.product_service.dto.product.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
  @NotEmpty private String name;
  @NotNull @Positive private int price;
  @NotNull @Positive private int stock;
  @JsonProperty("category_id") @NotEmpty private String categoryId;
}
