package com.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.es.service.impl.BaseEsServiceImpl;
import com.ticho.trace.server.domain.repository.UserRepository;
import com.ticho.trace.server.infrastructure.entity.UserBO;
import com.ticho.trace.server.infrastructure.mapper.UserMapper;
import com.ticho.trace.server.interfaces.query.UserQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * 用户信息 repository实现
 *
 * @author zhajianjun
 * @date 2023-04-25 21:38:41
 */
@Repository
@Slf4j
public class UserRepositoryImpl extends BaseEsServiceImpl<UserMapper, UserBO> implements UserRepository {

    @Override
    public EsPageInfo<UserBO> page(UserQuery query) {
        LambdaEsQueryWrapper<UserBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.eq(Objects.nonNull(query.getId()), UserBO::getId, query.getId());
        wrapper.eq(StrUtil.isNotBlank(query.getUsername()), UserBO::getUsername, query.getUsername());
        wrapper.eq(StrUtil.isNotBlank(query.getRealname()), UserBO::getRealname, query.getRealname());
        wrapper.eq(StrUtil.isNotBlank(query.getEmail()), UserBO::getEmail, query.getEmail());
        wrapper.eq(StrUtil.isNotBlank(query.getMobile()), UserBO::getMobile, query.getMobile());
        wrapper.eq(StrUtil.isNotBlank(query.getPhoto()), UserBO::getPhoto, query.getPhoto());
        wrapper.eq(StrUtil.isNotBlank(query.getLastIp()), UserBO::getLastIp, query.getLastIp());
        wrapper.eq(Objects.nonNull(query.getStatus()), UserBO::getStatus, query.getStatus());
        wrapper.like(StrUtil.isNotBlank(query.getRemark()), UserBO::getRemark, query.getRemark());
        return baseEsMapper.pageQuery(wrapper, query.getPageNum(), query.getPageSize());
    }

    @Override
    public UserBO selectByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        LambdaEsQueryWrapper<UserBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.eq(UserBO::getUsername, username);
        return baseEsMapper.selectOne(wrapper);
    }

}
