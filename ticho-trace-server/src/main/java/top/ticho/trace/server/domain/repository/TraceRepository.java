package top.ticho.trace.server.domain.repository;

import cn.easyes.core.biz.EsPageInfo;
import top.ticho.boot.es.service.BaseEsService;
import top.ticho.trace.server.infrastructure.entity.TraceBO;
import top.ticho.trace.server.interfaces.query.TraceQuery;

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
     * @param indexNames 索引名称
     * @return {@link List}<{@link TraceBO}>
     */
    List<TraceBO> selectByTraceId(String traceId, String... indexNames);

    /**
     * 分页查询
     *
     * @param query      查询
     * @param indexNames 索引名称
     * @return {@link EsPageInfo}<{@link TraceBO}>
     */
    EsPageInfo<TraceBO> page(TraceQuery query, String... indexNames);

}
