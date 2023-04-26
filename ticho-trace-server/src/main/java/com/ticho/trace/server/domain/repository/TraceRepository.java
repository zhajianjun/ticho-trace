package com.ticho.trace.server.domain.repository;

import com.ticho.boot.es.service.BaseEsService;
import com.ticho.trace.server.infrastructure.entity.TraceBO;

import java.util.List;

/**
 * 链路信息 repository接口
 *
 * @author zhajianjun
 * @date 2023-04-26 13:39
 */
public interface TraceRepository extends BaseEsService<TraceBO> {

    /**
     * 根据链路id查询
     *
     * @param traceId 跟踪id
     * @return {@link List}<{@link TraceBO}>
     */
    List<TraceBO> selectByTraceId(String traceId);

}
