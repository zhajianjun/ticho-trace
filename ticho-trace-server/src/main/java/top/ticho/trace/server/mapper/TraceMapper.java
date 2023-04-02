package top.ticho.trace.server.mapper;

import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import org.springframework.stereotype.Repository;
import top.ticho.trace.server.entity.TraceInfo;

/**
 * 链路收集信息 mapper
 *
 * @author zhajianjun
 * @date 2023-04-02 01:40:48
 */
@Repository
public interface TraceMapper extends BaseEsMapper<TraceInfo> {
}