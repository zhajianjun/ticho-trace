package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.OrderByParam;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.server.application.service.TraceService;
import com.ticho.trace.server.infrastructure.entity.Trace;
import com.ticho.trace.server.infrastructure.mapper.TraceMapper;
import com.ticho.trace.server.interfaces.assembler.TraceAssembler;
import com.ticho.trace.server.interfaces.dto.TraceDTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 链路收集接口实现
 *
 * @author zhajianjun
 * @date 2023-04-02 16:00:16
 */
@Service
@Slf4j
public class TraceServiceImpl implements TraceService {

    @Autowired
    private TraceMapper traceMapper;

    @Override
    public void collect(TraceDTO traceDto) {
        if (traceDto.getStart() == null) {
            log.warn("链路格式异常，start开始时间戳不存在");
            return;
        }
        Trace trace = TraceAssembler.INSTANCE.dtoToTrace(traceDto);
        String startTime = trace.getStartTime();
        String indexName = LogConst.TRACE_INDEX_PREFIX + "_" + startTime.substring(0, 10);
        String id = IdUtil.getSnowflakeNextIdStr();
        long now = SystemClock.now();
        trace.setId(id);
        trace.setCrtTime(now);
        trace.setCreateTime(TraceAssembler.getTime(now));
        traceMapper.insert(trace, indexName);
    }


    public List<TraceDTO> getByTraceId(String traceId) {
        // @formatter:off
        if (StrUtil.isBlank(traceId)) {
            return Collections.emptyList();
        }
        LambdaEsQueryWrapper<Trace> wrapper = EsWrappers.lambdaQuery(Trace.class);
        wrapper.index(LogConst.TRACE_INDEX_PREFIX + "*");
        wrapper.eq(Trace::getTraceId, traceId);
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setSort(SortOrder.DESC.name());
        orderByParam.setOrder(LogConst.SPAN_ID_KEY + ".keyword");
        return traceMapper.selectList(wrapper)
            .stream()
            .map(TraceAssembler.INSTANCE::traceToDto)
            .collect(Collectors.toList());
        // @formatter:on
    }

}
