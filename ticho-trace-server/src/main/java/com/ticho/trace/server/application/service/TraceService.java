package com.ticho.trace.server.application.service;

import com.ticho.trace.server.interfaces.dto.TraceDTO;
import com.ticho.trace.server.interfaces.vo.TraceVO;

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
     * @param trace 跟踪收集信息
     */
    void collect(TraceDTO trace);

    /**
     * 通过traceId查询链路信息
     *
     * @param traceId 跟踪id
     * @return {@link List}<{@link TraceVO}>
     */
    List<TraceVO> getByTraceId(String traceId);

}
