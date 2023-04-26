package com.ticho.trace.server.domain.repository;

import cn.easyes.core.biz.EsPageInfo;
import com.ticho.boot.es.service.BaseEsService;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.interfaces.query.SystemQuery;

/**
 * 系统信息 repository接口
 *
 * @author zhajianjun
 * @date 2023-04-26 13:39
 */
public interface SystemRepository extends BaseEsService<SystemBO> {

    /**
     * 根据条件查询query列表
     *
     * @param query 查询条件
     *
     * @param indexNames 索引名称
     * @return {@link EsPageInfo}<{@link SystemBO}>
     */
    EsPageInfo<SystemBO> page(SystemQuery query, String... indexNames);

}
