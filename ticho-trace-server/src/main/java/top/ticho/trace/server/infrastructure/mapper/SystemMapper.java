package top.ticho.trace.server.infrastructure.mapper;

import cn.easyes.core.core.BaseEsMapper;
import top.ticho.trace.server.infrastructure.entity.SystemBO;
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
