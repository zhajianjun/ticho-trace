package com.ticho.trace.server.domain.service;

import cn.easyes.core.biz.EsPageInfo;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.PageResult;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.valid.ValidUtil;
import com.ticho.trace.common.constant.LogConst;
import com.ticho.trace.server.application.service.LogService;
import com.ticho.trace.server.domain.repository.LogRepository;
import com.ticho.trace.server.domain.service.handle.SecretHandle;
import com.ticho.trace.server.infrastructure.entity.LogBO;
import com.ticho.trace.server.interfaces.assembler.LogAssembler;
import com.ticho.trace.server.interfaces.dto.LogDTO;
import com.ticho.trace.server.interfaces.query.LogQuery;
import com.ticho.trace.server.interfaces.vo.LogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志服务 实现
 *
 * @author zhajianjun
 * @date 2023-04-02 11:30:26
 */
@Service
@Slf4j
public class LogServiceImpl extends SecretHandle implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Override
    public void collect(@RequestBody List<LogDTO> logs) {
        // @formatter:off
        String systemId = getSystemIdAndCheck();
        Map<String, List<LogBO>> collect = logs
            .stream()
            .filter(this::checkDate)
            .collect(Collectors.groupingBy(x-> index(x, systemId), Collectors.mapping(this::convert, Collectors.toList())));
        collect.forEach((k, v) -> logRepository.saveBatch(v, k));
        // @formatter:on
    }

    @Override
    public PageResult<LogVO> page(LogQuery logQuery) {
        // @formatter:off
        ValidUtil.valid(logQuery);
        checkDate(logQuery);
        String[] indexs = getIndexs(logQuery);
        EsPageInfo<LogBO> page = logRepository.page(logQuery, indexs);
        List<LogVO> logs = page.getList()
            .stream()
            .map(LogAssembler.INSTANCE::logToVO)
            .collect(Collectors.toList());
        return new PageResult<>(logQuery.getPageNum(), logQuery.getPageSize(), page.getTotal(), logs);
        // @formatter:on
    }

    /**
     * 根据日志时间区间获取索引
     *
     * @param logQuery 日志查询
     * @return {@link List}<{@link String}>
     */
    private String[] getIndexs(LogQuery logQuery) {
        LocalDateTime startDateTime = logQuery.getStartDateTime();
        LocalDateTime endDateTime = logQuery.getEndDateTime();
        LocalDate startDate = startDateTime.toLocalDate();
        String systemId = logQuery.getSystemId();
        LocalDate endDate = endDateTime.toLocalDate();
        List<String> indexs = new ArrayList<>();
        addIndexs(startDate, endDate, systemId, indexs);
        return indexs.toArray(new String[0]);
    }

    /**
     * 检查日期
     *
     * @param logQuery 日志查询
     */
    private void checkDate(LogQuery logQuery) {
        LocalDateTime startDateTime = logQuery.getStartDateTime();
        LocalDateTime endDateTime = logQuery.getEndDateTime();
        // 日志开始时间要小于结束时间
        boolean before = startDateTime.compareTo(endDateTime) <= 0;
        Assert.isTrue(before, BizErrCode.PARAM_ERROR, "日志开始时间要小于等于结束时间");
        // 日志时间间隔不能超过7天
        boolean between = LocalDateTimeUtil.between(startDateTime, endDateTime, ChronoUnit.DAYS) <= 7;
        Assert.isTrue(between, BizErrCode.PARAM_ERROR, "日志时间间隔不能超过7天");
    }

    /**
     * 递归添加索引到列表中
     * 当开始日期累加到大于结束日期则结束
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param systemId 系统id
     * @param indexs 索引
     */
    public void addIndexs(LocalDate startDate, LocalDate endDate, String systemId, List<String> indexs) {
        if (startDate.isAfter(endDate)) {
            return;
        }
        String date = startDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)) + "*";
        indexs.add(parseIndex(systemId, date));
        startDate = startDate.plusDays(1);
        addIndexs(startDate, endDate, systemId, indexs);
    }

    /**
     * 根据日志的时间获取索引
     *
     * @param logDTO 日志信息
     * @param systemId 系统id
     * @return {@link String}
     */
    public String index(LogDTO logDTO, String systemId) {
        // @formatter:off
        Long dtTime = logDTO.getDtTime();
        LocalDateTime dateTime = LocalDateTimeUtil.of(dtTime);
        logDTO.setDateTime(dateTime);
        String date = dateTime.toString().substring(0, 10);
        return parseIndex(systemId, date);
        // @formatter:on
    }

    public String parseIndex(String systemId, String date) {
        String s = "_";
        return LogConst.LOG_INDEX_PREFIX + s + systemId + s + date;
    }

    private LogBO convert(LogDTO logDTO) {
        LogBO logBO = LogAssembler.INSTANCE.dtoToLog(logDTO);
        String id = IdUtil.getSnowflakeNextIdStr();
        logBO.setId(id);
        return logBO;
    }

    /**
     * 检查格式
     *
     * @param logDTO 日志
     * @return boolean
     */
    private boolean checkDate(LogDTO logDTO) {
        // checkFormat
        Long dtTime = logDTO.getDtTime();
        if (dtTime == null) {
            log.warn("日志格式异常，dtTime不存在");
            return false;
        }
        return true;
    }

}

