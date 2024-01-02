package top.ticho.trace.server.domain.handle;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.ticho.boot.security.util.BaseUserUtil;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.util.Assert;
import top.ticho.trace.server.domain.repository.SystemRepository;
import top.ticho.trace.server.domain.repository.UserRepository;
import top.ticho.trace.server.infrastructure.core.constant.CommConst;
import top.ticho.trace.server.infrastructure.entity.SystemBO;
import top.ticho.trace.server.infrastructure.entity.UserBO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 公共处理
 *
 * @author zhajianjun
 * @date 2023-04-26 21:05:10
 */
@Slf4j
public abstract class CommonHandle {

    @Autowired
    private SystemRepository systemRepository;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserRepository userRepository;


    /**
     * 根据秘钥查询系统id
     *
     * @return {@link String}
     */
    public String getSystemIdAndCheck() {
        String secret = request.getHeader(CommConst.SECRET_KEY);
        return getSystemIdAndCheck(secret);
    }

    /**
     * 根据秘钥查询系统id
     *
     * @return {@link String}
     */
    public String getSystemIdAndCheck(String secret) {
        Assert.isNotBlank(secret, BizErrCode.FAIL, "秘钥不能为空");
        SystemBO cacheBySecret = systemRepository.getCacheBySecret(secret);
        Assert.isNotNull(cacheBySecret, BizErrCode.FAIL, "系统信息不存在");
        return cacheBySecret.getSystemId();
    }

    /**
     * 检查当前用户的系统id
     *
     * @param systemId 系统标识
     */
    public void checkCurUserSystemId(String systemId) {
        Assert.isNotBlank(systemId, BizErrCode.FAIL, "系统id不能为空");
        String currentUsername = BaseUserUtil.getCurrentUsername();
        boolean isAdmin = Objects.equals(currentUsername, CommConst.ADMIN_USERNAME);
        if (isAdmin) {
            return;
        }
        UserBO userBO = userRepository.getByUsername(currentUsername);
        Assert.isNotNull(userBO, BizErrCode.FAIL, "用户信息不存在");
        List<String> systemIds = userBO.getSystemIds();
        boolean contain = Objects.nonNull(systemIds) && systemIds.contains(systemId);
        Assert.isNotNull(contain, BizErrCode.FAIL, StrUtil.format("用户未绑定系统id[{}]", systemId));
    }


}
