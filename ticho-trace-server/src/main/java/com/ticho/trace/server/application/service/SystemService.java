package com.ticho.trace.server.application.service;

import com.ticho.boot.view.core.PageResult;
import com.ticho.trace.server.interfaces.dto.SystemDTO;
import com.ticho.trace.server.interfaces.query.SystemQuery;
import com.ticho.trace.server.interfaces.vo.SystemVO;

import java.io.Serializable;

/**
 * 系统信息接口
 *
 * @author zhajianjun
 * @date 2023-04-23 20:50:39
 */
public interface SystemService {

    /**
     * 保存系统信息
     *
     * @param systemDTO 系统信息DTO 对象
     */
    void save(SystemDTO systemDTO);

    /**
     * 系统信息状态更改
     *
     * @param systemId systemId
     * @param status   状态
     */
    void updateStatus(String systemId, Integer status);

    /**
     * 系统秘钥更新
     *
     * @param systemId 系统标识
     * @param secret   秘钥
     */
    void updateSecret(String systemId, String secret);

    /**
     * 修改系统信息
     *
     * @param systemDTO 系统信息DTO 对象
     */
    void updateById(SystemDTO systemDTO);

    /**
     * 根据id查询系统信息
     *
     * @param id 主键
     * @return {@link SystemVO}
     */
    SystemVO getById(Serializable id);

    /**
     * 根据秘钥查询系统信息
     *
     * @param secret 秘钥
     * @return {@link SystemVO}
     */
    SystemVO getCacheBySecret(String secret);

    /**
     * 分页查询系统信息列表
     *
     * @param query 查询
     * @return {@link PageResult}<{@link SystemVO}>
     */
    PageResult<SystemVO> page(SystemQuery query);

}
