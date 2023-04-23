package com.ticho.trace.server.interfaces.assembler;

import com.ticho.trace.server.infrastructure.entity.LogBO;
import com.ticho.trace.server.infrastructure.entity.SystemBO;
import com.ticho.trace.server.interfaces.dto.SystemDTO;
import com.ticho.trace.server.interfaces.vo.SystemVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 日志信息转换
 *
 * @author zhajianjun
 * @date 2023-04-23 14:44
 */
@Mapper
public interface SystemAssembler {
    SystemAssembler INSTANCE = Mappers.getMapper(SystemAssembler.class);


    /**
     * dto转系统信息
     *
     * @param systemDTO 系统信息dto
     * @return {@link LogBO}
     */
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    SystemBO dtoToSystem(SystemDTO systemDTO);

    /**
     * 系统信息转Vo
     *
     * @param systemBO 系统信息
     * @return {@link SystemVO}
     */
    SystemVO systemToVO(SystemBO systemBO);

}