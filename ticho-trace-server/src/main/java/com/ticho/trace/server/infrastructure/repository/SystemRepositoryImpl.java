package com.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.es.service.impl.BaseEsServiceImpl;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.infrastructure.mapper.SystemMapper;
import com.ticho.trace.server.interfaces.query.SystemQuery;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * 系统信息 repository实现
 *
 * @author zhajianjun
 * @date 2023-04-26 13:39
 */
@Repository
public class SystemRepositoryImpl extends BaseEsServiceImpl<SystemMapper, SystemBO> implements SystemRepository {

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
