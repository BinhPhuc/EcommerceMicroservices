package com.binhphuc.product_service.dto.category.request;

import com.esotericsoftware.kryo.serializers.FieldSerializer.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {
    @NotEmpty
    private String name;

    @JsonProperty("parent_id")
    @NotNull
    private String parentId;
}
