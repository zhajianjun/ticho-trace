package com.ticho.trace.server.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.ticho.boot.es.component.EsTemplate;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.trace.common.bean.LogInfo;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.server.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.format.DateTimeFormatter;
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
    public void collect(@RequestBody List<LogInfo> logs) {
        // @formatter:off
        Map<String, List<Map<String, Object>>> collect = logs
            .stream()
            .filter(this::checkFormat)
            .collect(Collectors.groupingBy(this::index, Collectors.mapping(this::convert, Collectors.toList())));
        collect.forEach((k, v) -> esTemplate.saveBatchForMap(null, k, v));
        // @formatter:on
    }

    /**
     * 根据日志的时间获取索引
     *
     * @param logInfo 日志信息
     * @return {@link String}
     */
    public String index(LogInfo logInfo) {
        // @formatter:off
        Long dtTime = logInfo.getDtTime();
        String dateTime = LocalDateTimeUtil.of(dtTime).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN));
        logInfo.setDateTime(dateTime);
        return LogConst.LOG_INDEX_PREFIX + "_" + dateTime.substring(0, 10);
        // @formatter:on
    }

    private Map<String, Object> convert(LogInfo logInfo) {
        String id = IdUtil.getSnowflakeNextIdStr();
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.id(id);
        Map<String, Object> logMap = JsonUtil.toMap(logInfo);
        // 移除mdc信息
        logMap.remove(LogConst.MDC_KEY);
        return logMap;
    }

    /**
     * 检查格式
     *
     * @param logInfo 日志
     * @return boolean
     */
    private boolean checkFormat(LogInfo logInfo) {
        // checkFormat
        Long dtTime = logInfo.getDtTime();
        if (dtTime == null) {
            log.warn("日志格式异常，dtTime不存在");
            return false;
        }
        return true;
    }

}

