package com.ticho.trace.server.service;

import com.ticho.boot.view.core.EsPageResult;
import com.ticho.trace.common.bean.LogInfo;
import com.ticho.trace.server.dto.LogDTO;
import com.ticho.trace.server.query.LogQuery;

import java.util.List;
import java.util.Map;

/**
 * 日志服务 接口
 *
 * @author zhajianjun
 * @date 2023-04-02 11:29:59
 */
public interface LogService {


    /**
     * 日志收集
     *
     * @param logs 日志
     */
    void collect(List<LogDTO> logs);

    /**
     * 分页查询
     *
     * @param logQuery 日志查询
     * @return {@link EsPageResult}<{@link Map}<{@link String}, {@link Object}>>
     */
    EsPageResult<Map<String, Object>> page(LogQuery logQuery);

}
