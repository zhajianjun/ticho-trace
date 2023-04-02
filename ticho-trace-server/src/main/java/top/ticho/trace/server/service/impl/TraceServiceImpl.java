package top.ticho.trace.server.service.impl;

import cn.easyes.core.biz.OrderByParam;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.server.entity.TraceInfo;
import top.ticho.trace.server.mapper.TraceMapper;
import top.ticho.trace.server.service.TraceService;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * 链路收集接口实现
 *
 * @author zhajianjun
 * @date 2023-04-02 16:00:16
 */
@Service
public class TraceServiceImpl implements TraceService {

    @Autowired
    private TraceMapper traceMapper;

    @Override
    public void collect(TraceInfo traceInfo) {
        Long start = traceInfo.getStart();
        Long end = traceInfo.getEnd();
        String startTime = LocalDateTimeUtil.of(start).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN));
        String endTime = LocalDateTimeUtil.of(end).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN));
        traceInfo.setStartTime(startTime);
        traceInfo.setEndTime(endTime);
        String indexName = LogConst.TRACE_INDEX_PREFIX + "_" + startTime.substring(0, 10);
        String id = IdUtil.getSnowflakeNextIdStr();
        traceInfo.setId(id);
        traceMapper.insert(traceInfo, indexName);
    }


    public List<TraceInfo> getByTraceId(String traceId) {
        if (StrUtil.isBlank(traceId)) {
            return Collections.emptyList();
        }
        LambdaEsQueryWrapper<TraceInfo> wrapper = EsWrappers.lambdaQuery(TraceInfo.class);
        wrapper.index(LogConst.TRACE_INDEX_PREFIX + "*");
        wrapper.eq(TraceInfo::getTraceId, traceId);
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setSort(SortOrder.DESC.name());
        orderByParam.setOrder(LogConst.SPAN_ID_KEY + ".keyword");
        return traceMapper.selectList(wrapper);
    }

}
