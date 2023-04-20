package com.ticho.trace.server.interfaces.assembler;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.trace.server.infrastructure.entity.Trace;
import com.ticho.trace.server.interfaces.dto.TraceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.format.DateTimeFormatter;

/**
 * 链路转换
 *
 * @author zhajianjun
 * @date 2023-04-18 14:29
 */
@Mapper(imports = {TraceAssembler.class})
public interface TraceAssembler {
    TraceAssembler INSTANCE = Mappers.getMapper(TraceAssembler.class);


    /**
     * dto转trace
     *
     * @param traceDto 跟踪dto
     * @return {@link Trace}
     */
    @Mapping(target = "startTime", expression = "java(TraceAssembler.getTime(traceDto.getStart()))")
    @Mapping(target = "endTime", expression = "java(TraceAssembler.getTime(traceDto.getEnd()))")
    @Mapping(target = "crtTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    Trace dtoToTrace(TraceDTO traceDto);

    /**
     * trace转Dto
     *
     * @param trace 跟踪
     * @return {@link TraceDTO}
     */
    TraceDTO traceToDto(Trace trace);


    static String getTime(Long time){
        if (time == null) {
            return StrUtil.EMPTY;
        }
        return LocalDateTimeUtil.of(time).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN));
    }

}
