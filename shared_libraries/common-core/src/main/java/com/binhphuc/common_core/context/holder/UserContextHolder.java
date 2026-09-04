package com.binhphuc.common_core.context.holder;

import com.binhphuc.common_core.context.UserContext;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();

    public static void setUserContext(UserContext userContext) {
        userContextThreadLocal.set(userContext);
    }

    public static UserContext getUserContext() {
        return userContextThreadLocal.get();
    }

    public static void clear() {
        userContextThreadLocal.remove();
    }
}
