package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.trace.server.application.service.UserInfoService;
import com.ticho.trace.server.infrastructure.entity.UserInfo;
import com.ticho.trace.server.infrastructure.mapper.UserInfoMapper;
import com.ticho.trace.server.interfaces.assembler.UserInfoAssembler;
import com.ticho.trace.server.interfaces.dto.UserInfoDTO;
import com.ticho.trace.server.interfaces.query.UserInfoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户信息 服务实现
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfoDTO userInfoDTO) {
        UserInfo userInfo = UserInfoAssembler.INSTANCE.dtoToEntity(userInfoDTO);
        Assert.isTrue(userInfoMapper.insert(userInfo) == 1, BizErrCode.FAIL, "保存失败");
    }

    @Override
    public void removeById(Serializable id) {
        Assert.isTrue(userInfoMapper.deleteById(id) == 1, BizErrCode.FAIL, "删除失败");
    }

    @Override
    public void updateById(UserInfoDTO userInfoDTO) {
        UserInfo userInfo = UserInfoAssembler.INSTANCE.dtoToEntity(userInfoDTO);
        Assert.isTrue(userInfoMapper.updateById(userInfo) == 1, BizErrCode.FAIL, "修改失败");
    }

    @Override
    public UserInfoDTO getById(Serializable id) {
        UserInfo userInfo = userInfoMapper.selectById(id);
        return UserInfoAssembler.INSTANCE.entityToDto(userInfo);
    }

    @Override
    public PageResult<UserInfoDTO> page(UserInfoQuery query) {
        // @formatter:off
        query.checkPage();
        LambdaEsQueryWrapper<UserInfo> wrapper = EsWrappers.lambdaQuery(null);
        EsPageInfo<UserInfo> page = userInfoMapper.pageQuery(wrapper, query.getPageNum(), query.getPageSize());
        wrapper.eq(Objects.nonNull(query.getId()), UserInfo::getId, query.getId());
        wrapper.eq(StrUtil.isNotBlank(query.getUsername()), UserInfo::getUsername, query.getUsername());
        wrapper.eq(StrUtil.isNotBlank(query.getPassword()), UserInfo::getPassword, query.getPassword());
        wrapper.eq(StrUtil.isNotBlank(query.getRealname()), UserInfo::getRealname, query.getRealname());
        wrapper.eq(StrUtil.isNotBlank(query.getEmail()), UserInfo::getEmail, query.getEmail());
        wrapper.eq(StrUtil.isNotBlank(query.getMobile()), UserInfo::getMobile, query.getMobile());
        wrapper.eq(StrUtil.isNotBlank(query.getPhoto()), UserInfo::getPhoto, query.getPhoto());
        wrapper.eq(StrUtil.isNotBlank(query.getLastIp()), UserInfo::getLastIp, query.getLastIp());
        wrapper.eq(Objects.nonNull(query.getLastTime()), UserInfo::getLastTime, query.getLastTime());
        wrapper.eq(Objects.nonNull(query.getStatus()), UserInfo::getStatus, query.getStatus());
        wrapper.eq(StrUtil.isNotBlank(query.getRemark()), UserInfo::getRemark, query.getRemark());
        wrapper.eq(StrUtil.isNotBlank(query.getCreateBy()), UserInfo::getCreateBy, query.getCreateBy());
        wrapper.eq(Objects.nonNull(query.getCreateTime()), UserInfo::getCreateTime, query.getCreateTime());
        wrapper.eq(StrUtil.isNotBlank(query.getUpdateBy()), UserInfo::getUpdateBy, query.getUpdateBy());
        wrapper.eq(Objects.nonNull(query.getUpdateTime()), UserInfo::getUpdateTime, query.getUpdateTime());
        List<UserInfoDTO> userInfoDTOs = page.getList()
            .stream()
            .map(UserInfoAssembler.INSTANCE::entityToDto)
            .collect(Collectors.toList());
        return new PageResult<>(page.getPageNum(), page.getPageSize(), page.getTotal(), userInfoDTOs);
        // @formatter:on
    }
}
