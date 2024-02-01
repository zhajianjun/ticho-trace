package top.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ticho.boot.view.core.PageResult;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.util.Assert;
import top.ticho.boot.web.util.valid.ValidUtil;
import top.ticho.tool.trace.common.constant.LogConst;
import top.ticho.trace.server.application.service.TraceService;
import top.ticho.trace.server.domain.handle.CommonHandle;
import top.ticho.trace.server.domain.repository.SystemRepository;
import top.ticho.trace.server.domain.repository.TraceRepository;
import top.ticho.trace.server.infrastructure.entity.SystemBO;
import top.ticho.trace.server.infrastructure.entity.TraceBO;
import top.ticho.trace.server.interfaces.assembler.TraceAssembler;
import top.ticho.trace.server.interfaces.dto.TraceDTO;
import top.ticho.trace.server.interfaces.query.TraceQuery;
import top.ticho.trace.server.interfaces.vo.TraceVO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
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
public class TraceServiceImpl extends CommonHandle implements TraceService {

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
        List<TraceBO> traceBos = traceRepository.selectByTraceId(traceId, LogConst.TRACE_INDEX_PREFIX + "*");
        List<String> systemIds = traceBos.stream().map(TraceBO::getSystemId).collect(Collectors.toList());
        Map<String, SystemBO> systemMap = systemRepository.getMapBySystemIds(systemIds);
        return traceBos
            .stream()
            .map(TraceAssembler.INSTANCE::traceToVo)
            .peek(x-> setSystemInfo(x, systemMap))
            .collect(Collectors.toList());
        // @formatter:on
    }

    @Override
    public PageResult<TraceVO> page(TraceQuery query) {
        // @formatter:off
        ValidUtil.valid(query);
        checkDate(query);
        checkCurUserSystemId(query.getSystemId());
        EsPageInfo<TraceBO> page = traceRepository.page(query, LogConst.TRACE_INDEX_PREFIX + "*");
        List<TraceBO> traceBos = page.getList();
        List<String> systemIds = traceBos
            .stream()
            .map(TraceBO::getSystemId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, SystemBO> systemMap = systemRepository.getMapBySystemIds(systemIds);
        List<TraceVO> traceVos= traceBos
            .stream()
            .map(TraceAssembler.INSTANCE::traceToVo)
            .peek(x-> setSystemInfo(x, systemMap))
            .collect(Collectors.toList());
        return new PageResult<>(query.getPageNum(), query.getPageSize(), page.getTotal(), traceVos);
        // @formatter:on
    }

    /**
     * 检查日期
     *
     * @param logQuery 日志查询
     */
    private void checkDate(TraceQuery logQuery) {
        LocalDateTime startDateTime = logQuery.getStartTimeFirst();
        LocalDateTime endDateTime = logQuery.getStartTimeLast();
        // 因为时间精确到毫秒，如果结束时间的毫秒数等于0，则毫秒数增加到最大值
        int i = endDateTime.get(ChronoField.MILLI_OF_SECOND);
        if (i == 0) {
            endDateTime = endDateTime.plus(999, ChronoUnit.MILLIS);
        }
        // 日志开始时间要小于结束时间
        boolean before = !startDateTime.isAfter(endDateTime);
        Assert.isTrue(before, BizErrCode.PARAM_ERROR, "链路开始时间要小于等于结束时间");
        // 日志时间间隔不能超过7天
        boolean between = LocalDateTimeUtil.between(startDateTime, endDateTime, ChronoUnit.DAYS) <= 7;
        Assert.isTrue(between, BizErrCode.PARAM_ERROR, "链路时间间隔不能超过7天");
    }

    /**
     * 系统信息注入
     *
     * @param traceVO   链路信息
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
