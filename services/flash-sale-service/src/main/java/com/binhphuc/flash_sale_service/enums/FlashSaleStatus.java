package com.binhphuc.flash_sale_service.enums;

public enum FlashSaleStatus {
    SCHEDULED("SCHEDULED"),
    ACTIVE("ACTIVE"),
    ENDED("ENDED"),
    CANCELLED("CANCELLED");

    private String status;

    FlashSaleStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
