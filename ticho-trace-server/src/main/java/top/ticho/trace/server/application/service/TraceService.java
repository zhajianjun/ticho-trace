package top.ticho.trace.server.application.service;

import cn.easyes.core.biz.EsPageInfo;
import top.ticho.boot.view.core.PageResult;
import top.ticho.trace.server.infrastructure.entity.TraceBO;
import top.ticho.trace.server.interfaces.dto.TraceDTO;
import top.ticho.trace.server.interfaces.query.TraceQuery;
import top.ticho.trace.server.interfaces.vo.TraceVO;

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
     * @param secret 秘钥
     * @param trace  跟踪收集信息
     */
    void collect(String secret, TraceDTO trace);

    /**
     * 通过traceId查询链路信息
     *
     * @param traceId 跟踪id
     * @return {@link List}<{@link TraceVO}>
     */
    List<TraceVO> getByTraceId(String traceId);

    /**
     * 分页查询
     *
     * @param query 查询
     * @return {@link EsPageInfo}<{@link TraceBO}>
     */
    PageResult<TraceVO> page(TraceQuery query);

}
