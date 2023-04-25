package com.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.es.service.impl.BaseEsServiceImpl;
import com.ticho.trace.server.domain.repository.LogRepository;
import com.ticho.trace.server.infrastructure.entity.LogBO;
import com.ticho.trace.server.infrastructure.mapper.LogMapper;
import com.ticho.trace.server.interfaces.query.LogQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 日志收集信息 repository实现
 *
 * @author zhajianjun
 * @date 2023-04-25 22:41:58
 */
@Repository
@Slf4j
public class LogRepositoryImpl extends BaseEsServiceImpl<LogMapper, LogBO> implements LogRepository {

    @Override
    public EsPageInfo<LogBO> page(LogQuery logQuery, String... indexNames) {
        LambdaEsQueryWrapper<LogBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.index(indexNames);
        LocalDateTime startDateTime = logQuery.getStartDateTime();
        LocalDateTime endDateTime = logQuery.getEndDateTime();
        long start = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long end = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        wrapper.eq(StrUtil.isNotBlank(logQuery.getTraceId()), LogBO::getTraceId, logQuery.getTraceId());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getSpanId()), LogBO::getSpanId, logQuery.getSpanId());
        wrapper.like(StrUtil.isNotBlank(logQuery.getAppName()), LogBO::getAppName, logQuery.getAppName());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getEnv()), LogBO::getEnv, logQuery.getEnv());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getIp()), LogBO::getIp, logQuery.getIp());
        wrapper.like(StrUtil.isNotBlank(logQuery.getPreAppName()), LogBO::getPreAppName, logQuery.getPreAppName());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getPreIp()), LogBO::getPreIp, logQuery.getPreIp());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getLogLevel()), LogBO::getLogLevel, logQuery.getLogLevel());
        wrapper.like(StrUtil.isNotBlank(logQuery.getClassName()), LogBO::getClassName, logQuery.getClassName());
        wrapper.like(StrUtil.isNotBlank(logQuery.getMethod()), LogBO::getMethod, logQuery.getMethod());
        wrapper.like(StrUtil.isNotBlank(logQuery.getContent()), LogBO::getContent, logQuery.getContent());
        wrapper.ge(LogBO::getDtTime, start);
        wrapper.le(LogBO::getDtTime, end);
        return baseEsMapper.pageQuery(wrapper, logQuery.getPageNum(), logQuery.getPageSize());
    }

}
