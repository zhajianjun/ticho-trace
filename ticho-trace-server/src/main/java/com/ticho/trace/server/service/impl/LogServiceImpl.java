package com.ticho.trace.server.service.impl;

import cn.easyes.core.cache.GlobalConfigCache;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.ticho.trace.common.bean.LogInfo;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.server.service.LogService;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 日志服务 实现
 *
 * @author zhajianjun
 * @date 2023-04-02 11:30:26
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Resource
    private RestHighLevelClient client;

    @Override
    public int collect(@RequestBody List<LogInfo> logs) {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(GlobalConfigCache.getGlobalConfig().getDbConfig().getRefreshPolicy().getValue());
        logs.stream().filter(this::checkFormat).forEach(entity -> handle(bulkRequest, entity));
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = this.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("日志推送es失败，{}", e.getMessage(), e);
            Assert.cast(BizErrCode.FAIL, "日志推送es异常");
        }
        assert bulkResponse != null;
        if (bulkResponse.hasFailures()) {
            log.warn("日志推送es异常，{}", bulkResponse.buildFailureMessage());
        }
        int totalSuccess = 0;
        for (BulkItemResponse next : bulkResponse) {
            if (Objects.equals(next.status(), RestStatus.CREATED)) {
                ++totalSuccess;
            }
        }
        return totalSuccess;
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

    private void handle(BulkRequest bulkRequest, LogInfo logInfo) {
        Long dtTime = logInfo.getDtTime();
        String dateTime = LocalDateTimeUtil.of(dtTime).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN));
        logInfo.setDateTime(dateTime);
        String indexName = LogConst.LOG_INDEX_PREFIX + "_" + dateTime.substring(0, 10);
        String id = IdUtil.getSnowflakeNextIdStr();
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.id(id);
        Map<String, Object> logMap = JsonUtil.toMap(logInfo);
        // 移除mdc信息
        logMap.remove(LogConst.MDC_KEY);
        indexRequest.index(indexName);
        indexRequest.source(logMap, XContentType.JSON);
        bulkRequest.add(indexRequest);
    }


}

