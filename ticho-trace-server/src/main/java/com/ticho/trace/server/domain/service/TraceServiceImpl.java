package com.ticho.trace.server.domain.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.server.application.service.TraceService;
import com.ticho.trace.server.domain.repository.SystemRepository;
import com.ticho.trace.server.domain.repository.TraceRepository;
import com.ticho.trace.server.domain.service.handle.SecretHandle;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.infrastructure.entity.TraceBO;
import com.ticho.trace.server.interfaces.assembler.TraceAssembler;
import com.ticho.trace.server.interfaces.dto.TraceDTO;
import com.ticho.trace.server.interfaces.vo.TraceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 链路收集接口实现
 *
 * @author zhajianjun
 * @date 2023-04-02 16:00:16
 */
@Service
@Slf4j
public class TraceServiceImpl extends SecretHandle implements TraceService {

    @Autowired
    private TraceRepository traceRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Override
    public void collect(String secret, TraceDTO traceDto) {
        String systemId = getSystemIdAndCheck(secret);
        if (traceDto.getStart() == null) {
            log.warn("链路格式异常，start开始时间戳不存在");
            return;
        }
        TraceBO trace = TraceAssembler.INSTANCE.dtoToTrace(traceDto);
        LocalDateTime startTime = trace.getStartTime();
        String indexName = LogConst.TRACE_INDEX_PREFIX + "_" + startTime.toString().substring(0, 10);
        String id = IdUtil.getSnowflakeNextIdStr();
        trace.setId(id);
        trace.setCreateTime(LocalDateTime.now());
        trace.setSystemId(systemId);
        traceRepository.save(trace, indexName);
    }


    public List<TraceVO> getByTraceId(String traceId) {
        // @formatter:off
        if (StrUtil.isBlank(traceId)) {
            return Collections.emptyList();
        }
        List<TraceBO> traceBos = traceRepository.selectByTraceId(traceId);
        List<String> systemIds = traceBos.stream().map(TraceBO::getSystemId).collect(Collectors.toList());
        Map<String, SystemBO> systemMap = systemRepository.getMapBySystemIds(systemIds);
        return traceBos
            .stream()
            .map(TraceAssembler.INSTANCE::traceToVo)
            .peek(x-> setSystemInfo(x, systemMap))
            .collect(Collectors.toList());
        // @formatter:on
    }

    /**
     * 系统信息注入
     *
     * @param traceVO 链路信息
     * @param systemMap 系统map
     */
    private void setSystemInfo(TraceVO traceVO, Map<String, SystemBO> systemMap) {
        if (Objects.isNull(traceVO)) {
            return;
        }
        String systemId = traceVO.getSystemId();
        SystemBO systemBO = systemMap.get(systemId);
        if (Objects.isNull(systemBO)) {
            return;
        }
        traceVO.setSystemName(systemBO.getSystemName());
    }

}
