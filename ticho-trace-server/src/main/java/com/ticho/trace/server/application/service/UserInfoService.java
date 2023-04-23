package com.ticho.trace.server.application.service;

import com.ticho.boot.view.core.PageResult;
import com.ticho.trace.server.interfaces.dto.UserDTO;
import com.ticho.trace.server.interfaces.query.UserQuery;
import com.ticho.trace.server.interfaces.vo.UserVO;

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
     * @param userDTO 用户信息DTO 对象
     */
    void save(UserDTO userDTO);

    /**
     * 删除用户信息
     *
     * @param id 主键
     */
    void removeById(Serializable id);

    /**
     * 修改用户信息
     *
     * @param userDTO 用户信息DTO 对象
     */
    void updateById(UserDTO userDTO);

    /**
     * 根据id查询用户信息
     *
     * @param id 主键
     * @return {@link UserVO}
     */
    UserVO getById(Serializable id);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return {@link UserVO}
     */
    UserVO getByUsername(String username);

    /**
     * 分页查询用户信息列表
     *
     * @param query 查询
     * @return {@link PageResult}<{@link UserVO}>
     */
    PageResult<UserVO> page(UserQuery query);

}

