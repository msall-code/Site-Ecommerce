package com.maket.maket_backend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// On cible uniquement la règle Sonar de nullabilité pour éviter tout conflit avec le compilateur
@SuppressWarnings("java:S2638")
public class KeycloakSecurityConfig implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @Nullable
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof List<?>)) {
            return Collections.emptyList();
        }

        List<?> roles = (List<?>) rolesObj;

        return roles.stream()
                .map(Object::toString)
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}