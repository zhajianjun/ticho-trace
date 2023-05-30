package com.ticho.trace.server.interfaces.assembler;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.ticho.trace.server.infrastructure.entity.TraceBO;
import com.ticho.trace.server.interfaces.dto.TraceDTO;
import com.ticho.trace.server.interfaces.vo.TraceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

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
     * @return {@link TraceBO}
     */
    @Mapping(target = "startTime", expression = "java(TraceAssembler.getTime(traceDto.getStart()))")
    @Mapping(target = "endTime", expression = "java(TraceAssembler.getTime(traceDto.getEnd()))")
    @Mapping(target = "createTime", ignore = true)
    TraceBO dtoToTrace(TraceDTO traceDto);

    /**
     * trace转Dto
     *
     * @param trace 跟踪
     * @return {@link TraceDTO}
     */
    TraceVO traceToVo(TraceBO trace);


    static LocalDateTime getTime(Long time) {
        if (time == null) {
            return null;
        }
        return LocalDateTimeUtil.of(time);
    }

}
