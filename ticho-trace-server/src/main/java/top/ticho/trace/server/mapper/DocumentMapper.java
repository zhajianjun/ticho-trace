package top.ticho.trace.server.mapper;

import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author zhajianjun
 * @date 2023-04-02 01:40:48
 */
@Repository
public interface DocumentMapper extends BaseEsMapper<Map<String, Object>> {
}