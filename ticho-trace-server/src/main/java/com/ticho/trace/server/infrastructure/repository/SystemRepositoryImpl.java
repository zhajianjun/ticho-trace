package com.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import cn.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import cn.easyes.core.core.EsWrappers;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ticho.boot.es.service.impl.BaseEsServiceImpl;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.infrastructure.mapper.SystemMapper;
import com.ticho.trace.server.interfaces.query.SystemQuery;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统信息 repository实现
 *
 * @author zhajianjun
 * @date 2023-04-26 13:39
 */
@Repository
@Slf4j
public class SystemRepositoryImpl extends BaseEsServiceImpl<SystemMapper, SystemBO> implements SystemRepository {

    private Cache<String, SystemBO> systemCache = null;

    @Qualifier("asyncTaskExecutor")
    @Autowired
    private Executor executor;

    @PostConstruct
    public void init() {
        // @formatter:off
        systemCache = Caffeine.newBuilder()
            //初始容量
            .initialCapacity(16)
            // 最大长度
            .maximumSize(50)
            // 打开统计功能
            .recordStats()
            // 设置自定义过期
            // 设置固定过期 在最后一次访问或者写入后开始计时，在指定的时间后过期。假如一直有请求访问该key，那么这个缓存将一直不会过期。
            .expireAfterAccess(Duration.ofMinutes(30))
            // 设置固定过期 失效后同步加载缓存，阻塞机制获取缓存
            //.expireAfterWrite(Duration.ofMinutes(30))
            // 设置固定过期 失效后异步加载，其它线程任然获取旧值
            //.refreshAfterWrite(Duration.ofMinutes(30))
            // 缓存写入删除回调 同步
            //.writer(new DefaultCacheWriter())
            // 缓存移除监听 异步
            .removalListener((key, value, removalCause) -> log.info("缓存移除监听, 移除的key = {}, value = {}, cause = {}", key, value, removalCause))
            // 异步线程池
            .executor(executor)
            .build(new CacheLoader<String, SystemBO>() {
                @Override
                public @Nullable SystemBO load(@NonNull String secret) {
                    return getBySecret(secret);
                }
            });
        // @formatter:on
    }

    @Override
    public boolean updateStatus(String systemId, Integer status) {
        LambdaEsUpdateWrapper<SystemBO> wrapper = EsWrappers.lambdaUpdate(null);
        wrapper.eq(SystemBO::getSystemId, systemId);
        wrapper.set(SystemBO::getStatus, status);
        return baseEsMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateSecret(String systemId, String secret) {
        LambdaEsUpdateWrapper<SystemBO> wrapper = EsWrappers.lambdaUpdate(null);
        wrapper.eq(SystemBO::getSystemId, systemId);
        wrapper.set(SystemBO::getSecret, secret);
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

    public SystemBO getCacheBySecret(String secret) {
        return systemCache.get(secret, this::getBySecret);
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

    @Override
    public List<SystemBO> listBySystemIds(Collection<String> systemIds) {
        if (CollUtil.isEmpty(systemIds)) {
            return Collections.emptyList();
        }
        LambdaEsQueryWrapper<SystemBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.in(SystemBO::getSystemId, systemIds);
        return baseEsMapper.selectList(wrapper);
    }

    @Override
    public Map<String, SystemBO> getMapBySystemIds(Collection<String> systemIds) {
        // @formatter:off
        return listBySystemIds(systemIds)
            .stream()
            .collect(Collectors.toMap(SystemBO::getSystemId, Function.identity()));
        // @formatter:on
    }

    @Override
    public List<SystemBO> listAll() {
        LambdaEsQueryWrapper<SystemBO> wrapper = EsWrappers.lambdaQuery(null);
        return baseEsMapper.selectList(wrapper);
    }

}
