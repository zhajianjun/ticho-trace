package top.ticho.trace.server.infrastructure.core.component;

import cn.hutool.core.util.ArrayUtil;
import top.ticho.boot.security.auth.PermissionService;
import top.ticho.boot.security.util.BaseUserUtil;
import top.ticho.trace.server.infrastructure.core.constant.CommConst;
import top.ticho.trace.server.interfaces.dto.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 接口权限实现
 *
 * @author zhajianjun
 * @date 2022-09-26 17:31:58
 */
@Slf4j
@Component(CommConst.USER_PERM_KEY)
@Order(1)
public class UsernamePermissionServiceImpl implements PermissionService {

    public boolean hasPerms(String... permissions) {
        log.debug("权限校验，permissions = {}", String.join(",", permissions));
        if (ArrayUtil.isEmpty(permissions)) {
            return false;
        }
        String permission = permissions[0];
        SecurityUser currentUser = BaseUserUtil.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return false;
        }
        String username = currentUser.getUsername();
        return Objects.equals(username, permission);
    }

}
