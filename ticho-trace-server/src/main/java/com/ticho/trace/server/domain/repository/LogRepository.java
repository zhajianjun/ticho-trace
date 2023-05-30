package com.ticho.trace.server.domain.repository;

import cn.easyes.core.biz.EsPageInfo;
import com.ticho.boot.es.service.BaseEsService;
import com.ticho.trace.server.infrastructure.entity.LogBO;
import com.ticho.trace.server.interfaces.query.LogQuery;

/**
 * 日志收集信息 repository接口
 *
 * @author zhajianjun
 * @date 2023-04-25 21:56:1
 */
public interface LogRepository extends BaseEsService<LogBO> {

    /**
     * 根据条件查询query列表
     *
     * @param query      查询条件
     * @param indexNames 索引名称
     * @return {@link EsPageInfo}<{@link LogBO}>
     */
    EsPageInfo<LogBO> page(LogQuery query, String... indexNames);

}
