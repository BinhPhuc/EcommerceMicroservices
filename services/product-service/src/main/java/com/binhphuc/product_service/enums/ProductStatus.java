package com.binhphuc.product_service.enums;

public enum ProductStatus {
    DRAFT("DRAFT"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    OUT_OF_STOCK("OUT_OF_STOCK"),
    ARCHIVED("ARCHIVED");

    private String status;

    ProductStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
