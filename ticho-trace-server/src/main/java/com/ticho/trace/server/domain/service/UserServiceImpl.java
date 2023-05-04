package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.security.util.BaseUserUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.valid.ValidGroup;
import com.ticho.boot.web.util.valid.ValidUtil;
import com.ticho.trace.server.application.service.UserService;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.domain.repository.UserRepository;
import com.ticho.trace.server.infrastructure.core.constant.CommConst;
import com.ticho.trace.server.infrastructure.core.enums.UserStatus;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.infrastructure.entity.UserBO;
import com.ticho.trace.server.interfaces.assembler.SystemAssembler;
import com.ticho.trace.server.interfaces.assembler.UserAssembler;
import com.ticho.trace.server.interfaces.dto.AdminUserDTO;
import com.ticho.trace.server.interfaces.dto.SecurityUser;
import com.ticho.trace.server.interfaces.dto.UserDTO;
import com.ticho.trace.server.interfaces.dto.UserPasswordDTO;
import com.ticho.trace.server.interfaces.query.UserQuery;
import com.ticho.trace.server.interfaces.vo.SystemVO;
import com.ticho.trace.server.interfaces.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户信息 服务实现
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void adminUserInit(AdminUserDTO adminUserDTO) {
        ValidUtil.valid(adminUserDTO);
        UserBO select = userRepository.getByUsername(CommConst.ADMIN_USERNAME);
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

    public void checkSystemInfo(Collection<String> systemIds) {
        // @formatter:off
        if (CollUtil.isEmpty(systemIds)) {
            return;
        }
        List<SystemBO> systemBos = systemRepository.listBySystemIds(systemIds);
        List<String> systemIdsDb = systemBos.stream().map(SystemBO::getSystemId).distinct().collect(Collectors.toList());
        String notMatchSyetemIds = systemIds
            .stream()
            .distinct()
            .filter(x-> !systemIdsDb.contains(x))
            .collect(Collectors.joining(","));
        Assert.isBlank(notMatchSyetemIds, BizErrCode.FAIL, String.format("编号[%s]的系统信息不存在", notMatchSyetemIds));
        // @formatter:on
    }

    @Override
    public void save(UserDTO userDTO) {
        ValidUtil.valid(userDTO, ValidGroup.Add.class);
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        UserBO select = userRepository.getByUsername(username);
        Assert.isNull(select, BizErrCode.FAIL, "用户已存在");
        checkSystemInfo(userDTO.getSystemIds());
        String encode = passwordEncoder.encode(password);
        UserBO userBO = UserAssembler.INSTANCE.dtoToEntity(userDTO);
        LocalDateTime now = LocalDateTime.now();
        userBO.setId(IdUtil.getSnowflakeNextIdStr());
        userBO.setPassword(encode);
        userBO.setStatus(UserStatus.NORMAL.code());
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
        ValidUtil.valid(userDTO, ValidGroup.Upd.class);
        String id = userDTO.getId();
        UserBO byId = userRepository.getById(id);
        Assert.isNotNull(byId, BizErrCode.FAIL, "用户不存在");
        checkSystemInfo(userDTO.getSystemIds());
        UserBO userBO = UserAssembler.INSTANCE.dtoToEntity(userDTO);
        // 账户不可更改
        userBO.setUsername(null);
        LocalDateTime now = LocalDateTime.now();
        userBO.setUpdateBy(null);
        userBO.setCreateTime(now);
        Assert.isTrue(userRepository.updateById(userBO), BizErrCode.FAIL, "修改失败");
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        // @formatter:off
        ValidUtil.valid(userPasswordDTO);
        Long id = userPasswordDTO.getId();
        String password = userPasswordDTO.getPassword();
        String passwordNew = userPasswordDTO.getPasswordNew();
        UserBO queryUser = userRepository.getById(id);
        Assert.isNotEmpty(queryUser, BizErrCode.FAIL, "用户不存在");
        String encodedPassword = queryUser.getPassword();
        SecurityUser loginUser = BaseUserUtil.getCurrentUser();
        // 非管理员用户，只能修改自己的密码
        boolean isAdmin = Objects.equals(loginUser.getUsername(), CommConst.ADMIN_USERNAME);
        if (!isAdmin) {
            Assert.isTrue(Objects.equals(queryUser.getUsername(), loginUser.getUsername()), BizErrCode.FAIL, "只能修改自己的密码");
        }
        boolean matches = passwordEncoder.matches(password, encodedPassword);
        Assert.isTrue(matches, BizErrCode.FAIL, "密码错误");
        String encodedPasswordNew = passwordEncoder.encode(passwordNew);
        UserBO user = new UserBO();
        user.setId(queryUser.getId());
        user.setPassword(encodedPasswordNew);
        // 更新密码
        boolean update = userRepository.updateById(user);
        Assert.isTrue(update, BizErrCode.FAIL, "更新密码失败");
        // @formatter:on
    }

    @Override
    public UserVO getById(Serializable id) {
        UserBO userBO = userRepository.getById(id);
        UserVO userVO = UserAssembler.INSTANCE.entityToVo(userBO);
        setSystemInfo(userVO);
        return userVO;
    }

    @Override
    public UserVO getByUsername(String username) {
        UserBO userBO = userRepository.getByUsername(username);
        UserVO userVO = UserAssembler.INSTANCE.entityToVo(userBO);
        setSystemInfo(userVO);
        return userVO;
    }

    /**
     * 注入系统信息
     *
     * @param userVO 用户信息
     */
    private void setSystemInfo(UserVO userVO) {
        // @formatter:off
        if (Objects.isNull(userVO)) {
            return;
        }
        List<String> systemIds = userVO.getSystemIds();
        List<SystemBO> systemBos = systemRepository.listBySystemIds(systemIds);
        List<SystemVO> systems = systemBos
            .stream()
            .map(SystemAssembler.INSTANCE::systemToVO)
            .collect(Collectors.toList());
        userVO.setSystems(systems);
        // @formatter:on
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

}
