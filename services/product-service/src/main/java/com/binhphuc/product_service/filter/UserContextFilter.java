package com.binhphuc.product_service.filter;

import com.binhphuc.product_service.context.UserContext;
import com.binhphuc.product_service.context.holder.UserContextHolder;
import com.binhphuc.product_service.enums.TrustedHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(TrustedHeader.X_USER_ID.getHeaderName());
        String username = request.getHeader(TrustedHeader.X_USER_NAME.getHeaderName());
        if (!requireHeader(userId) || !requireHeader(username)) {
            throw new ServletException("Missing required headers: " + TrustedHeader.X_USER_ID.getHeaderName() + ", "
                    + TrustedHeader.X_USER_NAME.getHeaderName());
        }
        try {
            UserContext userContext = UserContext.builder().userId(userId).username(username).build();
            UserContextHolder.setUserContext(userContext);
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    boolean requireHeader(String header) {
        return StringUtils.hasText(header);
    }
}
