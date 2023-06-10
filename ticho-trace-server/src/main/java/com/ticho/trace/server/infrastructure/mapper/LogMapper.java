package com.ticho.trace.server.infrastructure.mapper;

import cn.easyes.core.core.BaseEsMapper;
import com.ticho.trace.server.infrastructure.entity.LogBO;
import org.springframework.stereotype.Repository;

/**
 * 日志收集信息 mapper
 *
 * @author zhajianjun
 * @date 2023-04-23 14:38
 */
@Repository
public interface LogMapper extends BaseEsMapper<LogBO> {
}
