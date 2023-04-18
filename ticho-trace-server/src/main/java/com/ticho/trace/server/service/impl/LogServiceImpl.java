package com.ticho.trace.server.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.es.component.EsTemplate;
import com.ticho.boot.es.query.EsQuery;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.EsPageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.valid.ValidUtil;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.server.dto.LogDTO;
import com.ticho.trace.server.query.LogQuery;
import com.ticho.trace.server.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志服务 实现
 *
 * @author zhajianjun
 * @date 2023-04-02 11:30:26
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    private EsTemplate esTemplate;

    @Override
    public void collect(@RequestBody List<LogDTO> logs) {
        // @formatter:off
        Map<String, List<Map<String, Object>>> collect = logs
            .stream()
            .filter(this::checkFormat)
            .collect(Collectors.groupingBy(this::index, Collectors.mapping(this::convert, Collectors.toList())));
        collect.forEach((k, v) -> esTemplate.saveBatchForMap(k, v));
        // @formatter:on
    }

    @Override
    public EsPageResult<Map<String, Object>> page(LogQuery logQuery) {
        ValidUtil.valid(logQuery);
        checkDate(logQuery);
        List<String> indexs = getIndexs(logQuery);
        QueryBuilder queryBuilder = getQueryBuilder(logQuery);
        EsQuery<Map<String, Object>> query = new EsQuery<>();
        query.setQueryBuilder(queryBuilder);
        query.setPageSize(logQuery.getPageSize());
        query.setPageNum(logQuery.getPageNum());
        query.setIndexs(indexs);
        // 显示的字段
        query.setFields(null);
        query.setSortFields(logQuery.getSortFields());
        query.setSortOrders(logQuery.getSortOrders());
        return esTemplate.pageForMap(query);
    }

    /**
     * 根据日志时间区间获取索引
     *
     * @param logQuery 日志查询
     * @return {@link List}<{@link String}>
     */
    private List<String> getIndexs(LogQuery logQuery) {
        LocalDateTime startDateTime = logQuery.getStartDateTime();
        LocalDateTime endDateTime = logQuery.getEndDateTime();
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        List<String> indexs = new ArrayList<>();
        addIndexs(startDate, endDate, indexs);
        return indexs;
    }

    /**
     * 检查日期
     *
     * @param logQuery 日志查询
     */
    private void checkDate(LogQuery logQuery) {
        LocalDateTime startDateTime = logQuery.getStartDateTime();
        LocalDateTime endDateTime = logQuery.getEndDateTime();
        // 日志开始时间要小于结束时间
        boolean before = startDateTime.compareTo(endDateTime) <= 0;
        Assert.isTrue(before, BizErrCode.PARAM_ERROR, "日志开始时间要小于等于结束时间");
        // 日志时间间隔不能超过7天
        boolean between = LocalDateTimeUtil.between(startDateTime, endDateTime, ChronoUnit.DAYS) <= 7;
        Assert.isTrue(between, BizErrCode.PARAM_ERROR, "日志时间间隔不能超过7天");
    }

    /**
     * 递归添加索引到列表中
     * 当开始日期累加到大于结束日期则结束
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param indexs 索引
     */
    public void addIndexs(LocalDate startDate, LocalDate endDate, List<String> indexs) {
        if (startDate.isAfter(endDate)) {
            return;
        }
        String date = startDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)) + "*";
        indexs.add(parseIndex(date));
        startDate = startDate.plusDays(1);
        addIndexs(startDate, endDate, indexs);
    }

    private QueryBuilder getQueryBuilder(LogQuery logQuery) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        String traceId = logQuery.getTraceId();
        String spanId = logQuery.getSpanId();
        String appName = logQuery.getAppName();
        String env = logQuery.getEnv();
        String ip = logQuery.getIp();
        String preAppName = logQuery.getPreAppName();
        String preIp = logQuery.getPreIp();
        String logLevel = logQuery.getLogLevel();
        LocalDateTime startDateTime = logQuery.getStartDateTime();
        LocalDateTime endDateTime = logQuery.getEndDateTime();
        String className = logQuery.getClassName();
        String method = logQuery.getMethod();
        String content = logQuery.getContent();
        if (StrUtil.isNotBlank(traceId)) {
            queryBuilder.must(QueryBuilders.matchQuery("traceId", traceId));
        }
        if (StrUtil.isNotBlank(spanId)) {
            queryBuilder.must(QueryBuilders.matchQuery("spanId", spanId));
        }
        if (StrUtil.isNotBlank(appName)) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("appName", appName));
        }
        if (StrUtil.isNotBlank(env)) {
            queryBuilder.must(QueryBuilders.matchQuery("env", env));
        }
        if (StrUtil.isNotBlank(ip)) {
            queryBuilder.must(QueryBuilders.matchQuery("ip", ip));
        }
        if (StrUtil.isNotBlank(preAppName)) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("preAppName", preAppName));
        }
        if (StrUtil.isNotBlank(preIp)) {
            queryBuilder.must(QueryBuilders.matchQuery("preIp", preIp));
        }
        if (StrUtil.isNotBlank(logLevel)) {
            queryBuilder.must(QueryBuilders.matchQuery("logLevel", logLevel));
        }
        if (StrUtil.isNotBlank(className)) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("className", className));
        }
        if (StrUtil.isNotBlank(method)) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("method", method));
        }
        if (StrUtil.isNotBlank(content)) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery("content", content));
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("dtTime");
        rangeQueryBuilder.gte(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        rangeQueryBuilder.lte(endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        queryBuilder.must(rangeQueryBuilder);
        return queryBuilder;
    }

    /**
     * 根据日志的时间获取索引
     *
     * @param logDTO 日志信息
     * @return {@link String}
     */
    public String index(LogDTO logDTO) {
        // @formatter:off
        Long dtTime = logDTO.getDtTime();
        String dateTime = LocalDateTimeUtil.of(dtTime).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN));
        logDTO.setDateTime(dateTime);
        return parseIndex(dateTime.substring(0, 10));
        // @formatter:on
    }

    public String parseIndex(String date) {
        return LogConst.LOG_INDEX_PREFIX + "_" + date;
    }

    private Map<String, Object> convert(LogDTO logDTO) {
        String id = IdUtil.getSnowflakeNextIdStr();
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.id(id);
        Map<String, Object> logMap = JsonUtil.toMap(logDTO);
        // 移除mdc信息
        logMap.remove(LogConst.MDC_KEY);
        return logMap;
    }

    /**
     * 检查格式
     *
     * @param logDTO 日志
     * @return boolean
     */
    private boolean checkFormat(LogDTO logDTO) {
        // checkFormat
        Long dtTime = logDTO.getDtTime();
        if (dtTime == null) {
            log.warn("日志格式异常，dtTime不存在");
            return false;
        }
        return true;
    }

}

