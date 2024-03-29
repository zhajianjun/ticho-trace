package top.ticho.trace.server.application.service;

import top.ticho.boot.view.core.PageResult;
import top.ticho.trace.server.interfaces.dto.AdminUserDTO;
import top.ticho.trace.server.interfaces.dto.UserDTO;
import top.ticho.trace.server.interfaces.dto.UserPasswordDTO;
import top.ticho.trace.server.interfaces.query.UserQuery;
import top.ticho.trace.server.interfaces.vo.UserVO;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息 服务接口
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
public interface UserService {

    /**
     * 管理用户初始化
     *
     * @param adminUserDTO 管理用户dto
     */
    void adminUserInit(AdminUserDTO adminUserDTO);

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
    void removeById(Long id);

    /**
     * 批量删除用户信息
     *
     * @param ids id
     */
    void removeByIds(List<Long> ids);

    /**
     * 修改用户信息
     *
     * @param userDTO 用户信息DTO 对象
     */
    void updateById(UserDTO userDTO);

    /**
     * 更改用户密码
     *
     * @param userPasswordDTO 用户信息
     */
    void updatePassword(UserPasswordDTO userPasswordDTO);

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

