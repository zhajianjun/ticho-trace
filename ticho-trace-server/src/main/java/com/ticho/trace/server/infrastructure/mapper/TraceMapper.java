package com.ticho.trace.server.infrastructure.mapper;

import cn.easyes.core.core.BaseEsMapper;
import com.ticho.trace.server.infrastructure.entity.TraceBO;
import org.springframework.stereotype.Repository;

/**
 * 链路收集信息 mapper
 *
 * @author zhajianjun
 * @date 2023-04-02 01:40:48
 */
@Repository
public interface TraceMapper extends BaseEsMapper<TraceBO> {
}