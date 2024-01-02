package top.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.biz.OrderByParam;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import cn.easyes.core.core.EsWrappers;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import top.ticho.boot.es.service.impl.BaseEsServiceImpl;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.server.domain.repository.TraceRepository;
import top.ticho.trace.server.infrastructure.entity.TraceBO;
import top.ticho.trace.server.infrastructure.mapper.TraceMapper;
import top.ticho.trace.server.interfaces.query.TraceQuery;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 链路信息 repository实现
 *
 * @author zhajianjun
 * @date 2023-04-26 13:50
 */
@Repository
public class TraceRepositoryImpl extends BaseEsServiceImpl<TraceMapper, TraceBO> implements TraceRepository {

    @Override
    public EsPageInfo<TraceBO> page(TraceQuery query, String... indexNames) {
        LambdaEsQueryWrapper<TraceBO> wrapper = EsWrappers.lambdaQuery(null);
        wrapper.eq(Objects.nonNull(query.getId()), TraceBO::getId, query.getId());
        wrapper.eq(StrUtil.isNotBlank(query.getSystemId()), TraceBO::getSystemId, query.getSystemId());
        wrapper.eq(StrUtil.isNotBlank(query.getTraceId()), TraceBO::getTraceId, query.getTraceId());
        wrapper.eq(StrUtil.isNotBlank(query.getSpanId()), TraceBO::getSpanId, query.getSpanId());
        wrapper.eq(StrUtil.isNotBlank(query.getAppName()), TraceBO::getAppName, query.getAppName());
        wrapper.eq(StrUtil.isNotBlank(query.getEnv()), TraceBO::getEnv, query.getEnv());
        wrapper.eq(StrUtil.isNotBlank(query.getIp()), TraceBO::getIp, query.getIp());
        wrapper.eq(StrUtil.isNotBlank(query.getPreAppName()), TraceBO::getPreAppName, query.getPreAppName());
        wrapper.eq(StrUtil.isNotBlank(query.getPreIp()), TraceBO::getPreIp, query.getPreIp());
        wrapper.eq(StrUtil.isNotBlank(query.getType()), TraceBO::getType, query.getType());
        wrapper.eq(StrUtil.isNotBlank(query.getUrl()), TraceBO::getUrl, query.getUrl());
        wrapper.eq(StrUtil.isNotBlank(query.getPort()), TraceBO::getPort, query.getPort());
        wrapper.eq(StrUtil.isNotBlank(query.getFullUrl()), TraceBO::getFullUrl, query.getFullUrl());
        wrapper.eq(StrUtil.isNotBlank(query.getMethod()), TraceBO::getMethod, query.getMethod());
        wrapper.eq(Objects.nonNull(query.getStatus()), TraceBO::getStatus, query.getStatus());
        wrapper.ge(Objects.nonNull(query.getStartTimeFirst()), TraceBO::getStart, toEpochMilli(query.getStartTimeFirst()));
        wrapper.le(Objects.nonNull(query.getStartTimeLast()), TraceBO::getStart, toEpochMilli(query.getStartTimeLast()));
        wrapper.ge(Objects.nonNull(query.getEndTimeFirst()), TraceBO::getEnd, toEpochMilli(query.getEndTimeFirst()));
        wrapper.le(Objects.nonNull(query.getEndTimeLast()), TraceBO::getEnd, toEpochMilli(query.getEndTimeLast()));
        wrapper.ge(Objects.nonNull(query.getConsumeFirst()), TraceBO::getConsume, query.getConsumeFirst());
        wrapper.le(Objects.nonNull(query.getConsumeLast()), TraceBO::getConsume, query.getConsumeLast());
        wrapper.index(indexNames);
        return baseEsMapper.pageQuery(wrapper, query.getPageNum(), query.getPageSize());
    }

    public Long toEpochMilli(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return LocalDateTimeUtil.toEpochMilli(localDateTime);
    }


    @Override
    public List<TraceBO> selectByTraceId(String traceId, String... indexNames) {
        LambdaEsQueryWrapper<TraceBO> wrapper = EsWrappers.lambdaQuery(TraceBO.class);
        wrapper.index(indexNames);
        wrapper.eq(TraceBO::getTraceId, traceId);
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setSort(SortOrder.DESC.name());
        orderByParam.setOrder(LogConst.SPAN_ID_KEY + ".keyword");
        return baseEsMapper.selectList(wrapper);
    }

}
