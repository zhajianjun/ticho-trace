package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.valid.ValidGroup;
import com.ticho.boot.web.util.valid.ValidUtil;
import com.ticho.trace.server.application.service.SystemService;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.infrastructure.core.enums.UserStatus;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.interfaces.assembler.SystemAssembler;
import com.ticho.trace.server.interfaces.dto.SystemDTO;
import com.ticho.trace.server.interfaces.query.SystemQuery;
import com.ticho.trace.server.interfaces.vo.SystemVO;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
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

    @Autowired
    private StringEncryptor stringEncryptor;

    @Override
    public void save(SystemDTO systemDTO) {
        ValidUtil.valid(systemDTO, ValidGroup.Add.class);
        String systemId = systemDTO.getSystemId();
        SystemBO select = systemRepository.getBySystemId(systemId);
        Assert.isNull(select, BizErrCode.FAIL, "系统已存在");
        SystemBO systemBO = SystemAssembler.INSTANCE.dtoToSystem(systemDTO);
        String secret = systemBO.getSecret();
        secret = StrUtil.isBlank(secret) ? systemId : secret;
        LocalDateTime now = LocalDateTime.now();
        systemBO.setId(IdUtil.getSnowflakeNextIdStr());
        systemBO.setSecret(stringEncryptor.encrypt(secret));
        systemBO.setStatus(UserStatus.NORMAL.code());
        systemBO.setCreateBy(null);
        systemBO.setCreateTime(now);
        systemBO.setUpdateBy(null);
        systemBO.setCreateTime(now);
        Assert.isTrue(systemRepository.save(systemBO), BizErrCode.FAIL, "保存失败");
    }

    @Override
    public void updateStatus(String systemId, Integer status) {
        Assert.isTrue(systemRepository.updateStatus(systemId, status), BizErrCode.FAIL, "更新失败");
    }

    @Override
    public void updateSecret(String systemId, String secret) {
        String encrypt = stringEncryptor.encrypt(secret);
        Assert.isTrue(systemRepository.updateSecret(systemId, encrypt), BizErrCode.FAIL, "更新失败");
    }

    @Override
    public void updateById(SystemDTO systemDTO) {
        ValidUtil.valid(systemDTO, ValidGroup.Upd.class);
        String id = systemDTO.getId();
        SystemBO byId = systemRepository.getById(id);
        Assert.isNotNull(byId, BizErrCode.FAIL, "用户不存在");
        SystemBO systemBO = SystemAssembler.INSTANCE.dtoToSystem(systemDTO);
        // 系统id和秘钥不能更新
        systemBO.setSystemId(null);
        systemBO.setSecret(null);
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
    public SystemVO getCacheBySecret(String secret) {
        SystemBO systemBO = systemRepository.getCacheBySecret(secret);
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

    @Override
    public List<SystemVO> listAll() {
        // @formatter:off
        return systemRepository.list()
            .stream()
            .map(SystemAssembler.INSTANCE::systemToVO)
            .collect(Collectors.toList());
        // @formatter:on
    }

}
