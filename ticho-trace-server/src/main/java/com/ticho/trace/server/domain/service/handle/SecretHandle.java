package com.ticho.trace.server.domain.service.handle;

import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.util.Assert;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.infrastructure.core.constant.CommConst;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-04-26 21:05:10
 */
@Slf4j
public abstract class SecretHandle {

    @Autowired
    private SystemRepository systemRepository;

    @Resource
    private HttpServletRequest request;


    /**
     * 查询系统id和检查
     *
     * @return {@link String}
     */
    public String getSystemIdAndCheck() {
        String secret = request.getHeader(CommConst.SECRET_KEY);
        return getSystemIdAndCheck(secret);
    }

    public String getSystemIdAndCheck(String secret) {
        Assert.isNotBlank(secret, BizErrCode.FAIL, "秘钥不能为空");
        SystemBO cacheBySecret = systemRepository.getCacheBySecret(secret);
        Assert.isNotNull(cacheBySecret, BizErrCode.FAIL, "系统信息不存在");
        return cacheBySecret.getSystemId();
    }



}
