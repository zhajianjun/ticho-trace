package com.ticho.trace.server.infrastructure.core.component;

import cn.hutool.core.convert.Convert;
import com.ticho.boot.security.constant.BaseOAuth2Const;
import com.ticho.boot.security.constant.BaseSecurityConst;
import com.ticho.boot.security.filter.AbstractAuthTokenFilter;
import com.ticho.trace.server.interfaces.dto.SecurityUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * jwt token验证过滤器
 *
 * @author zhajianjun
 * @date 2023-04-26 14:32
 */
@Component(BaseOAuth2Const.OAUTH2_TOKEN_FILTER_BEAN_NAME)
public class DefaultAuthenticationTokenFilter extends AbstractAuthTokenFilter<SecurityUser> {

    @Override
    public SecurityUser convert(Map<String, Object> decodeAndVerify) {
        // @formatter:off
        String username = Optional.ofNullable(decodeAndVerify.get(BaseSecurityConst.USERNAME)).map(Object::toString).orElse(null);
        List<String> authorities = Optional.ofNullable(decodeAndVerify.get(BaseSecurityConst.AUTHORITIES)).map(x-> Convert.toList(String.class, x)).orElse(null);
        Integer status = Optional.ofNullable(decodeAndVerify.get("status")).map(Convert::toInt).orElse(null);
        SecurityUser user = new SecurityUser();
        user.setUsername(username);
        user.setRoles(authorities);
        user.setStatus(status);
        return user;
        // @formatter:on
    }

}
