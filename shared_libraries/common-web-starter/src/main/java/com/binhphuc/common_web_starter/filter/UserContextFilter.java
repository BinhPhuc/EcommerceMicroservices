package com.binhphuc.common_web_starter.filter;

import java.io.IOException;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.binhphuc.common_core.context.UserContext;
import com.binhphuc.common_core.context.holder.UserContextHolder;
import com.binhphuc.common_core.enums.TrustedHeader;
import com.binhphuc.common_web_starter.config.UserContextProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserContextFilter extends OncePerRequestFilter {
    private final UserContextProperties userContextProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return userContextProperties.getExcludePatterns().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(TrustedHeader.X_USER_ID.getHeaderName());
        String username = request.getHeader(TrustedHeader.X_USER_NAME.getHeaderName());
        if (!requireHeader(userId) || !requireHeader(username)) {
            if (userContextProperties.isRequired()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing required headers: " + TrustedHeader.X_USER_ID.getHeaderName() + ", "
                                + TrustedHeader.X_USER_NAME.getHeaderName());
                return;
            }
            filterChain.doFilter(request, response);
            return;
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
