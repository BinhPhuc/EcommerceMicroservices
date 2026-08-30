package com.binhphuc.api_gateway.enums;

public enum TrustedHeader {
    X_USER_ID("X-User-Id"),
    X_USER_NAME("X-User-Name");

    private final String headerName;

    TrustedHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}
