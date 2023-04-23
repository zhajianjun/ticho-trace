package com.ticho.trace.server.infrastructure.mapper;

import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import org.springframework.stereotype.Repository;

/**
 * 系统信息 mapper
 *
 * @author zhajianjun
 * @date 2023-04-23 20:48:52
 */
@Repository
public interface SystemMapper extends BaseEsMapper<SystemBO> {
}
