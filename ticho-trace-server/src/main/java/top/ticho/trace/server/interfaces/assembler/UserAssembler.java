package top.ticho.trace.server.interfaces.assembler;

import top.ticho.trace.server.infrastructure.entity.UserBO;
import top.ticho.trace.server.interfaces.dto.UserDTO;
import top.ticho.trace.server.interfaces.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 用户信息 转换
 *
 * @author zhajianjun
 * @date 2023-04-20 23:10
 */
@Mapper
public interface UserAssembler {
    UserAssembler INSTANCE = Mappers.getMapper(UserAssembler.class);

    /**
     * 用户信息
     *
     * @param dto 用户信息DTO
     * @return {@link UserBO}
     */
    UserBO dtoToEntity(UserDTO dto);

    /**
     * 用户信息DTO
     *
     * @param entity 用户信息
     * @return {@link UserDTO}
     */
    UserVO entityToVo(UserBO entity);

}