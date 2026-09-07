package com.binhphuc.flash_sale_service.helper;

import java.util.List;
import java.util.stream.Collectors;

public final class CacheHelper {
    private CacheHelper() {
    }

    public static String createCacheKey(String primaryKey, List<String> unsortedKeys) {
        // primaryKey:sortedKey1,sortedKey2,sortedKey3
        String sortedKeys = unsortedKeys.stream().sorted().collect(Collectors.joining(","));
        StringBuilder cacheKeyBuilder =
                new StringBuilder(primaryKey).append(":").append(sortedKeys);
        return cacheKeyBuilder.toString();
    }
}
