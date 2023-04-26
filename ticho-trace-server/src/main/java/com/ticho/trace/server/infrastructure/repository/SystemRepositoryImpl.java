package com.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.conditions.LambdaEsUpdateWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.es.service.impl.BaseEsServiceImpl;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.infrastructure.mapper.SystemMapper;
import com.ticho.trace.server.interfaces.query.SystemQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * 系统信息 repository实现
 *
 * @author zhajianjun
 * @date 2023-04-26 13:39
 */
@Repository
@Slf4j
public class SystemRepositoryImpl extends BaseEsServiceImpl<SystemMapper, SystemBO> implements SystemRepository {


    @Override
    public boolean updateStatusById(String id, Integer status) {
        LambdaEsUpdateWrapper<SystemBO> wrapper = EsWrappers.lambdaUpdate(null);
        wrapper.eq(SystemBO::getId, id);
        wrapper.set(SystemBO::getStatus, status);
        return baseEsMapper.update(null, wrapper) > 0;
    }

    @Override
    public SystemBO getBySystemId(String systemId) {
        if (StrUtil.isBlank(systemId)) {
            return null;
        }
        LambdaEsQueryWrapper<SystemBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.eq(SystemBO::getSystemId, systemId);
        return baseEsMapper.selectOne(wrapper);
    }

    @Override
    public SystemBO getBySecret(String secret) {
        if (StrUtil.isBlank(secret)) {
            return null;
        }
        LambdaEsQueryWrapper<SystemBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.eq(SystemBO::getSecret, secret);
        return baseEsMapper.selectOne(wrapper);
    }

    @Override
    public EsPageInfo<SystemBO> page(SystemQuery query, String... indexNames) {
        LambdaEsQueryWrapper<SystemBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.eq(Objects.nonNull(query.getId()), SystemBO::getId, query.getId());
        wrapper.eq(StrUtil.isNotBlank(query.getSystemId()), SystemBO::getSystemId, query.getSystemId());
        wrapper.like(StrUtil.isNotBlank(query.getSystemName()), SystemBO::getSystemName, query.getSystemName());
        wrapper.eq(StrUtil.isNotBlank(query.getSecret()), SystemBO::getSecret, query.getSecret());
        wrapper.eq(Objects.nonNull(query.getStatus()), SystemBO::getStatus, query.getStatus());
        wrapper.like(StrUtil.isNotBlank(query.getRemark()), SystemBO::getRemark, query.getRemark());
        wrapper.index(indexNames);
        return baseEsMapper.pageQuery(wrapper, query.getPageNum(), query.getPageSize());
    }

}
