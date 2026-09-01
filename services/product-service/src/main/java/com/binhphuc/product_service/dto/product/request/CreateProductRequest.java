package com.binhphuc.product_service.dto.product.request;

import com.binhphuc.product_service.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @JsonProperty("category_id")
    @NotEmpty
    private String categoryId;

    @NotNull
    private ProductStatus status;

    private List<CreateProductImageRequest> images;

    private CreateProductVariantRequest variant;

    private Integer stock;
}
