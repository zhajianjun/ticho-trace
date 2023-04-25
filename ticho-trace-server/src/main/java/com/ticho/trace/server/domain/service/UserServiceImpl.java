package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.security.constant.BaseSecurityConst;
import com.ticho.boot.security.handle.load.LoadUserService;
import com.ticho.boot.view.core.BaseSecurityUser;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.valid.ValidUtil;
import com.ticho.trace.server.application.service.UserService;
import com.ticho.trace.server.domain.repository.UserRepository;
import com.ticho.trace.server.infrastructure.core.constant.CommConst;
import com.ticho.trace.server.infrastructure.entity.UserBO;
import com.ticho.trace.server.interfaces.assembler.UserAssembler;
import com.ticho.trace.server.interfaces.dto.AdminUserDTO;
import com.ticho.trace.server.interfaces.dto.UserDTO;
import com.ticho.trace.server.interfaces.query.UserQuery;
import com.ticho.trace.server.interfaces.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息 服务实现
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Slf4j
@Service(BaseSecurityConst.LOAD_USER_TYPE_USERNAME)
public class UserServiceImpl implements UserService, LoadUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void adminUserInit(AdminUserDTO adminUserDTO) {
        ValidUtil.valid(adminUserDTO);
        UserBO select = userRepository.selectByUsername(CommConst.ADMIN_USERNAME);
        Assert.isNull(select, BizErrCode.FAIL, "管理员用户已存在");
        String password = adminUserDTO.getPassword();
        String realname = adminUserDTO.getRealname();
        realname = StrUtil.isBlank(realname) ? CommConst.ADMIN_REALNAME : realname;
        String encode = passwordEncoder.encode(password);
        LocalDateTime now = LocalDateTime.now();
        UserBO userBO = new UserBO();
        userBO.setId(IdUtil.getSnowflakeNextIdStr());
        userBO.setUsername(CommConst.ADMIN_USERNAME);
        userBO.setPassword(encode);
        userBO.setRealname(realname);
        userBO.setEmail(adminUserDTO.getEmail());
        userBO.setMobile(adminUserDTO.getMobile());
        userBO.setPhoto(adminUserDTO.getPhoto());
        userBO.setStatus(1);
        userBO.setRemark("管理员账户");
        userBO.setCreateBy(CommConst.ADMIN_USERNAME);
        userBO.setCreateTime(now);
        userBO.setUpdateBy(CommConst.ADMIN_USERNAME);
        userBO.setUpdateTime(now);
        userRepository.save(userBO);
    }

    @Override
    public void save(UserDTO userDTO) {
        UserBO userBO = UserAssembler.INSTANCE.dtoToEntity(userDTO);
        // TODO
        LocalDateTime now = LocalDateTime.now();
        userBO.setId(IdUtil.getSnowflakeNextIdStr());
        userBO.setCreateBy(null);
        userBO.setCreateTime(now);
        userBO.setUpdateBy(null);
        userBO.setCreateTime(now);
        Assert.isTrue(userRepository.save(userBO), BizErrCode.FAIL, "保存失败");
    }

    @Override
    public void removeById(Serializable id) {
        Assert.isTrue(userRepository.removeById(id), BizErrCode.FAIL, "删除失败");
    }

    @Override
    public void updateById(UserDTO userDTO) {
        UserBO userBO = UserAssembler.INSTANCE.dtoToEntity(userDTO);
        LocalDateTime now = LocalDateTime.now();
        userBO.setUpdateBy(null);
        userBO.setCreateTime(now);
        Assert.isTrue(userRepository.updateById(userBO), BizErrCode.FAIL, "修改失败");
    }

    @Override
    public UserVO getById(Serializable id) {
        UserBO userBO = userRepository.getById(id);
        return UserAssembler.INSTANCE.entityToVo(userBO);
    }

    @Override
    public UserVO getByUsername(String username) {
        UserBO userBO = userRepository.selectByUsername(username);
        return UserAssembler.INSTANCE.entityToVo(userBO);
    }

    @Override
    public PageResult<UserVO> page(UserQuery query) {
        // @formatter:off
        query.checkPage();
        EsPageInfo<UserBO> page = userRepository.page(query);
        List<UserVO> userDTOS = page.getList()
            .stream()
            .map(UserAssembler.INSTANCE::entityToVo)
            .collect(Collectors.toList());
        return new PageResult<>(query.getPageNum(), query.getPageSize(), page.getTotal(), userDTOS);
        // @formatter:on
    }

    @Override
    public BaseSecurityUser load(String username) {
        // @formatter:off
        // 用户信息校验
        UserBO user = userRepository.selectByUsername(username);
        Assert.isNotNull(user, HttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        BaseSecurityUser securityUser = new BaseSecurityUser();
        securityUser.setUsername(username);
        securityUser.setPassword(user.getPassword());
        securityUser.setRoles(Collections.singletonList("admin"));
        return securityUser;
    }

}
