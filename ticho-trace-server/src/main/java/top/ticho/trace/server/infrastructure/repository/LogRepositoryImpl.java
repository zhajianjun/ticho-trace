package top.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import cn.easyes.core.core.EsWrappers;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import top.ticho.boot.es.service.impl.BaseEsServiceImpl;
import top.ticho.trace.server.domain.repository.LogRepository;
import top.ticho.trace.server.infrastructure.entity.LogBO;
import top.ticho.trace.server.infrastructure.mapper.LogMapper;
import top.ticho.trace.server.interfaces.query.LogQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
        wrapper.eq(StrUtil.isNotBlank(logQuery.getTraceId()), LogBO::getTraceId, logQuery.getTraceId());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getSpanId()), LogBO::getSpanId, logQuery.getSpanId());
        wrapper.like(StrUtil.isNotBlank(logQuery.getAppName()), LogBO::getAppName, logQuery.getAppName());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getEnv()), LogBO::getEnv, logQuery.getEnv());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getIp()), LogBO::getIp, logQuery.getIp());
        wrapper.like(StrUtil.isNotBlank(logQuery.getPreAppName()), LogBO::getPreAppName, logQuery.getPreAppName());
        wrapper.eq(StrUtil.isNotBlank(logQuery.getPreIp()), LogBO::getPreIp, logQuery.getPreIp());
        String logLevel = logQuery.getLogLevel();
        if (StrUtil.isNotBlank(logLevel)) {
            wrapper.eq(StrUtil.isNotBlank(logLevel), LogBO::getLogLevel, logLevel.toUpperCase());
        }
        wrapper.like(StrUtil.isNotBlank(logQuery.getClassName()), LogBO::getClassName, logQuery.getClassName());
        wrapper.like(StrUtil.isNotBlank(logQuery.getMethod()), LogBO::getMethod, logQuery.getMethod());
        wrapper.matchPhrase(StrUtil.isNotBlank(logQuery.getContent()), LogBO::getContent, logQuery.getContent());
        wrapper.ge(LogBO::getDtTime, LocalDateTimeUtil.toEpochMilli(logQuery.getStartDateTime()));
        wrapper.le(LogBO::getDtTime, LocalDateTimeUtil.toEpochMilli(logQuery.getEndDateTime()));
        boolean isAsc = Boolean.TRUE.equals(logQuery.getIsAsc());
        wrapper.orderBy(true, isAsc, LogBO::getDtTime);
        return baseEsMapper.pageQuery(wrapper, logQuery.getPageNum(), logQuery.getPageSize());
    }

}
