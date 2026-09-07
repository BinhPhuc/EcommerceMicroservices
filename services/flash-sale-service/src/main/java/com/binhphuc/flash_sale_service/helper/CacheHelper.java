package com.binhphuc.flash_sale_service.helper;

import java.util.List;

public final class CacheHelper {
    private CacheHelper() {
    }

    public static String createCacheKey(String value, List<String> keys) {
        // value:sortedKey1:sortedKey2:sortedKey3
        List<String> sortedKeys = keys.stream().sorted().toList();
        StringBuilder cacheKeyBuilder = new StringBuilder(value);
        for (int i = 0; i < sortedKeys.size(); i++) {
            cacheKeyBuilder.append(":").append(sortedKeys.get(i));
        }
        return cacheKeyBuilder.toString();
    }
}
