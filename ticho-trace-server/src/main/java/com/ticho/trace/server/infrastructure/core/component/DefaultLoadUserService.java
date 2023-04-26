package com.ticho.trace.server.infrastructure.core.component;

import com.ticho.boot.security.constant.BaseSecurityConst;
import com.ticho.boot.security.handle.load.LoadUserService;
import com.ticho.boot.view.core.BaseSecurityUser;
import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.util.Assert;
import com.ticho.trace.server.domain.repository.UserRepository;
import com.ticho.trace.server.infrastructure.core.enums.UserStatus;
import com.ticho.trace.server.infrastructure.entity.UserBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

/**
 * 权限用户查询服务
 *
 * @author zhajianjun
 * @date 2023-04-26 14:32
 */
@Component(BaseSecurityConst.LOAD_USER_TYPE_USERNAME)
public class DefaultLoadUserService implements LoadUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public BaseSecurityUser load(String username) {
        // 用户信息校验
        UserBO user = userRepository.getByUsername(username);
        Assert.isNotNull(user, HttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        // 状态校验
        Integer status = user.getStatus();
        String message = UserStatus.getByCode(status);
        boolean normal = Objects.equals(status, UserStatus.NORMAL.code());
        Assert.isTrue(normal, HttpErrCode.NOT_LOGIN, String.format("用户%s", message));
        BaseSecurityUser securityUser = new BaseSecurityUser();
        securityUser.setUsername(username);
        securityUser.setPassword(user.getPassword());
        securityUser.setRoles(Collections.singletonList(""));
        return securityUser;
    }

}
