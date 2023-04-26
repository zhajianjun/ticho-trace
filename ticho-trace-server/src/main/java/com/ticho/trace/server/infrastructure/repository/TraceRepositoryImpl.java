package com.ticho.trace.server.infrastructure.repository;

import cn.easyes.core.biz.OrderByParam;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import com.ticho.boot.es.service.impl.BaseEsServiceImpl;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.server.domain.repository.TraceRepository;
import com.ticho.trace.server.infrastructure.entity.TraceBO;
import com.ticho.trace.server.infrastructure.mapper.TraceMapper;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 链路信息 repository实现
 *
 * @author zhajianjun
 * @date 2023-04-26 13:50
 */
@Repository
public class TraceRepositoryImpl extends BaseEsServiceImpl<TraceMapper, TraceBO> implements TraceRepository {

    @Override
    public List<TraceBO> selectByTraceId(String traceId) {
        LambdaEsQueryWrapper<TraceBO> wrapper = EsWrappers.lambdaQuery(TraceBO.class);
        wrapper.index(LogConst.TRACE_INDEX_PREFIX + "*");
        wrapper.eq(TraceBO::getTraceId, traceId);
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setSort(SortOrder.DESC.name());
        orderByParam.setOrder(LogConst.SPAN_ID_KEY + ".keyword");
        return baseEsMapper.selectList(wrapper);
    }

}
