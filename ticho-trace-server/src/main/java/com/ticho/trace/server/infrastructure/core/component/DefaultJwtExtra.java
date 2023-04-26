package com.ticho.trace.server.infrastructure.core.component;

import com.ticho.boot.security.handle.jwt.JwtExtra;
import com.ticho.trace.server.interfaces.dto.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * jwt额外参数配置
 *
 * @author zhajianjun
 * @date 2023-04-26 14:32
 */
@Component
public class DefaultJwtExtra implements JwtExtra {

    @Override
    public Map<String, Object> getExtra() {
        Map<String, Object> extMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) principal;
            extMap.put("status", securityUser.getStatus());
        }
        return extMap;
    }

}
