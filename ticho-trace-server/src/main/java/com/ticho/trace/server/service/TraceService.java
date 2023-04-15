package com.ticho.trace.server.service;

import com.ticho.trace.server.entity.TraceInfo;

import java.util.List;

/**
 * 链路收集接口
 *
 * @author zhajianjun
 * @date 2023-04-02 15:58:58
 */
public interface TraceService {

    /**
     * 收集
     *
     * @param traceInfo 跟踪收集信息
     */
    void collect(TraceInfo traceInfo);

    /**
     * 通过traceId查询链路信息
     *
     * @param traceId 跟踪id
     * @return {@link List}<{@link TraceInfo}>
     */
    List<TraceInfo> getByTraceId(String traceId);

}
