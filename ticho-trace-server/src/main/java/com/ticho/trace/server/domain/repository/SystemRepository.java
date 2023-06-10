package com.ticho.trace.server.domain.repository;

import cn.easyes.core.biz.EsPageInfo;
import com.ticho.boot.es.service.BaseEsService;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.interfaces.query.SystemQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统信息 repository接口
 *
 * @author zhajianjun
 * @date 2023-04-26 13:39
 */
public interface SystemRepository extends BaseEsService<SystemBO> {

    /**
     * 通过系统编号更新状态
     *
     * @param systemId 系统编号
     * @param status   状态
     * @return boolean
     */
    boolean updateStatus(String systemId, Integer status);

    /**
     * 通过系统编号更新秘钥
     *
     * @param systemId 系统编号
     * @param secret   秘钥
     * @return boolean
     */
    boolean updateSecret(String systemId, String secret);

    /**
     * 根据系统id查询
     *
     * @param systemId 系统id
     * @return {@link SystemBO}
     */
    SystemBO getBySystemId(String systemId);

    /**
     * 根据秘钥查询
     *
     * @param secret 秘钥
     * @return {@link SystemBO}
     */
    SystemBO getBySecret(String secret);

    /**
     * 根据秘钥查询
     *
     * @param secret 秘钥
     * @return {@link SystemBO}
     */
    SystemBO getCacheBySecret(String secret);

    /**
     * 根据条件查询query列表
     *
     * @param query      查询条件
     * @param indexNames 索引名称
     * @return {@link EsPageInfo}<{@link SystemBO}>
     */
    EsPageInfo<SystemBO> page(SystemQuery query, String... indexNames);

    /**
     * 根据系统id列表查询
     *
     * @param systemIds 系统id列表
     * @return {@link List}<{@link SystemBO}>
     */
    List<SystemBO> listBySystemIds(Collection<String> systemIds);

    /**
     * 根据系统id列表查询Map
     *
     * @param systemIds 系统id列表
     * @return {@link Map}<{@link String}, {@link SystemBO}>
     */
    Map<String, SystemBO> getMapBySystemIds(Collection<String> systemIds);

    /**
     * 查询所有
     *
     * @return {@link List}<{@link SystemBO}>
     */
    List<SystemBO> listAll();

}
