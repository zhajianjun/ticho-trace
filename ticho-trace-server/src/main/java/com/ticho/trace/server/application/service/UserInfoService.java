package com.ticho.trace.server.application.service;

import com.ticho.boot.view.core.PageResult;
import com.ticho.trace.server.interfaces.dto.UserInfoDTO;
import com.ticho.trace.server.interfaces.query.UserInfoQuery;

import java.io.Serializable;

/**
 * 用户信息 服务接口
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
public interface UserInfoService {
    /**
     * 保存用户信息
     *
     * @param userInfoDTO 用户信息DTO 对象
     */
    void save(UserInfoDTO userInfoDTO);

    /**
     * 删除用户信息
     *
     * @param id 主键
     */
    void removeById(Serializable id);

    /**
     * 修改用户信息
     *
     * @param userInfoDTO 用户信息DTO 对象
     */
    void updateById(UserInfoDTO userInfoDTO);

    /**
     * 根据id查询用户信息
     *
     * @param id 主键
     * @return {@link UserInfoDTO}
     */
    UserInfoDTO getById(Serializable id);

    /**
     * 分页查询用户信息列表
     *
     * @param query 查询
     * @return {@link PageResult}<{@link UserInfoDTO}>
     */
    PageResult<UserInfoDTO> page(UserInfoQuery query);

}

