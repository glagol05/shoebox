package com.example.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static String currentUserId() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No JWT authentication found");
        }

        return jwt.getSubject();
    }

    public static String currentUsername() {
        Jwt jwt = currentJwt();
        return jwt.getClaimAsString("preferred_username");
    }

    public static boolean hasRole(String role) {
        Jwt jwt = currentJwt();
        return jwt.getClaimAsStringList("realm_access.roles")
                .contains(role);
    }

    private static Jwt currentJwt() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No JWT authentication found");
        }

        return jwt;
    }
}