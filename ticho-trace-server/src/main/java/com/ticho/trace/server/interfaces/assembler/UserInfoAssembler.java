package com.ticho.trace.server.interfaces.assembler;

import com.ticho.trace.server.infrastructure.entity.UserInfo;
import com.ticho.trace.server.interfaces.dto.UserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 用户信息 转换
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Mapper
public interface UserInfoAssembler {
    UserInfoAssembler INSTANCE = Mappers.getMapper(UserInfoAssembler.class);

    /**
     * 用户信息
     *
     * @param dto 用户信息DTO
     * @return {@link UserInfo}
     */
    UserInfo dtoToEntity(UserInfoDTO dto);

    /**
     * 用户信息DTO
     *
     * @param entity 用户信息
     * @return {@link UserInfoDTO}
     */
    UserInfoDTO entityToDto(UserInfo entity);

}