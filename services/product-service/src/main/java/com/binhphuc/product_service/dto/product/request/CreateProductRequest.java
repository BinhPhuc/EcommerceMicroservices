package com.binhphuc.product_service.dto.product.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {
  @NotEmpty private String name;
  @NotNull private int price;
  @NotNull private int stock;
  @NotEmpty private String categoryId;
}
