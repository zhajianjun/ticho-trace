package com.ticho.trace.server.infrastructure.mapper;

import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import com.ticho.trace.server.infrastructure.entity.UserBO;
import org.springframework.stereotype.Repository;


/**
 * 用户信息 mapper
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Repository
public interface UserMapper extends BaseEsMapper<UserBO> {

}