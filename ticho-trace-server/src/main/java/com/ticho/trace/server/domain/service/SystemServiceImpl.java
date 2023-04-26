package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.hutool.core.util.IdUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.trace.server.application.service.SystemService;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
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
    private SystemRepository systemRepository;

    @Override
    public void save(SystemDTO systemDTO) {
        SystemBO systemBO = SystemAssembler.INSTANCE.dtoToSystem(systemDTO);
        LocalDateTime now = LocalDateTime.now();
        systemBO.setId(IdUtil.getSnowflakeNextIdStr());
        systemBO.setCreateBy(null);
        systemBO.setCreateTime(now);
        systemBO.setUpdateBy(null);
        systemBO.setCreateTime(now);
        Assert.isTrue(systemRepository.save(systemBO), BizErrCode.FAIL, "保存失败");
    }

    @Override
    public void removeById(Serializable id) {
        Assert.isTrue(systemRepository.removeById(id), BizErrCode.FAIL, "删除失败");
    }

    @Override
    public void updateById(SystemDTO systemDTO) {
        SystemBO systemBO = SystemAssembler.INSTANCE.dtoToSystem(systemDTO);
        LocalDateTime now = LocalDateTime.now();
        systemBO.setUpdateBy(null);
        systemBO.setCreateTime(now);
        Assert.isTrue(systemRepository.updateById(systemBO), BizErrCode.FAIL, "修改失败");
    }

    @Override
    public SystemVO getById(Serializable id) {
        SystemBO systemBO = systemRepository.getById(id);
        return SystemAssembler.INSTANCE.systemToVO(systemBO);
    }

    @Override
    public PageResult<SystemVO> page(SystemQuery query) {
        // @formatter:off
        query.checkPage();
        EsPageInfo<SystemBO> page = systemRepository.page(query, systemRepository.getIndexName());
        List<SystemVO> systemDTOS = page.getList()
            .stream()
            .map(SystemAssembler.INSTANCE::systemToVO)
            .collect(Collectors.toList());
        return new PageResult<>(query.getPageNum(), query.getPageSize(), page.getTotal(), systemDTOS);
        // @formatter:on
    }


}
