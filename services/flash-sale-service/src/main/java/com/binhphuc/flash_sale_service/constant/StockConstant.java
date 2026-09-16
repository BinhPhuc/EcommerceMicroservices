package com.binhphuc.flash_sale_service.constant;

public final class StockConstant {
    private StockConstant() {
    }

    public static final String STOCK_CACHE_KEY_PREFIX = "stock::";
    public static final long RESERVE_STOCK_SUCCESS = 0L;
    public static final long RESERVE_STOCK_KEY_NOT_FOUND = -1L;
    public static final long RESERVE_STOCK_NOT_ENOUGH = -2L;
    public static final long RESERVE_STOCK_INVALID_VALUE = -3L;
    public static final long RESERVE_STOCK_DUPLICATED_REQUEST = -4L;
    public static final long RELEASE_STOCK_KEY_NOT_FOUND = -1L;
    public static final long RELEASE_STOCK_INVALID_VALUE = -2L;
}
