package com.ticho.trace.server.domain.repository;

import cn.easyes.core.biz.EsPageInfo;
import com.ticho.boot.es.service.BaseEsService;
import com.ticho.trace.server.infrastructure.entity.UserBO;
import com.ticho.trace.server.interfaces.query.UserQuery;

import java.util.List;

/**
 * 用户信息 repository接口
 *
 * @author zhajianjun
 * @date 2023-04-20 23:24
 */
public interface UserRepository extends BaseEsService<UserBO> {

    /**
     * 根据条件查询query列表
     *
     * @param query 查询条件
     * @return {@link List}<{@link UserBO}>
     */
    EsPageInfo<UserBO> page(UserQuery query);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return {@link UserBO}
     */
    UserBO getByUsername(String username);

}

