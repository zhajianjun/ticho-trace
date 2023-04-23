package com.ticho.trace.server.interfaces.assembler;

import cn.hutool.core.date.DatePattern;
import com.ticho.trace.server.infrastructure.entity.LogBO;
import com.ticho.trace.server.interfaces.dto.LogDTO;
import com.ticho.trace.server.interfaces.vo.LogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 日志信息转换
 *
 * @author zhajianjun
 * @date 2023-04-23 14:44
 */
@Mapper(imports = {LogAssembler.class})
public interface LogAssembler {
    LogAssembler INSTANCE = Mappers.getMapper(LogAssembler.class);


    /**
     * dto转trace
     *
     * @param logDTO 日志dto
     * @return {@link LogBO}
     */
    @Mapping(target = "systemId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    LogBO dtoToLog(LogDTO logDTO);

    /**
     * 日志转Vo
     *
     * @param trace 日志信息
     * @return {@link LogDTO}
     */
    LogVO logToVO(LogBO trace);

}
