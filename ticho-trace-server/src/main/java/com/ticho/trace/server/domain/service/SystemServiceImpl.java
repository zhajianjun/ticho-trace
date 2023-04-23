package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.toolkit.EsWrappers;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.trace.server.application.service.SystemService;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.infrastructure.mapper.SystemMapper;
import com.ticho.trace.server.interfaces.assembler.SystemAssembler;
import com.ticho.trace.server.interfaces.dto.SystemDTO;
import com.ticho.trace.server.interfaces.query.SystemQuery;
import com.ticho.trace.server.interfaces.vo.SystemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统信息接口实现
 *
 * @author zhajianjun
 * @date 2023-04-23 20:51:16
 */
@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Autowired
    private SystemMapper systemMapper;

    @Override
    public void save(SystemDTO systemDTO) {
        SystemBO systemBO = SystemAssembler.INSTANCE.dtoToSystem(systemDTO);
        LocalDateTime now = LocalDateTime.now();
        systemBO.setId(IdUtil.getSnowflakeNextIdStr());
        systemBO.setCreateBy(null);
        systemBO.setCreateTime(now);
        systemBO.setUpdateBy(null);
        systemBO.setCreateTime(now);
        Assert.isTrue(systemMapper.insert(systemBO) == 1, BizErrCode.FAIL, "保存失败");
    }

    @Override
    public void removeById(Serializable id) {
        Assert.isTrue(systemMapper.deleteById(id) == 1, BizErrCode.FAIL, "删除失败");
    }

    @Override
    public void updateById(SystemDTO systemDTO) {
        SystemBO systemBO = SystemAssembler.INSTANCE.dtoToSystem(systemDTO);
        LocalDateTime now = LocalDateTime.now();
        systemBO.setUpdateBy(null);
        systemBO.setCreateTime(now);
        Assert.isTrue(systemMapper.updateById(systemBO) == 1, BizErrCode.FAIL, "修改失败");
    }

    @Override
    public SystemVO getById(Serializable id) {
        SystemBO systemBO = systemMapper.selectById(id);
        return SystemAssembler.INSTANCE.systemToVO(systemBO);
    }

    @Override
    public PageResult<SystemVO> page(SystemQuery query) {
        // @formatter:off
        query.checkPage();
        LambdaEsQueryWrapper<SystemBO> wrapper = EsWrappers.lambdaQuery(null);
        EsPageInfo<SystemBO> page = systemMapper.pageQuery(wrapper, query.getPageNum(), query.getPageSize());
        wrapper.eq(Objects.nonNull(query.getId()), SystemBO::getId, query.getId());
        wrapper.eq(StrUtil.isNotBlank(query.getSystemId()), SystemBO::getSystemId, query.getSystemId());
        wrapper.like(StrUtil.isNotBlank(query.getSystemName()), SystemBO::getSystemName, query.getSystemName());
        wrapper.eq(StrUtil.isNotBlank(query.getSecret()), SystemBO::getSecret, query.getSecret());
        wrapper.eq(Objects.nonNull(query.getStatus()), SystemBO::getStatus, query.getStatus());
        wrapper.like(StrUtil.isNotBlank(query.getRemark()), SystemBO::getRemark, query.getRemark());
        List<SystemVO> systemDTOS = page.getList()
            .stream()
            .map(SystemAssembler.INSTANCE::systemToVO)
            .collect(Collectors.toList());
        return new PageResult<>(query.getPageNum(), query.getPageSize(), page.getTotal(), systemDTOS);
        // @formatter:on
    }


}
